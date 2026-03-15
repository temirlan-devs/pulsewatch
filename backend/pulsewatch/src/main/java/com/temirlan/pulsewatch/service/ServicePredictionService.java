package com.temirlan.pulsewatch.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.dto.ServiceRiskRankingResponse;
import com.temirlan.pulsewatch.dto.ServicesResponse;

@Service
public class ServicePredictionService {
    
    private final MetricEntryService metricEntryService;
    private final ServiceHealthService serviceHealthService;
    private final AlertService alertService;

    public ServicePredictionService(MetricEntryService metricEntryService, ServiceHealthService serviceHealthService, AlertService alertService) {
        this.metricEntryService = metricEntryService;
        this.serviceHealthService = serviceHealthService;
        this.alertService = alertService;
    }

    public ServicePredictionResponse predictServiceRisk(String service) {

        ServiceHealthResponse health = serviceHealthService.getServiceHealth(service);

        double errorRate = health.getErrorRate();
        double latency = health.getAverageLatency();
        long openAlerts = health.getOpenAlerts();

        double riskScore = 0.0;
        double confidence = 0.3;
        String predictedStatus = health.getStatus().name();
        
        List<String> reasons = new ArrayList<>();

        if (errorRate > 0.10) {
            riskScore += 0.5;
            reasons.add("Error rate is critically high");
        } else if (errorRate > 0.05) {
            riskScore += 0.3;
            reasons.add("Error rate is elevated");
        }

        if (latency > 500) {
            riskScore += 0.2;
            reasons.add("Latency is increasing");
        }

        if (openAlerts > 0) {
            riskScore += 0.2;
            reasons.add("Active alerts detected");
        }

        riskScore = Math.min(1.0, riskScore);
        confidence = Math.min(1.0, confidence);

        String reason = reasons.isEmpty()
                ? "Service operating normally"
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
