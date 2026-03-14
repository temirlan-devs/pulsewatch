package com.temirlan.pulsewatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.service.ServicePredictionService;

@RestController
public class ServicePredictionController {
    
    private final ServicePredictionService servicePredictionService;

    public ServicePredictionController(ServicePredictionService servicePredictionService) {
        this.servicePredictionService = servicePredictionService;
    }

    @GetMapping("/services/{service}/prediction")
    public ServicePredictionResponse getServicePrediction(@PathVariable String service) {
        return servicePredictionService.predictServiceRisk(service);
    }

    @GetMapping("/services/predictions")
    public List<ServicePredictionResponse> getAllServicePredictions() {
        return servicePredictionService.predictAllServices();
    }

}
