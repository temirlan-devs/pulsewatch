package com.temirlan.pulsewatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.temirlan.pulsewatch.model.AlertEntry;

public interface AlertEntryRepository extends JpaRepository<AlertEntry, Long> {

    Optional<AlertEntry> findTopByServiceOrderByTimestampDesc(String service);
    
}
