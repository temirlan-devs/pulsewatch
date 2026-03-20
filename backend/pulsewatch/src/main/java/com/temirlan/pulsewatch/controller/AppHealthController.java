package com.temirlan.pulsewatch.controller;

import com.temirlan.pulsewatch.service.LogEntryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppHealthController {
    
    private final LogEntryService logEntryService;

    public AppHealthController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @GetMapping("/app-health")
    public String health() {

        logEntryService.createHealthLog();

        return "PulseWatch backend is running";
    }
}
