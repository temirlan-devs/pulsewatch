package com.temirlan.pulsewatch.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.MetricsSummaryResponse;
import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.enums.AlertStatus;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.repository.AlertEntryRepository;
import com.temirlan.pulsewatch.repository.LogEntryRepository;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;

@Service
public class ServiceHealthService {

    private final MetricEntryRepository metricEntryRepository;
    private final LogEntryRepository logEntryRepository;
    private final AlertEntryRepository alertEntryRepository;
    private final MetricEntryService metricEntryService;
    private final AlertService alertService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ServiceHealthService(MetricEntryRepository metricEntryRepository, LogEntryRepository logEntryRepository,
            AlertEntryRepository alertEntryRepository, MetricEntryService metricEntryService,
            AlertService alertService) {
        this.metricEntryRepository = metricEntryRepository;
        this.logEntryRepository = logEntryRepository;
        this.alertEntryRepository = alertEntryRepository;
        this.metricEntryService = metricEntryService;
        this.alertService = alertService;
    }

    public String determineStatus(long lastMetricTimestamp, long lastLogTimestamp, double errorRate,
            double averageLatency) {
        long now = System.currentTimeMillis();
        long freshnessThresholdMs = 5 * 60 * 1000;

        boolean noData = lastMetricTimestamp == 0 && lastLogTimestamp == 0;
        boolean staleMetrics = lastMetricTimestamp == 0 || (now - lastMetricTimestamp > freshnessThresholdMs);

        if (noData || staleMetrics) {
            return "NO_DATA";
        }

        if (errorRate > 0.05) {
            return "ERROR";
        }

        if (averageLatency > 500 || errorRate > 0.01) {
            return "WARN";
        }

        return "OK";
    }

    public List<ServiceHealthResponse> sortServicesByPriority(List<ServiceHealthResponse> services) {
        return services.stream()
                .sorted(
                        Comparator
                                .comparingInt((ServiceHealthResponse r) -> switch (r.getStatus()) {
                                    case "ERROR" -> 0;
                                    case "WARN" -> 1;
                                    case "NO_DATA" -> 2;
                                    default -> 3;
                                })
                                .thenComparing(ServiceHealthResponse::getOpenAlerts, Comparator.reverseOrder())
                                .thenComparing(ServiceHealthResponse::getErrorRate, Comparator.reverseOrder()))
                .toList();
    }

    public ServiceHealthResponse getServiceHealth(String service) {
        long lastMetricTimestamp = metricEntryRepository
                .findTopByServiceOrderByTimestampDesc(service)
                .map(m -> m.getTimestamp())
                .orElse(0L);

        long lastLogTimestamp = logEntryRepository
                .findTopByServiceOrderByTimestampDesc(service)
                .map(l -> l.getTimestamp())
                .orElse(0L);

        String lastMetricTimestampReadable = lastMetricTimestamp == 0
                ? null
                : Instant.ofEpochMilli(lastMetricTimestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(FORMATTER);

        String lastLogTimestampReadable = lastLogTimestamp == 0
                ? null
                : Instant.ofEpochMilli(lastLogTimestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(FORMATTER);

        MetricsSummaryResponse summary = metricEntryService.getSummary(service, 0L, System.currentTimeMillis());

        double errorRate = summary.errorRate();
        double averageLatency = summary.averageLatency();

        String status = determineStatus(lastMetricTimestamp, lastLogTimestamp, errorRate, averageLatency);

        String reason = null;
        if ("ERROR".equals(status)) {
            reason = "Error rate exceeded 5%";
            alertService.createAlertIfStatusChanged(service, AlertType.HEALTH, status, reason);
        }

        long openAlerts = alertEntryRepository.countByServiceAndAlertStatus(service, AlertStatus.OPEN);
        long acknowledgedAlerts = alertEntryRepository.countByServiceAndAlertStatus(service, AlertStatus.ACKNOWLEDGED);

        return new ServiceHealthResponse(service, status, errorRate, averageLatency, lastMetricTimestamp,
                lastLogTimestamp, lastMetricTimestampReadable, lastLogTimestampReadable, openAlerts,
                acknowledgedAlerts);

    }

}
