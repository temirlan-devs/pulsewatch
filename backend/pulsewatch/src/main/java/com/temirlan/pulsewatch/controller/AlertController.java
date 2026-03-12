package com.temirlan.pulsewatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.AlertResponse;
import com.temirlan.pulsewatch.service.AlertService;

@RestController
public class AlertController {
    
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/alerts")
    public List<AlertResponse> getAllAlerts(@RequestParam(required = false) String service) {

        if (service != null && !service.isBlank()) {
            return alertService.getAlertByService(service);
        }

        return alertService.getAllAlerts();
    }


}
