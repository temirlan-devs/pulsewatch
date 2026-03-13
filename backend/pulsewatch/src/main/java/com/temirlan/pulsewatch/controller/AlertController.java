package com.temirlan.pulsewatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.AlertResponse;
import com.temirlan.pulsewatch.dto.AlertStatsResponse;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.service.AlertService;

@RestController
public class AlertController {
    
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/alerts/{id}/acknowledge")
    public AlertResponse acknowledgeAlert(@PathVariable Long id) {
        return alertService.acknowledgeAlert(id);
    }

    @PostMapping("/alerts/{id}/resolve")
    public AlertResponse resolveAlert(@PathVariable Long id) {
        return alertService.resolveAlert(id);
    }

    @GetMapping("/alerts/stats")
    public AlertStatsResponse getAlertStats() {
        return alertService.getAlertsStats();
    }

    @GetMapping("/alerts")
    public List<AlertResponse> getAllAlerts(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) AlertType type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to
    ) {
        return alertService.getAlerts(service, type, status, from, to);
    }


}
