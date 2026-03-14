package com.temirlan.pulsewatch.dto;

import com.temirlan.pulsewatch.enums.AlertSeverity;
import com.temirlan.pulsewatch.enums.AlertStatus;
import com.temirlan.pulsewatch.enums.AlertType;

public class AlertResponse {
    
    private Long id;
    private String service;
    private AlertType type;
    private String status;
    private String reason;
    private Long timestamp;
    private AlertStatus alertStatus;
    private AlertSeverity alertSeverity;

    public AlertResponse(Long id, String service, AlertType type, String status, String reason, Long timestamp, AlertStatus alertStatus, AlertSeverity alertSeverity) {
        this.id = id;
        this.service = service;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.timestamp = timestamp;
        this.alertStatus = alertStatus;
        this.alertSeverity = alertSeverity;
    }

    public Long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public AlertType getType() {
        return type;
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

    public AlertStatus getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(AlertStatus alertStatus) {
        this.alertStatus = alertStatus;
    }

    public AlertSeverity getAlertSeverity() {
        return alertSeverity;
    }

    public void setAlertSeverity(AlertSeverity alertSeverity) {
        this.alertSeverity = alertSeverity;
    }

}
