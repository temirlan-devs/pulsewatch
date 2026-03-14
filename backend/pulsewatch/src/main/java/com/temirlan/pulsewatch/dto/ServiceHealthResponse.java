package com.temirlan.pulsewatch.dto;

import com.temirlan.pulsewatch.enums.ServiceStatus;

public class ServiceHealthResponse {
    
    public String service;
    public ServiceStatus status;

    private double errorRate;
    private double averageLatency;

    private long lastMetricTimestamp;
    private long lastLogTimestamp;
    private String lastMetricTimestampReadable;
    private String lastLogTimestampReadable;

    private long openAlerts;
    private long acknowledgedAlerts;

    public ServiceHealthResponse(String service, ServiceStatus status, double errorRate, double averageLatency, long lastMetricTimestamp, long lastLogTimestamp, String lastMetricTimestampReadable, String lastLogTimestampReadable, long openAlerts, long acknowledgedAlerts) {
        this.service = service;
        this.status = status;
        this.errorRate = errorRate;
        this.averageLatency = averageLatency;
        this.lastMetricTimestamp = lastMetricTimestamp;
        this.lastLogTimestamp = lastLogTimestamp;
        this.lastMetricTimestampReadable = lastMetricTimestampReadable;
        this.lastLogTimestampReadable = lastLogTimestampReadable;
        this.openAlerts = openAlerts;
        this.acknowledgedAlerts = acknowledgedAlerts;
    }

    public String getService() {
        return service;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public double getAverageLatency() {
        return averageLatency;
    }

    public long getLastMetricTimestamp() {
        return lastMetricTimestamp;
    }

    public long getLastLogTimestamp() {
        return lastLogTimestamp;
    }

    public String getLastMetricTimestampReadable() {
        return lastMetricTimestampReadable;
    }

    public String getLastLogTimestampReadable() {
        return lastLogTimestampReadable;
    }

    public long getOpenAlerts() {
        return openAlerts;
    }

    public long getAcknowledgedAlerts() {
        return acknowledgedAlerts;
    }

}
