package com.temirlan.pulsewatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class MetricIngestionRequest {

    @NotBlank
    private String service;

    @NotNull
    @PositiveOrZero
    private Long requestCount;

    @NotNull
    @PositiveOrZero
    private Long errorCount;

    @NotNull
    private Double averageLatency;

    @NotNull
    private Long timestamp;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public Long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Long errorCount) {
        this.errorCount = errorCount;
    }

    public Double getAverageLatency() {
        return averageLatency;
    }

    public void setAverageLatency(Double averageLatency) {
        this.averageLatency = averageLatency;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
