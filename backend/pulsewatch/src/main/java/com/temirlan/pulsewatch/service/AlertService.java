package com.temirlan.pulsewatch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.AlertResponse;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.AlertEntry;
import com.temirlan.pulsewatch.repository.AlertEntryRepository;

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
            entry.getTimestamp()
        );
    }

    public AlertEntry createAlert(String service, AlertType type, String status, String reason) {
        AlertEntry alertEntry = new AlertEntry();

        alertEntry.setService(service);
        alertEntry.setType(type);
        alertEntry.setStatus(status);
        alertEntry.setReason(reason);
        alertEntry.setTimestamp(System.currentTimeMillis());
        
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

    public List<AlertResponse> getAlertByService(String service) {
        return alertEntryRepository.findByService(service)
                .stream()
                .map(this::mapToAlertResponse)
                .toList();
    }

    public List<AlertResponse> getAllAlerts() {
        return alertEntryRepository.findAll()
                .stream()
                .map(this::mapToAlertResponse)
                .toList();
    }
}
