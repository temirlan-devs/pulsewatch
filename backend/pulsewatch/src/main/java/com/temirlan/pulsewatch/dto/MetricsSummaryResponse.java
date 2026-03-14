package com.temirlan.pulsewatch.dto;

public record MetricsSummaryResponse(
    String service,
    long totalRequests,
    long totalErrors,
    double errorRate,
    double averageLatency,
    long fromTimestamp,
    long toTimestamp,
    String fromTimestampReadable,
    String toTimestampReadable
) {}
