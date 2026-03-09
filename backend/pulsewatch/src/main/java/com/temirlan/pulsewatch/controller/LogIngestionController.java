package com.temirlan.pulsewatch.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import com.temirlan.pulsewatch.dto.LogIngestionRequest;
import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.service.LogEntryService;

@RestController
@RequestMapping("/logs")
public class LogIngestionController {
    
    private final LogEntryService logEntryService;

    public LogIngestionController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @PostMapping
    public LogEntry ingestLog(@Valid @RequestBody LogIngestionRequest request) {
        return logEntryService.saveLog(request);
    }

    @GetMapping
    public Page<LogEntry> getLogs(@RequestParam(required = false) String service, Pageable pageable) {
        
        if (service != null) {
            return logEntryService.getLogsByService(service, pageable);
        }
        
        return logEntryService.getLogs(pageable);
    }

}
