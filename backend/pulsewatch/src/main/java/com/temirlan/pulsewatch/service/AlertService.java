package com.temirlan.pulsewatch.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.AlertResponse;
import com.temirlan.pulsewatch.dto.AlertStatsResponse;
import com.temirlan.pulsewatch.dto.PagedAlertResponse;
import com.temirlan.pulsewatch.enums.AlertSeverity;
import com.temirlan.pulsewatch.enums.AlertStatus;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.AlertEntry;
import com.temirlan.pulsewatch.repository.AlertEntryRepository;
import com.temirlan.pulsewatch.repository.AlertEntrySpecification;

@Service
public class AlertService {
    private final AlertEntryRepository alertEntryRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Value("${pulsewatch.alert.cooldown-ms}")
    private long alertCooldownMs;


    public AlertService(AlertEntryRepository alertEntryRepository) {
        this.alertEntryRepository = alertEntryRepository;
    }

    private PagedAlertResponse mapToPagedAlertResponse(Page<AlertResponse> page) {
        return new PagedAlertResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    private AlertResponse mapToAlertResponse(AlertEntry entry) {
        String readable = Instant.ofEpochMilli(entry.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMATTER);

        return new AlertResponse(
            entry.getId(), 
            entry.getService(), 
            entry.getType(),
            entry.getStatus(), 
            entry.getReason(), 
            entry.getTimestamp(),
            readable,
            entry.getAlertStatus(),
            entry.getAlertSeverity()
        );
    }

    public AlertEntry createAlert(String service, AlertType type, String status, String reason) {
        AlertEntry alertEntry = new AlertEntry();

        alertEntry.setService(service);
        alertEntry.setType(type);
        alertEntry.setStatus(status);
        alertEntry.setReason(reason);
        alertEntry.setTimestamp(System.currentTimeMillis());
        alertEntry.setAlertStatus(AlertStatus.OPEN);

        AlertSeverity severity;

        if ("ERROR".equals(status)) {
            severity = AlertSeverity.CRITICAL;
        } else if ("WARN".equals(status)) {
            severity = AlertSeverity.WARNING;
        } else {
            severity = AlertSeverity.INFO;
        }

        alertEntry.setAlertSeverity(severity);
        
        return alertEntryRepository.save(alertEntry);
    }

    public void createAlertIfStatusChanged(String service, AlertType type, String status, String reason) {
        Optional<AlertEntry> latestAlert = alertEntryRepository.findTopByServiceAndTypeAndStatusOrderByTimestampDesc(service, type, status);
        
        if(latestAlert.isPresent()) {
            AlertEntry alert = latestAlert.get();

            boolean sameType = type.equals(alert.getType());
            boolean sameStatus = status.equals(alert.getStatus());
            boolean withinCooldown = (System.currentTimeMillis() - alert.getTimestamp() < alertCooldownMs);

            if (sameType && sameStatus && withinCooldown) {
                return;
            }
        }
        
        createAlert(service, type, status, reason);
    }

    public AlertResponse acknowledgeAlert(Long id) {
        AlertEntry alert = alertEntryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setAlertStatus(AlertStatus.ACKNOWLEDGED);

        AlertEntry saved = alertEntryRepository.save(alert);

        return mapToAlertResponse(saved);
    }

    public AlertResponse resolveAlert(Long id) {
        AlertEntry alert = alertEntryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setAlertStatus(AlertStatus.RESOLVED);

        AlertEntry saved = alertEntryRepository.save(alert);

        return mapToAlertResponse(saved);
    }

    public AlertStatsResponse getAlertsStats() {
        List<AlertEntry> alerts = alertEntryRepository.findAll();

        long totalAlerts = alerts.size();

        long openAlerts = alertEntryRepository.countByAlertStatus(AlertStatus.OPEN);
        long acknowledgedAlerts = alertEntryRepository.countByAlertStatus(AlertStatus.ACKNOWLEDGED);
        long resolvedAlerts = alertEntryRepository.countByAlertStatus(AlertStatus.RESOLVED);

        long healthAlerts = alertEntryRepository.countByType(AlertType.HEALTH);
        long anomalyAlerts = alertEntryRepository.countByType(AlertType.ANOMALY);

        return new AlertStatsResponse(totalAlerts, openAlerts, acknowledgedAlerts, resolvedAlerts, healthAlerts, anomalyAlerts);
    }

    public PagedAlertResponse getAlerts(String service, AlertType type, String status, AlertSeverity severity, Long from, Long to, Pageable pageable) {
        Specification<AlertEntry> spec = AlertEntrySpecification.withFilters(service, type, status, severity, from, to);

        Page<AlertResponse> page = alertEntryRepository
                .findAll(spec, pageable)
                .map(this::mapToAlertResponse);

        return mapToPagedAlertResponse(page);
    }

}
