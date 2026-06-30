package com.shruti.homeenergy.usageservice.controller;

import com.shruti.homeenergy.usageservice.dto.UsageDto;
import com.shruti.homeenergy.usageservice.service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usage")
public class UsageController {

    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UsageDto> getUserDeviceUsage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "3") int days
    ){
        final UsageDto usageDto = usageService.getXDaysUsageForUser(userId, days);
        return ResponseEntity.ok(usageDto);
    }
}
