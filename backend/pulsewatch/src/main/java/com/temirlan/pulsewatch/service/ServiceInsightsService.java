package com.temirlan.pulsewatch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServiceInsightsResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.enums.ServiceStatus;
import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.repository.LogEntryRepository;

@Service
public class ServiceInsightsService {

    private final ServiceHealthService serviceHealthService;
    private final ServicePredictionService servicePredictionService;
    private final LogEntryRepository logEntryRepository;
    @Value("${pulsewatch.freshness-threshold-minutes}")
    private long freshnessThresholdMinutes;
    @Value("${pulsewatch.insights.log-window-multiplier}")
    private long logWindowMultipler;
    @Value("${pulsewatch.insights.log-limit}")
    private int logLimit;

    public ServiceInsightsService(ServiceHealthService serviceHealthService, ServicePredictionService servicePredictionService, LogEntryRepository logEntryRepository) {
        this.serviceHealthService = serviceHealthService;
        this.servicePredictionService = servicePredictionService;
        this.logEntryRepository = logEntryRepository;
    }

    public ServiceInsightsResponse getServiceInsights(String service) {
        ServiceHealthResponse health = serviceHealthService.getServiceHealth(service);
        ServicePredictionResponse prediction = servicePredictionService.predictServiceRisk(service);

        long now = System.currentTimeMillis();
        long logWindowMs = freshnessThresholdMinutes * logWindowMultipler * 60 * 1000;
        long from = now - logWindowMs;

        List<LogEntry> recentLogs = logEntryRepository
                .findByServiceAndTimestampBetween(
                    service, 
                    from,
                    now, 
                    PageRequest.of(0, logLimit)
                )
                .getContent();

        boolean hasTimeoutLogs = recentLogs.stream()
                    .anyMatch(log -> log.getMessage().toLowerCase().contains("timeout"));
        boolean hasDatabaseLogs = recentLogs.stream()
                    .anyMatch(log -> log.getMessage().toLowerCase().contains("database"));
        boolean hasAuthLogs = recentLogs.stream()
                    .anyMatch(log -> log.getMessage().toLowerCase().contains("auth"));

        String summary;
        String likelyCause;
        String recommendedAction;

        if (health.getStatus() == ServiceStatus.NO_DATA) {
            return new ServiceInsightsResponse(
                    service,
                    "Service health cannot be determined because recent monitoring data is missing.",
                    "Recent metrics or logs are unavailable for this service.",
                    "Verify telemetry ingestion, service heartbeat, and monitoring pipeline health.",
                    prediction.getRiskScore(),
                    prediction.getPredictedStatus());
        }

        if (health.getStatus() == ServiceStatus.ERROR) {
            summary = "Service is currently unhealthy and at high operational risk.";
        } else if (health.getStatus() == ServiceStatus.WARN) {
            summary = "Service is degraded and should be monitored closely.";
        } else {
            summary = "Service is currently operating normally.";
        }

        List<String> likelyCauses = new ArrayList<>();
        List<String> recommendedActions = new ArrayList<>();



        if (hasTimeoutLogs) {
            likelyCauses.add("Frequent timeout-related logs were detected in the recent monitoring window.");
            recommendedActions.add("Check downstream dependencies, network latency, and request timeout configuration");
        } 
        if (hasDatabaseLogs) {
            likelyCauses.add("Recent logs suggest database-related failures or slow database interactions.");
            recommendedActions.add("Inspect database connectivity, query latency, and recent database errors.");
        } 
        if (hasAuthLogs) {
            likelyCauses.add("Recent logs suggest authentication-related failures or abnormal auth activity.");
            recommendedActions.add("Inspect authnentication dependencies, token validation flow, and recent auth error logs.");
        }
        if (health.getErrorRate() > 0.10 && health.getAverageLatency() > 500 && health.getOpenAlerts() > 0) {
            likelyCauses.add("Error rate is critically high, latency is elevated, and active alerts remain open.");
            recommendedActions.add("Investigate recent deployments, downstream dependencies, and unresolved alerts immediately.");
        } else if (health.getErrorRate() > 0.05) {
            likelyCauses.add("Error rate is elevated above normal operation thresholds.");
            recommendedActions.add("Inspect recent request failures and error logs for the affected service.");        
        } else if (health.getAverageLatency() > 500) {
            likelyCauses.add("Latency is increasing and may indicate resource contention or a slow downstream dependency.");
            recommendedActions.add("Check service latency breakdown, infrastructure load, and dependency response times."); 
        } else if (health.getOpenAlerts() > 0) {
            likelyCauses.add("Active alerts suggest unresolved operational issues are still affecting the service.");
            recommendedActions.add("Review open alerts and confirm whether mitigation steps have been applied."); 
        } 

        likelyCause = likelyCauses.isEmpty()
            ? "No strong negative signals are currently present."
            : String.join("; ", likelyCauses);

        recommendedAction = recommendedActions.isEmpty()
            ? "Continue routine monitoring."
            : String.join("; ", recommendedActions);

        return new ServiceInsightsResponse(service, summary, likelyCause, recommendedAction, prediction.getRiskScore(),
                prediction.getPredictedStatus());

    }

}
