package com.temirlan.pulsewatch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.AlertResponse;
import com.temirlan.pulsewatch.model.AlertEntry;
import com.temirlan.pulsewatch.repository.AlertEntryRepository;

@Service
public class AlertService {
    private final AlertEntryRepository alertEntryRepository;

    public AlertService(AlertEntryRepository alertEntryRepository) {
        this.alertEntryRepository = alertEntryRepository;
    }

    private AlertResponse mapToAlertResponse(AlertEntry entry) {
        return new AlertResponse(
            entry.getId(), 
            entry.getService(), 
            entry.getStatus(), 
            entry.getReason(), 
            entry.getTimestamp()
        );
    }

    public AlertEntry createAlert(String service, String status, String reason) {
        AlertEntry alertEntry = new AlertEntry();

        alertEntry.setService(service);
        alertEntry.setStatus(status);
        alertEntry.setReason(reason);
        alertEntry.setTimestamp(System.currentTimeMillis());
        
        return alertEntryRepository.save(alertEntry);
    }

    public void createAlertIfStatusChanged(String service, String status, String reason) {
        Optional<AlertEntry> latestAlert = alertEntryRepository.findTopByServiceOrderByTimestampDesc(service);
        
        if(latestAlert.isPresent() && status.equals(latestAlert.get().getStatus())) {
            return;
        }

        createAlert(service, status, reason);
    }

    public List<AlertResponse> getAllAlerts() {
        return alertEntryRepository.findAll()
                .stream()
                .map(this::mapToAlertResponse)
                .toList();
    }
}
