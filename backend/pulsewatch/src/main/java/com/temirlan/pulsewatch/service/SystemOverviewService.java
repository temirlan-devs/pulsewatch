package com.temirlan.pulsewatch.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.dto.ServicesResponse;
import com.temirlan.pulsewatch.dto.SystemOverviewResponse;
import com.temirlan.pulsewatch.enums.ServiceStatus;

@Service
public class SystemOverviewService {
    
    private final MetricEntryService metricEntryService;
    private final ServiceHealthService serviceHealthService;
    private final ServicePredictionService servicePredictionService;

    public SystemOverviewService (MetricEntryService metricEntryService, ServiceHealthService serviceHealthService, ServicePredictionService servicePredictionService) {
        this.metricEntryService = metricEntryService;
        this.serviceHealthService = serviceHealthService;
        this.servicePredictionService = servicePredictionService;
    }

    public SystemOverviewResponse getSystemOverview() {
        ServicesResponse servicesResponse = metricEntryService.getServices();
        List<String> services = servicesResponse.getServices();

        List<ServiceHealthResponse> healthResponses = services.stream()
                .map(serviceHealthService::getServiceHealth)
                .toList();
        List<ServicePredictionResponse> predictions = services.stream()
                .map(servicePredictionService::predictServiceRisk)
                .toList();
        
        long totalServices = healthResponses.size();

        long healthyServices = healthResponses.stream()
                .filter(r -> r.getStatus() == ServiceStatus.OK)
                .count();

        long warnServices = healthResponses.stream()
                .filter(r -> r.getStatus() == ServiceStatus.WARN)
                .count();
        
        long errorServices = healthResponses.stream()
                .filter(r -> r.getStatus() == ServiceStatus.ERROR)
                .count();
        
        long noDataServices = healthResponses.stream()
                .filter(r -> r.getStatus() == ServiceStatus.NO_DATA)
                .count();

        long totalOpenAlerts = services.stream()
                .mapToLong(service -> serviceHealthService.getServiceHealth(service).getOpenAlerts())
                .sum();

        String highestRiskServices = predictions.stream()
                .max(Comparator.comparingDouble(ServicePredictionResponse::getRiskScore))
                .map(ServicePredictionResponse::getService)
                .orElse(null);

        return new SystemOverviewResponse(totalServices, healthyServices, warnServices, errorServices, noDataServices, totalOpenAlerts, highestRiskServices);
    }


}
