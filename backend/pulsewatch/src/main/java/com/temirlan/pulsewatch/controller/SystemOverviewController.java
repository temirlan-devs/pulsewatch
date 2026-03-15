package com.temirlan.pulsewatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.temirlan.pulsewatch.dto.SystemOverviewResponse;
import com.temirlan.pulsewatch.service.SystemOverviewService;

@RestController
public class SystemOverviewController {
    
    private final SystemOverviewService systemOverviewService;

    public SystemOverviewController(SystemOverviewService systemOverviewService) {
        this.systemOverviewService = systemOverviewService;
    }

    @GetMapping("/system/overview")
    public SystemOverviewResponse getSystemOverview() {
        return systemOverviewService.getSystemOverview();
    }

}
