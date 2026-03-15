package com.temirlan.pulsewatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.ServiceInsightsResponse;
import com.temirlan.pulsewatch.service.ServiceInsightsService;

@RestController
public class ServiceInsightsController {
    
    private final ServiceInsightsService serviceInsightsService;

    public ServiceInsightsController(ServiceInsightsService serviceInsightsService) {
        this.serviceInsightsService = serviceInsightsService;
    }

    @GetMapping("/services/{service}/insights")
    public ServiceInsightsResponse getSericeInsights(@PathVariable String service) {
        return serviceInsightsService.getServiceInsights(service);
    }


}
