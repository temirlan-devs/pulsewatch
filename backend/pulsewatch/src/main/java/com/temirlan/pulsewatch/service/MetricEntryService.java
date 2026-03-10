package com.temirlan.pulsewatch.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.model.MetricEntry;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;
import com.temirlan.pulsewatch.dto.MetricResponse;
import com.temirlan.pulsewatch.dto.PagedMetricResponse;

@Service
public class MetricEntryService {

    private final MetricEntryRepository metricEntryRepository;

    public MetricEntryService(MetricEntryRepository metricEntryRepository) {
        this.metricEntryRepository = metricEntryRepository;
    }

    private MetricResponse mapToMetricResponse(MetricEntry entry) {
        return new MetricResponse(
            entry.getId(),
            entry.getService(),
            entry.getRequestCount(),
            entry.getErrorCount(),
            entry.getAverageLatency(),
            entry.getTimestamp()
        );
    }

    private PagedMetricResponse mapToPagedMetricResponse(Page<MetricResponse> page) {
        return new PagedMetricResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    public MetricResponse saveMetric(MetricEntry entry) {
        MetricEntry saved = metricEntryRepository.save(entry);
        return mapToMetricResponse(saved);
    }

    public Page<MetricResponse> getAllMetrics(Pageable pageable) {
        return metricEntryRepository
            .findAll(pageable)
            .map(this::mapToMetricResponse);
    }

    public PagedMetricResponse getMetrics(String service, Long from, Long to, Pageable pageable) {
        boolean hasService = service != null && !service.isBlank();
        boolean hasFrom = from != null;
        boolean hasTo = to != null;

        if (hasFrom ^ hasTo) {
            throw new IllegalArgumentException("Both 'from' and 'to' must be provided together.");
        }

        if (hasFrom && hasTo && from > to) {
            throw new IllegalArgumentException("'from' timestamp must be less than or equal to 'to' timestamp.");
        }

        Page<MetricResponse> page;

        if (hasService && hasFrom && hasTo) {
            page = getMetricsByServiceAndTimestamp(service, from, to, pageable);
        } else if (hasFrom && hasTo) {
            page = getMetricsByTimestamp(from, to, pageable);
        } else if (hasService) {
            page = getMetricsByService(service, pageable);
        } else {
            page = getAllMetrics(pageable);
        }

        return mapToPagedMetricResponse(page);
    }

    public Page<MetricResponse> getMetricsByService(String service, Pageable pageable) {
        return metricEntryRepository
            .findByService(service, pageable)
            .map(this::mapToMetricResponse);
    }

    public Page<MetricResponse> getMetricsByTimestamp(Long from, Long to, Pageable pageable) {
        return metricEntryRepository
            .findByTimestampBetween(from, to, pageable)
            .map(this::mapToMetricResponse);
    }

    public Page<MetricResponse> getMetricsByServiceAndTimestamp(String service, Long from, Long to, Pageable pageable) {
        return metricEntryRepository
            .findByServiceAndTimestampBetween(service, from, to, pageable)
            .map(this::mapToMetricResponse);
    }
}
