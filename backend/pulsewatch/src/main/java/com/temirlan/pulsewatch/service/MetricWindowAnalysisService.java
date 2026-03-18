package com.temirlan.pulsewatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.MetricWindowAnalysisResponse;
import com.temirlan.pulsewatch.model.MetricEntry;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;

@Service
public class MetricWindowAnalysisService {
    
    private final MetricEntryRepository metricEntryRepository;

    public MetricWindowAnalysisService(MetricEntryRepository metricEntryRepository) {
        this.metricEntryRepository = metricEntryRepository;
    }

    private double calculateErrorRate(List<MetricEntry> metrics) {
        long totalRequests = metrics.stream()
                .mapToLong(MetricEntry::getRequestCount)
                .sum();

        if (totalRequests == 0) {
            return 0.0;
        }

        long totalErrors = metrics.stream()
                .mapToLong(MetricEntry::getErrorCount)
                .sum();

        return (double) totalErrors / totalRequests;
    }

    private double calculateAverageLatency(List<MetricEntry> metrics) {
        return metrics.stream()
                .mapToDouble(MetricEntry::getAverageLatency)
                .average()
                .orElse(0.0);
    }

    public MetricWindowAnalysisResponse analyze(String service) {

        long now = System.currentTimeMillis();

        long currentFrom = now - (5 * 60 * 1000);
        long baselineFrom = now - (35 * 60 * 1000);
        long baseLineTo = currentFrom;

        List<MetricEntry> currentMetrics = metricEntryRepository.findByServiceAndTimestampBetween(service, currentFrom, now);
        List<MetricEntry> baselineMetrics = metricEntryRepository.findByServiceAndTimestampBetween(service, baselineFrom, baseLineTo);

        boolean hasCurrent = !currentMetrics.isEmpty();
        boolean hasBaseline = !baselineMetrics.isEmpty();

        double currentErrorRate = calculateErrorRate(currentMetrics);
        double baselineErrorRate = calculateErrorRate(baselineMetrics);

        double currentLatency = calculateAverageLatency(currentMetrics);
        double baselineLatency = calculateAverageLatency(baselineMetrics);

        return new MetricWindowAnalysisResponse(hasCurrent, hasBaseline, currentErrorRate, baselineErrorRate, currentLatency, baselineLatency);

    }

}
