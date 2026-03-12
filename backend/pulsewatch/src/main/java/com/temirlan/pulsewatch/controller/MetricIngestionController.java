package com.temirlan.pulsewatch.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.dto.MetricIngestionRequest;
import com.temirlan.pulsewatch.dto.MetricResponse;
import com.temirlan.pulsewatch.dto.MetricsSummaryResponse;
import com.temirlan.pulsewatch.dto.PagedMetricResponse;
import com.temirlan.pulsewatch.service.MetricEntryService;

@RestController
@RequestMapping("/metrics")
public class MetricIngestionController {

    private final MetricEntryService metricEntryService;

    public MetricIngestionController(MetricEntryService metricEntryService) {
        this.metricEntryService = metricEntryService;
    }

    @PostMapping
    public MetricResponse ingestMetric(@Valid @RequestBody MetricIngestionRequest request) {
        return metricEntryService.saveMetric(request);
    }

    @GetMapping
    public PagedMetricResponse getMetrics(
        @RequestParam(required = false) String service,
        @RequestParam(required = false) Long from,
        @RequestParam(required = false) Long to,
        Pageable pageable
    ) {
        if (pageable.getPageSize() > 100) {
            pageable = Pageable.ofSize(100).withPage(pageable.getPageNumber());
        }

        return metricEntryService.getMetrics(service, from, to, pageable);
    }

    @GetMapping("/summary")
    public MetricsSummaryResponse getSummary(
        @RequestParam String service,
        @RequestParam long from,
        @RequestParam long to
    ) {
        return metricEntryService.getSummary(service, from, to);
    }
}
