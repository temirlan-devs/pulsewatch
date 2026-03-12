package com.temirlan.pulsewatch.dto;

public class AlertResponse {
    
    private Long id;
    private String service;
    private String status;
    private String reason;
    private Long timestamp;

    public AlertResponse(Long id, String service, String status, String reason, Long timestamp) {
        this.id = id;
        this.service = service;
        this.status = status;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
