package com.temirlan.pulsewatch.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.MetricWindowAnalysisResponse;
import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.dto.ServiceRiskRankingResponse;
import com.temirlan.pulsewatch.dto.ServicesResponse;
import com.temirlan.pulsewatch.enums.ServiceStatus;

@Service
public class ServicePredictionService {
    
    private final MetricEntryService metricEntryService;
    private final ServiceHealthService serviceHealthService;
    private final MetricWindowAnalysisService metricWindowAnalysisService;

    public ServicePredictionService(MetricEntryService metricEntryService, ServiceHealthService serviceHealthService, MetricWindowAnalysisService metricWindowAnalysisService) {
        this.metricEntryService = metricEntryService;
        this.serviceHealthService = serviceHealthService;
        this.metricWindowAnalysisService = metricWindowAnalysisService;
    }

    public ServicePredictionResponse predictServiceRisk(String service) {

        ServiceHealthResponse health = serviceHealthService.getServiceHealth(service);
        MetricWindowAnalysisResponse analysis = metricWindowAnalysisService.analyze(service);

        if (!analysis.isHasCurrentData()) {
            return new ServicePredictionResponse(
                service,
                "NO_DATA",
                0.0,
                0.2,
                "No recent metrics available for prediction"
            );
        }

        double currentErrorRate = analysis.getCurrentErrorRate();
        double baselineErrorRate = analysis.getBaselineErrorRate();
        double currentLatency = analysis.getCurrentAverageLatency();
        double baselineLatency = analysis.getBaselineAverageLatency();


        long openAlerts = health.getOpenAlerts();
        ServiceStatus status = health.getStatus();

        double riskScore = 0.0;
        int triggeredSignals = 0;
        List<String> reasons = new ArrayList<>();
        

        if (analysis.isHasBaselineData()) {
            if (baselineErrorRate > 0 && currentErrorRate >= baselineErrorRate * 2) {
                riskScore += 0.3;
                triggeredSignals++;
                reasons.add("Error rate is more than 2x the baseline");
            } else if (currentErrorRate > baselineErrorRate) {
                riskScore += 0.15;
                triggeredSignals++;
                reasons.add("Error rate is increasing above baseline");
            }

            if (baselineErrorRate > 0 && currentLatency >= baselineLatency * 1.5) {
                riskScore += 0.2;
                triggeredSignals++;
                reasons.add("Latency is more than 1.5x the baseline");
            } else if (currentLatency > baselineLatency) {
                riskScore += 0.1;
                triggeredSignals++;
                reasons.add("Latency is increasing above baseline");
            }
        } else {
            if (currentErrorRate > 0.05) {
                riskScore += 0.2;
                triggeredSignals++;
                reasons.add("Current error rate is elevated");
            }

            if (currentLatency > 500) {
                riskScore += 0.15;
                triggeredSignals++;
                reasons.add("Current latency is elevated");
            }
        }

        if (openAlerts > 0) {
            riskScore += 0.2;
            triggeredSignals++;
            reasons.add("Open alerts are still active");
        }

        if (status == ServiceStatus.WARN) {
            riskScore += 0.1;
            triggeredSignals++;
            reasons.add("Service is already in WARN state");
        } else if (status == ServiceStatus.ERROR) {
            riskScore += 0.2;
            triggeredSignals++;
            reasons.add("Service is already in ERROR state");
        }

        riskScore = Math.min(1.0, riskScore);
        double confidence = analysis.isHasBaselineData() 
                ? 0.5 + (0.1 * triggeredSignals)
                : 0.4 + (0.1 * triggeredSignals);
        confidence = Math.min(1.0, confidence);

        String predictedStatus;
        if (riskScore >= 0.75) {
            predictedStatus = "ERROR";
        } else if (riskScore >= 0.4) {
            predictedStatus = "WARN";
        } else {
            predictedStatus = "OK";
        }

        String reason = reasons.isEmpty()
                ? "No strong degradation singals detected"
                : String.join("; ", reasons);

        return new ServicePredictionResponse(service, predictedStatus, riskScore, confidence, reason);
    }

    public List<ServicePredictionResponse> predictAllServices() {
        ServicesResponse servicesResponse = metricEntryService.getServices();

        return servicesResponse.getServices()
                .stream()
                .map(this::predictServiceRisk)
                .toList();
    }

    public List<ServiceRiskRankingResponse> getRiskRanking() {
        List<ServicePredictionResponse> predictions = predictAllServices();

        return predictions.stream()
                .sorted(Comparator.comparingDouble(ServicePredictionResponse::getRiskScore).reversed())
                .map(p -> new ServiceRiskRankingResponse(
                    p.getService(),
                    p.getRiskScore(),
                    p.getPredictedStatus(),
                    p.getConfidence()
                ))
                .toList();
    }

}
