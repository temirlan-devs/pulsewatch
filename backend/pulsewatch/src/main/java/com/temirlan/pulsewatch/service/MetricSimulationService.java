package com.temirlan.pulsewatch.service;

import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.MetricIngestionRequest;

@Service
public class MetricSimulationService {
    
    private final MetricEntryService metricEntryService;
    private final Random random = new Random();

    public MetricSimulationService(MetricEntryService metricEntryService) {
        this.metricEntryService = metricEntryService;
    }

    @Scheduled(fixedRate = 5000)
    public void simulateMetrics() {
        simulateService("auth-service");
        simulateService("payment-service");
        simulateService("order-service");
    }

    private void simulateService(String service) {
        boolean anomaly = random.nextInt(10) == 0;

        long requestCount = 80 + random.nextInt(80);
        long errorCount = anomaly ? 10 + random.nextInt(15) : random.nextInt(3);
        double averageLatency = anomaly ? 600 + random.nextInt(400) : 100 + random.nextInt(150);

        MetricIngestionRequest request = new MetricIngestionRequest();
        request.setService(service);
        request.setRequestCount(requestCount);  
        request.setErrorCount(errorCount);
        request.setAverageLatency(averageLatency);
        request.setTimestamp(System.currentTimeMillis());
        metricEntryService.saveMetric(request);
    }
}
