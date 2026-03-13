package com.temirlan.pulsewatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.AlertEntry;

public interface AlertEntryRepository extends JpaRepository<AlertEntry, Long> {

    Optional<AlertEntry> findTopByServiceOrderByTimestampDesc(String service);

    List<AlertEntry> findByService(String service);
    
    Optional<AlertEntry> findTopByServiceAndTypeAndStatusOrderByTimestampDesc(String service, AlertType type, String status);

    List<AlertEntry> findByType(AlertType type);
    
}
