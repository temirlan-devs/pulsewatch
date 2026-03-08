package com.temirlan.pulsewatch.controller;

import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.service.LogEntryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogIngestionController {
    
    private final LogEntryService logEntryService;

    public LogIngestionController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @PostMapping
    public LogEntry ingestLog(@RequestBody LogEntry logEntry) {
        return logEntryService.saveLog(logEntry);
    }

}
