package com.temirlan.pulsewatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.AlertEntry;

public interface AlertEntryRepository extends JpaRepository<AlertEntry, Long>, JpaSpecificationExecutor<AlertEntry> {

    Optional<AlertEntry> findTopByServiceOrderByTimestampDesc(String service);
    
    Optional<AlertEntry> findTopByServiceAndTypeAndStatusOrderByTimestampDesc(String service, AlertType type, String status);
    
}
