package com.temirlan.pulsewatch.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.MetricWindowAnalysisResponse;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;

@Service
public class AnomalyDetectionService {
    
    private final MetricEntryRepository metricEntryRepository;
    private final AlertService alertService;
    private final MetricWindowAnalysisService metricWindowAnalysisService;


    public AnomalyDetectionService(MetricEntryRepository metricEntryRepository, AlertService alertService, MetricWindowAnalysisService metricWindowAnalysisService) {
        this.metricEntryRepository = metricEntryRepository;
        this.alertService = alertService;
        this.metricWindowAnalysisService = metricWindowAnalysisService;
    }

    private void detectServiceAnomaly(String service) {
        MetricWindowAnalysisResponse analysis = metricWindowAnalysisService.analyze(service);

        if (!analysis.isHasCurrentData()) {
            return;
        }

        double currentErrorRate = analysis.getCurrentErrorRate();
        double baselineErrorRate = analysis.getBaselineErrorRate();

        if (!analysis.isHasBaselineData()) {
            if (currentErrorRate > 0.05) {
                alertService.createAlertIfStatusChanged(
                    service,
                    AlertType.ANOMALY, 
                    "WARN", 
                     "High error rate detected without historical baseline"
                );
            }
            return;
        }

        if (baselineErrorRate == 0.0) {
            return;
        }

        if (currentErrorRate > baselineErrorRate * 3) {
            alertService.createAlertIfStatusChanged(
                service, 
                AlertType.ANOMALY, 
                "WARN", 
                "Anomalous error rate spike detected"
            );
        }
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

}
