package com.shruti.homeenergy.ingestionservice.controller;


import com.shruti.homeenergy.ingestionservice.dto.EnergyUsageDto;
import com.shruti.homeenergy.ingestionservice.service.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingestion")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void ingestData(@RequestBody EnergyUsageDto usageDto){
        ingestionService.ingestEnergyUsage(usageDto);
    }
}
