package com.temirlan.pulsewatch.dto;

import com.temirlan.pulsewatch.enums.LogLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LogIngestionRequest {
    
    @NotNull
    private LogLevel level;
    
    @NotBlank
    private String message;

    @NotBlank
    private String service;

    @NotNull
    private Long timestamp;

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
