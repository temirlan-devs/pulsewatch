package com.temirlan.pulsewatch.dto;

public class AlertStatsResponse {
    
    private long totalAlerts;
    private long openAlerts;
    private long acknowledgedAlerts;
    private long resolvedAlerts;
    private long healthAlerts;
    private long anomalyAlerts;

    public AlertStatsResponse (
        long totalAlerts,
        long openAlerts,
        long acknowledgedAlerts,
        long resolvedAlerts,
        long healthAlerts,
        long anomalyAlerts
    ) {
        this.totalAlerts = totalAlerts;
        this.openAlerts = openAlerts;
        this.acknowledgedAlerts = acknowledgedAlerts;
        this.resolvedAlerts = resolvedAlerts;
        this.healthAlerts = healthAlerts;
        this.anomalyAlerts = anomalyAlerts;
    }

    public long getTotalAlerts() {
        return totalAlerts;
    }

    public long getOpenAlerts() {
        return openAlerts;
    }

    public long getAcknowledgedAlerts() {
        return acknowledgedAlerts;
    }

    public long getResolvedAlerts() {
        return resolvedAlerts;
    }

    public long getHealthAlerts() {
        return healthAlerts;
    }

    public long getAnomalyAlerts() {
        return anomalyAlerts;
    }

}
