package com.temirlan.pulsewatch.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.model.MetricEntry;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;
import com.temirlan.pulsewatch.dto.MetricIngestionRequest;
import com.temirlan.pulsewatch.dto.MetricResponse;
import com.temirlan.pulsewatch.dto.MetricsSummaryResponse;
import com.temirlan.pulsewatch.dto.PagedMetricResponse;

@Service
public class MetricEntryService {

    private final MetricEntryRepository metricEntryRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MetricEntryService(MetricEntryRepository metricEntryRepository) {
        this.metricEntryRepository = metricEntryRepository;
    }

    private PagedMetricResponse mapToPagedMetricResponse(Page<MetricResponse> page) {

        return new PagedMetricResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    private MetricResponse mapToMetricResponse(MetricEntry entry) {

        String readableTimestamp = Instant.ofEpochMilli(entry.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMATTER);

        return new MetricResponse(
                entry.getId(),
                entry.getService(),
                entry.getRequestCount(),
                entry.getErrorCount(),
                entry.getAverageLatency(),
                entry.getTimestamp(),
                readableTimestamp
            );
    }

    public MetricResponse saveMetric(MetricIngestionRequest request) {
        MetricEntry entry = new MetricEntry();
        entry.setService(request.getService());
        entry.setRequestCount(request.getRequestCount());
        entry.setErrorCount(request.getErrorCount());
        entry.setAverageLatency(request.getAverageLatency());
        entry.setTimestamp(request.getTimestamp());

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

    public MetricsSummaryResponse getSummary(String service, long from, long to) {
        List<MetricEntry> entries = metricEntryRepository
                .findByServiceAndTimestampBetween(service, from, to);

        if (entries.isEmpty()) {
            return new MetricsSummaryResponse(
                    service,
                    0,
                    0,
                    0.0,
                    0.0,
                    from,
                    to);
        }

        long totalRequests = entries.stream()
                .mapToLong(MetricEntry::getRequestCount)
                .sum();

        long totalErrors = entries.stream()
                .mapToLong(MetricEntry::getErrorCount)
                .sum();

        double averageLatency = entries.stream()
                .mapToDouble(MetricEntry::getAverageLatency)
                .average()
                .orElse(0.0);

        double errorRate = totalRequests == 0
                ? 0.0
                : (double) totalErrors / totalRequests;

        return new MetricsSummaryResponse(
                service,
                totalRequests,
                totalErrors,
                errorRate,
                averageLatency,
                from,
                to
        );

    }
}
