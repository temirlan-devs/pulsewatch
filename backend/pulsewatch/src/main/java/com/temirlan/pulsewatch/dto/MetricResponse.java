package com.temirlan.pulsewatch.dto;

public class MetricResponse {
    private Long id;
    private String service;
    private Long requestCount;
    private Long errorCount;
    private Double averageLatency;
    private Long timestamp;

    public MetricResponse(Long id, String service, Long requestCount, Long errorCount, Double averageLatency, Long timestamp) {
        this.id = id;
        this.service = service;
        this.requestCount = requestCount;
        this.errorCount = errorCount;
        this.averageLatency = averageLatency;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public Long getErrorCount() {
        return errorCount;
    }

    public Double getAverageLatency() {
        return averageLatency;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
