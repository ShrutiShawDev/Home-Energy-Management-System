package com.shruti.homeenergy.usageservice.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.shruti.homeenergy.kafka.event.AlertingEvent;
import com.shruti.homeenergy.kafka.event.EnergyUsageEvent;

import com.shruti.homeenergy.usageservice.client.DeviceClient;
import com.shruti.homeenergy.usageservice.client.UserClient;
import com.shruti.homeenergy.usageservice.dto.DeviceDto;
import com.shruti.homeenergy.usageservice.dto.UserDto;
import com.shruti.homeenergy.usageservice.model.DeviceEnergy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsageService {

    private final DeviceClient deviceClient;
    private final InfluxDBClient influxDBClient;
    private final UserClient userClient;

    @Value("${influx.bucket}")
    private String influxBucket;

    @Value("${influx.org}")
    private String influxOrg;
    private final KafkaTemplate<String, AlertingEvent> kafkaTemplate;

    public UsageService(DeviceClient deviceClient,
                        InfluxDBClient influxDBClient,
                        UserClient userClient,
                        KafkaTemplate<String, AlertingEvent>  kafkaTemplate) {
        this.deviceClient = deviceClient;
        this.influxDBClient = influxDBClient;
        this.userClient = userClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "energy-usage", groupId = "usage-service")
    public void energyUsageEvent(EnergyUsageEvent energyUsageEvent){
        //log.info("Received energy usage even: {}", energyUsageEvent);

        Point point = Point.measurement("energy_usage")
                .addTag("deviceId", String.valueOf(energyUsageEvent.deviceId()))
                .addField("energyConsumed", energyUsageEvent.energyConsumed())
                .time(energyUsageEvent.timestamp(), WritePrecision.MS);

        influxDBClient.getWriteApiBlocking().writePoint(influxBucket, influxOrg, point);
    }

        // how much energy did each device consume in the past hour?
    @Scheduled(cron = "*/10 * * * * *")
    public void aggregateDeviceEnergyUsage() {

        final Instant now = Instant.now();
        final Instant oneHourAgo = now.minusSeconds(3600);

        String fluxQuery = String.format("""
        from(bucket: "%s")
            |> range(start: time(v: "%s"), stop: time(v: "%s"))
            |> filter(fn: (r) => r["_measurement"] == "energy_usage")
            |> filter(fn: (r) => r["_field"] == "energyConsumed")
            |> group(columns: ["deviceId"])
            |> sum(column: "_value")
        """,
                influxBucket,
                oneHourAgo.toString(),
                now
        );

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tabbles = queryApi.query(fluxQuery, influxOrg);

        List<DeviceEnergy> deviceEnergies = new ArrayList<>();

        for(FluxTable table : tabbles){
            for(FluxRecord record : table.getRecords()){
                String deviceIdStr = (String) record.getValueByKey("deviceId");
                Double energyConsumed = record.getValueByKey("_value") instanceof Number ?
                        ((Number) record.getValueByKey("_value")).doubleValue() : 0.0;

                deviceEnergies.add(
                        DeviceEnergy.builder()
                                .deviceId(Long.valueOf(deviceIdStr))
                                .energyConsumed(energyConsumed)
                                .build()
                );

            }
        }
        log.info("Aggregated device energies over the past hour: {}", deviceEnergies);

        for(DeviceEnergy deviceEnergy : deviceEnergies){
           try{
               final DeviceDto deviceResponse = deviceClient.getDeviceId(deviceEnergy.getDeviceId());

               if(deviceResponse == null || deviceResponse.id() == null){
                   log.warn("device not found for ID: {}", deviceEnergy.getDeviceId());
                   continue;
               }

               deviceEnergy.setUserId(deviceResponse.userId());
           }catch(Exception e){
               log.warn("Failed to fetch device for ID: {}", deviceEnergy.getDeviceId());
           }

        }

        //remove devices with null userId
        deviceEnergies.removeIf(de -> de.getUserId() == null);

        //Get user-device mapping and aggregate per user
        Map<Long, List<DeviceEnergy>> userDeviceEnergyMap = deviceEnergies.stream()
                .collect(Collectors.groupingBy(DeviceEnergy::getUserId));

        log.info("User-device Energy Map: {}", userDeviceEnergyMap);


        // get users energy consumption threshold
        List<Long> userIds = new ArrayList<>(userDeviceEnergyMap.keySet());
        final Map<Long, Double> userThresholdMap = new HashMap<>();
        final Map<Long, String> userEmailMap = new HashMap<>();

        for(final Long userId : userIds){
            try{

                UserDto user = userClient.getUserById(userId);
                if(user == null || user.id() == null || !user.alerting()){
                    log.warn("User not found or alerting disabled for ID: {}", userId);
                    continue;
                }

                userThresholdMap.put(userId, user.energyAlertingThreshold());
                userEmailMap.put(userId, user.email());
            }catch (Exception e){
                log.warn("Failed to fetch user for ID: {}", userId);
            }
        }
        log.info("User Threshold Map: {}", userThresholdMap);

        //Check threshold against aggregated usage
        final List<Long> alertedUsers = new ArrayList<>(userThresholdMap.keySet());
        for(final Long userId : alertedUsers){
            final Double threshold = userThresholdMap.get(userId);
            final List<DeviceEnergy> devices = userDeviceEnergyMap.get(userId);

            final Double totalConsumption = devices.stream()
                    .mapToDouble(DeviceEnergy::getEnergyConsumed)
                    .sum();

            if(totalConsumption > threshold){
                log.info("ALERT: USER ID {} has exceeded the energy threshold! Total Consumption: {}, Threshold: {}",
                        userId, totalConsumption, threshold);

                // Put message on kafka alert topic
                final AlertingEvent alertingEvent = AlertingEvent.builder()
                        .userId(userId)
                        .message("Energy consumption threshold exceeded")
                        .threshold(threshold)
                        .energyConsumed(totalConsumption)
                        .email(userEmailMap.get(userId))
                        .build();

                // Sending alerting to kafka topic
                kafkaTemplate.send("energy-alerts", alertingEvent).whenComplete((result, ex) -> {
                    if(ex != null) log.error("Failed to send alert for userId {}: {}", userId, ex.getMessage());
                    else log.info("Alert sent for userId {}", userId);
                });

            }else{
                log.info("User ID {} is within the energy threshold. "+ "Total Consumption: {}, Threshold: {}",
                        userId, totalConsumption, threshold);
            }
        }
    }
}
