package com.temirlan.pulsewatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.model.LogEntry;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    
    Page<LogEntry> findByService(String service, Pageable pageable);

    Page<LogEntry> findByTimestampBetween(Long from, Long to, Pageable pageable);

    Page<LogEntry> findByServiceAndTimestampBetween(String service, Long from, Long to, Pageable pageable);

    Optional<LogEntry> findTopByServiceOrderByTimestampDesc(String service);

}
