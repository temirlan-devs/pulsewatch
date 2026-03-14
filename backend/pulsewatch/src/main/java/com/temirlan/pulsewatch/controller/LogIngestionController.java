package com.temirlan.pulsewatch.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.dto.LogIngestionRequest;
import com.temirlan.pulsewatch.service.LogEntryService;
import com.temirlan.pulsewatch.dto.LogResponse;
import com.temirlan.pulsewatch.dto.PagedLogResponse;

@RestController
@RequestMapping("/logs")
public class LogIngestionController {

    private final LogEntryService logEntryService;

    public LogIngestionController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @PostMapping
    public LogResponse ingestLog(@Valid @RequestBody LogIngestionRequest request) {
        return logEntryService.saveLog(request);
    }

    @GetMapping
    public PagedLogResponse getLogs(
        @RequestParam(required = false) String service, 
        @RequestParam(required = false) Long from,
        @RequestParam(required = false) Long to,
        Pageable pageable
    ) {
        if (pageable.getPageSize() > 100) {
            pageable = Pageable.ofSize(100).withPage(pageable.getPageNumber());
        }
        
        return logEntryService.getLogs(service, from, to, pageable);
    }

}
