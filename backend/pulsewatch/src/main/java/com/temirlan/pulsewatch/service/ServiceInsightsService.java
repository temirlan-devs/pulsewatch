package com.temirlan.pulsewatch.service;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServiceInsightsResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.enums.ServiceStatus;

@Service
public class ServiceInsightsService {

    private final ServiceHealthService serviceHealthService;
    private final ServicePredictionService servicePredictionService;

    public ServiceInsightsService(ServiceHealthService serviceHealthService,
            ServicePredictionService servicePredictionService) {
        this.serviceHealthService = serviceHealthService;
        this.servicePredictionService = servicePredictionService;
    }

    public ServiceInsightsResponse getServiceInsights(String service) {
        ServiceHealthResponse health = serviceHealthService.getServiceHealth(service);
        ServicePredictionResponse prediction = servicePredictionService.predictServiceRisk(service);

        if (health.getStatus() == ServiceStatus.NO_DATA) {
            return new ServiceInsightsResponse(
                    service,
                    "Service health cannot be determined because recent monitoring data is missing.",
                    "Recent metrics or logs are unavailable for this service.",
                    "Verify telemetry ingestion, service heartbeat, and monitoring pipeline health.",
                    prediction.getRiskScore(),
                    prediction.getPredictedStatus());
        }

        String summary;
        String likelyCause;
        String recommendedAction;

        if (health.getStatus() == ServiceStatus.ERROR) {
            summary = "Service is currently unhealthy and at high operational risk.";
        } else if (health.getStatus() == ServiceStatus.WARN) {
            summary = "Service is degraded and should be monitored closely.";
        } else if (health.getStatus() == ServiceStatus.NO_DATA) {
            summary = "Service health cannot be determined because recent monitoring data is missed.";
        } else {
            summary = "Service is currently operating normally.";
        }

        if (health.getErrorRate() > 0.10 && health.getAverageLatency() > 500 && health.getOpenAlerts() > 0) {
            likelyCause = "Error rate is critically high, latency is elevated, and active alerts remain open.";
            recommendedAction = "Investigate recent deployments, downstream dependencies, and unresolved alerts immediately.";
        } else if (health.getErrorRate() > 0.05) {
            likelyCause = "Error rate is elevated above normal operation thresholds.";
            recommendedAction = "Inspect recent request failures and error logs for the affected service.";
        } else if (health.getAverageLatency() > 500) {
            likelyCause = "Latency is increasing and may indicate resource contention or a slow downstream dependency.";
            recommendedAction = "Check service latency breakdown, infrastructure load, and dependency response times.";
        } else if (health.getOpenAlerts() > 0) {
            likelyCause = "Active alerts suggest unresolved operational issues are still affecting the service.";
            recommendedAction = "Review open alerts and confirm whether mitigation steps have been applied.";
        } else if (health.getStatus() == ServiceStatus.NO_DATA) {
            likelyCause = "Recent metrics or logs are unavailable for this service.";
            recommendedAction = "Verify telemetry ingestion, service heartbeat, and monitoring pipeline health.";
        } else {
            likelyCause = "No strong negative signals are currently present.";
            recommendedAction = "Continue routine monitoring.";
        }

        return new ServiceInsightsResponse(service, summary, likelyCause, recommendedAction, prediction.getRiskScore(),
                prediction.getPredictedStatus());

    }

}
