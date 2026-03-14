package com.temirlan.pulsewatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.model.MetricEntry;

public interface MetricEntryRepository extends JpaRepository<MetricEntry, Long> {
    
    Page<MetricEntry> findByService(String service, Pageable pageable);

    Page<MetricEntry> findByTimestampBetween(Long from, Long to, Pageable pageable);

    Page<MetricEntry> findByServiceAndTimestampBetween(String service, Long from, Long to, Pageable pageable);

    List<MetricEntry> findByServiceAndTimestampBetween(String service, Long from, Long to);

    Optional<MetricEntry> findTopByServiceOrderByTimestampDesc(String service);

    List<MetricEntry> findDistinctByOrderByServiceAsc();

    @Query("SELECT DISTINCT m.service FROM MetricEntry m")
    List<String> findDistinctServices();
}
