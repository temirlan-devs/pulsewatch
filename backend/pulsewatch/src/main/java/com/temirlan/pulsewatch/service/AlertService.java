package com.temirlan.pulsewatch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.AlertResponse;
import com.temirlan.pulsewatch.enums.AlertStatus;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.AlertEntry;
import com.temirlan.pulsewatch.repository.AlertEntryRepository;
import com.temirlan.pulsewatch.repository.AlertEntrySpecification;

@Service
public class AlertService {
    private final AlertEntryRepository alertEntryRepository;
    private static final long ALERT_COOLDOWN_MS = 10 * 60 * 1000;

    public AlertService(AlertEntryRepository alertEntryRepository) {
        this.alertEntryRepository = alertEntryRepository;
    }

    private AlertResponse mapToAlertResponse(AlertEntry entry) {
        return new AlertResponse(
            entry.getId(), 
            entry.getService(), 
            entry.getType(),
            entry.getStatus(), 
            entry.getReason(), 
            entry.getTimestamp(),
            entry.getAlertStatus()
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
        
        return alertEntryRepository.save(alertEntry);
    }

    public void createAlertIfStatusChanged(String service, AlertType type, String status, String reason) {
        Optional<AlertEntry> latestAlert = alertEntryRepository.findTopByServiceAndTypeAndStatusOrderByTimestampDesc(service, type, status);
        
        if(latestAlert.isPresent()) {
            AlertEntry alert = latestAlert.get();

            boolean sameType = type.equals(alert.getType());
            boolean sameStatus = status.equals(alert.getStatus());
            boolean withinCooldown = (System.currentTimeMillis() - alert.getTimestamp() < ALERT_COOLDOWN_MS);

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

    public List<AlertResponse> getAlerts(String service, AlertType type, String status, Long from, Long to) {
        Specification<AlertEntry> spec = AlertEntrySpecification.withFilters(service, type, status, from, to);

        return alertEntryRepository.findAll(spec)
                .stream()
                .map(this::mapToAlertResponse)
                .toList();
    }

}
