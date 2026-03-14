package com.temirlan.pulsewatch.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServicesResponse;
import com.temirlan.pulsewatch.service.MetricEntryService;
import com.temirlan.pulsewatch.service.ServiceHealthService;

@RestController
public class ServiceHealthController {
    
    private final ServiceHealthService serviceHealthService;
    private final MetricEntryService metricEntryService;

    public ServiceHealthController(ServiceHealthService serviceHealthService, MetricEntryService metricEntryService) { 
        this.serviceHealthService = serviceHealthService;
        this.metricEntryService = metricEntryService;
    }

    @GetMapping("/services/{service}/health")
    public ServiceHealthResponse getServiceHealth(@PathVariable String service) {
        return serviceHealthService.getServiceHealth(service);
    }

    @GetMapping("/services/health")
    public List<ServiceHealthResponse> getAllServicesHealth() {
        ServicesResponse servicesResponse = metricEntryService.getServices();

        List<ServiceHealthResponse> services = servicesResponse.getServices()
                .stream()
                .map(serviceHealthService::getServiceHealth)
                .toList();

        return serviceHealthService.sortServicesByPriority(services);              
    }

}
