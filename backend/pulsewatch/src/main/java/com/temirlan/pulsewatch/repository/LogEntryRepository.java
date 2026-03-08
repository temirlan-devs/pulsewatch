package com.temirlan.pulsewatch.repository;

import com.temirlan.pulsewatch.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    
}
