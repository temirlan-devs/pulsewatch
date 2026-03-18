package com.temirlan.pulsewatch.dto;

public class MetricWindowAnalysisResponse {
    
    private boolean hasCurrentData;
    private boolean hasBaselineData;
    private double currentErrorRate;
    private double baselineErrorRate;
    private double currentAverageLatency;
    private double baselineAverageLatency;

    public MetricWindowAnalysisResponse(boolean hasCurrentData, boolean hasBaselineData, double currentErrorRate, double baselineErrorRate, double currentAverageLatency, double baselineAverageLatency) {
        this.hasCurrentData = hasCurrentData;
        this.hasBaselineData = hasBaselineData;
        this.currentErrorRate = currentErrorRate;
        this.baselineErrorRate = baselineErrorRate;
        this.currentAverageLatency = currentAverageLatency;
        this.baselineAverageLatency = baselineAverageLatency;
    }

    public boolean isHasCurrentData() {
        return hasCurrentData;
    }

    public boolean isHasBaselineData() {
        return hasBaselineData;
    }

    public double getCurrentErrorRate() {
        return currentErrorRate;
    }

    public double getBaselineErrorRate() {
        return baselineErrorRate;
    }

    public double getCurrentAverageLatency() {
        return currentAverageLatency;
    }

    public double getBaselineAverageLatency() {
        return baselineAverageLatency;
    }

}
