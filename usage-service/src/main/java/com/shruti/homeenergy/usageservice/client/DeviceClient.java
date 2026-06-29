package com.shruti.homeenergy.usageservice.client;

import com.shruti.homeenergy.usageservice.dto.DeviceDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



@Component
public class DeviceClient {

    // Spring's built-in HTTP client for making REST calls
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public DeviceClient(@Value("${device.service.url}") String baseUrl){
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    public DeviceDto getDeviceId (Long deviceId){
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/{deviceId}")
                .buildAndExpand(deviceId)
                .toUriString();

        ResponseEntity<DeviceDto> response = restTemplate.getForEntity(url, DeviceDto.class);
        return  response.getBody();
    }
}
