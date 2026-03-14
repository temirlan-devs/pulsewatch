package com.temirlan.pulsewatch.dto;

public class ServiceHealthResponse {
    
    public String service;
    public String status;

    private double errorRate;
    private double averageLatency;

    private long lastMetricTimestamp;
    private long lastLogTimestamp;

    private long openAlerts;
    private long acknowledgedAlerts;

    public ServiceHealthResponse(String service, String status, double errorRate, double averageLatency, long lastMetricTimestamp, long lastLogTimestamp, long openAlerts, long acknowledgedAlerts) {
        this.service = service;
        this.status = status;
        this.errorRate = errorRate;
        this.averageLatency = averageLatency;
        this.lastMetricTimestamp = lastMetricTimestamp;
        this.lastLogTimestamp = lastLogTimestamp;
        this.openAlerts = openAlerts;
        this.acknowledgedAlerts = acknowledgedAlerts;
    }

    public String getService() {
        return service;
    }

    public String getStatus() {
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

    public long getOpenAlerts() {
        return openAlerts;
    }

    public long getAcknowledgedAlerts() {
        return acknowledgedAlerts;
    }

}
