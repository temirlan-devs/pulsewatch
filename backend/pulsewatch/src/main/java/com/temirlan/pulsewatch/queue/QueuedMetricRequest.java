package com.temirlan.pulsewatch.queue;

import com.temirlan.pulsewatch.dto.MetricIngestionRequest;

public class QueuedMetricRequest {
    
    private final MetricIngestionRequest request;
    private final int retryCount;

    public QueuedMetricRequest(MetricIngestionRequest request, int retryCount) {
        this.request = request;
        this.retryCount = retryCount;
    }

    public MetricIngestionRequest getRequest() {
        return request;
    }

    public int getRetryCount() {
        return retryCount;
    }

}
