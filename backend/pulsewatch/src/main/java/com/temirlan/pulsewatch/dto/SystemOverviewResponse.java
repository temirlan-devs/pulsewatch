package com.temirlan.pulsewatch.dto;

public class SystemOverviewResponse {
    
    private long totalServices;
    private long healthyServices;
    private long warnServices;
    private long errorServices;
    private long noDataServices;
    private long totalOpenAlerts;
    private String highestRiskServices;

    public SystemOverviewResponse (
        long totalServices, long healthyServices, long warnServices, long errorServices, long noDataServices, long totalOpenAlerts, String highestRiskServices
    ) {
        this.totalServices = totalServices;
        this.healthyServices = healthyServices;
        this.warnServices = warnServices;
        this.errorServices = errorServices;
        this.noDataServices = noDataServices;
        this.totalOpenAlerts = totalOpenAlerts;
        this.highestRiskServices = highestRiskServices;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public long getHealthServices() {
        return healthyServices;
    }

    public long getWarnServices() {
        return warnServices;
    }

    public long getErrorServices() {
        return errorServices;
    }

    public long getNoDataServices() {
        return noDataServices;
    }

    public long getTotalOpenAlerts() {
        return totalOpenAlerts;
    }

    public String getHighestRiskServices() {
        return highestRiskServices;
    }

}
