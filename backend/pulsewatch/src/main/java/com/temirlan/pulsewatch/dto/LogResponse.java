package com.temirlan.pulsewatch.dto;

import com.temirlan.pulsewatch.enums.LogLevel;

public class LogResponse {
    private Long id;
    private LogLevel level;
    private String message;
    private String service;
    private Long timestamp;

    public LogResponse(Long id, LogLevel level, String message, String service, Long timestamp) {
        this.id = id;
        this.level = level;
        this.message = message;
        this.service = service;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getService() {
        return service;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
}
