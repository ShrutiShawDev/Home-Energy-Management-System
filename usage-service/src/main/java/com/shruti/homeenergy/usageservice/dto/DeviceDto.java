package com.shruti.homeenergy.usageservice.dto;

import lombok.Builder;

@Builder
public record DeviceDto(
        Long id,
        String name,
        String type,
        Long userId,
        String location,
        Double energyConsumed
) {
}
