package com.temirlan.pulsewatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.model.MetricEntry;

public interface MetricEntryRepository extends JpaRepository<MetricEntry, Long> {
    
    Page<MetricEntry> findByService(String service, Pageable pageable);

    Page<MetricEntry> findByTimestampBetween(Long from, Long to, Pageable pageable);

    Page<MetricEntry> findByServiceAndTimestampBetween(String service, Long from, Long to, Pageable pageable);

}
