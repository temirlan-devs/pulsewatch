package com.temirlan.pulsewatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.service.ServiceHealthService;

@RestController
public class ServiceHealthController {
    
    private final ServiceHealthService serviceHealthService;

    public ServiceHealthController(ServiceHealthService serviceHealthService) { 
        this.serviceHealthService = serviceHealthService;
    }

    @GetMapping("/services/{service}/health")
    public ServiceHealthResponse getServiceHealth(@PathVariable String service) {
        return serviceHealthService.getServiceHealth(service);
    }

}
