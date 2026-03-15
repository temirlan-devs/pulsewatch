package com.temirlan.pulsewatch.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.MetricEntry;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;

@EnableScheduling
public class AnomalyDetectionService {
    
    private final MetricEntryRepository metricEntryRepository;
    private final AlertService alertService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public AnomalyDetectionService(MetricEntryRepository metricEntryRepository, AlertService alertService) {
        this.metricEntryRepository = metricEntryRepository;
        this.alertService = alertService;
    }

    private double calculateErrorRate(List<MetricEntry> metrics) {
        long totalRequests = metrics.stream()
            .mapToLong(MetricEntry::getRequestCount)
            .sum();

        long totalErrors = metrics.stream()
            .mapToLong(MetricEntry::getErrorCount)
            .sum();

        if (totalRequests == 0) {
            return 0.0;
        }

        return (double) totalErrors / totalRequests;
    }

    private void detectServiceAnomaly(String service) {
        long now = System.currentTimeMillis();

        long currentFrom = now - (5 * 60 * 1000);
        long baselineFrom = now - (35 * 60 * 1000);
        long baselineTo = currentFrom;

        System.out.println("Service: " + service);
        System.out.println("baselineFrom: " + formatTimestamp(baselineFrom));
        System.out.println("baselineTo: " + formatTimestamp(baselineTo));
        System.out.println("currentFrom: " + formatTimestamp(currentFrom));
        System.out.println("Now: " + formatTimestamp(now));

        List<MetricEntry> baselineMetrics = metricEntryRepository.findByServiceAndTimestampBetween(service, baselineFrom, baselineTo);
        List<MetricEntry> currentMetrics = metricEntryRepository.findByServiceAndTimestampBetween(service, currentFrom, now);

        if (baselineMetrics.isEmpty()) {
            System.out.println("Empty baseline list");
        }

        if (currentMetrics.isEmpty()) {
            System.out.println("Empty current list");
        }

        if (baselineMetrics.isEmpty() || currentMetrics.isEmpty()) {
            System.out.println("Empty list");
            System.out.println();
            return;
        }

        double baselineErrorRate = calculateErrorRate(baselineMetrics);
        double currentErrorRate = calculateErrorRate(currentMetrics);

        if (baselineErrorRate == 0.0) {
            System.out.println("baseline error rate is 0");
            System.out.println();
            return;
        }
        System.out.println("Current error rate: " + currentErrorRate);
        System.out.println("Baseline error rate: " + baselineErrorRate);
        if (currentErrorRate > baselineErrorRate * 3) {
            alertService.createAlertIfStatusChanged(service, AlertType.ANOMALY, "WARN", "Anomalous error rate spike detected");
        }
        System.out.println();
    }

    @Scheduled(fixedRate = 30000)
    public void detectAnomalies() {
        List<String> services = metricEntryRepository.findDistinctByOrderByServiceAsc()
            .stream()
            .map(metric -> metric.getService())
            .distinct()
            .toList();

        for(String service : services) {
            detectServiceAnomaly(service);
        }
    }

    private String formatTimestamp(long ts) {
        return Instant.ofEpochMilli(ts)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMATTER);
    }

}
