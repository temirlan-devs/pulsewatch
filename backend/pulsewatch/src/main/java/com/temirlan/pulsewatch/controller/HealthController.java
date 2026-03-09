package com.temirlan.pulsewatch.controller;

import com.temirlan.pulsewatch.service.LogEntryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    private final LogEntryService logEntryService;

    public HealthController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @GetMapping("/health")
    public String health() {

        logEntryService.createHealthLog();

        return "PulseWatch backend is running";
    }
}
