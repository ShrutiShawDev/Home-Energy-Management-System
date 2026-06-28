package com.shruti.homeenergy.ingestionservice.service;

import com.shruti.homeenergy.ingestionservice.dto.EnergyUsageDto;
import com.shruti.homeenergy.kafka.event.EnergyUsageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IngestionService {

    private final KafkaTemplate<String, EnergyUsageEvent> kafkaTemplate;

    public IngestionService(KafkaTemplate<String, EnergyUsageEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void ingestEnergyUsage(EnergyUsageDto input){
        // covert DTO to event
        EnergyUsageEvent event = EnergyUsageEvent.builder()
                .deviceId(input.deviceId())
                .energyConsumed(input.energyConsumed())
                .timestamp(input.timestamp())
                .build();

        // Send kafka to topic
        kafkaTemplate.send("energy-usage", event);
        log.info("Infested Energy Usage Event: {}", event);
    }
}
