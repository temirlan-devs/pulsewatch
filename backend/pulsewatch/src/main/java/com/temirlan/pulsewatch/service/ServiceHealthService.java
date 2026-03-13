package com.temirlan.pulsewatch.service;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.MetricsSummaryResponse;
import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.repository.LogEntryRepository;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;

@Service
public class ServiceHealthService {
    
    private final MetricEntryRepository metricEntryRepository;
    private final LogEntryRepository logEntryRepository;
    private final MetricEntryService metricEntryService;
    private final AlertService alertService;

    public ServiceHealthService(MetricEntryRepository metricEntryRepository, LogEntryRepository logEntryRepository, MetricEntryService metricEntryService, AlertService alertService) {
        this.metricEntryRepository = metricEntryRepository;
        this.logEntryRepository = logEntryRepository;
        this.metricEntryService = metricEntryService;
        this.alertService = alertService;
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

        MetricsSummaryResponse summary = metricEntryService.getSummary(service, 0L, System.currentTimeMillis());

        double errorRate = summary.errorRate();
        double averageLatency = summary.averageLatency();

        String status = determineStatus(lastMetricTimestamp, lastLogTimestamp, errorRate, averageLatency);

        String reason = null;
        if("ERROR".equals(status)) {
            reason = "Error rate exceeded 5%";
            alertService.createAlertIfStatusChanged(service, AlertType.HEALTH, status, reason);
        }

        return new ServiceHealthResponse(service, status, errorRate, averageLatency, lastMetricTimestamp, lastLogTimestamp);
        
    }

    public String determineStatus(long lastMetricTimestamp, long lastLogTimestamp, double errorRate, double averageLatency) {
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

}
