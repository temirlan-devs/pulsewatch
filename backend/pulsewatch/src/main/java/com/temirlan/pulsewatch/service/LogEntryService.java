package com.temirlan.pulsewatch.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.dto.LogIngestionRequest;
import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.repository.LogEntryRepository;
import com.temirlan.pulsewatch.dto.LogResponse;
import com.temirlan.pulsewatch.dto.PagedLogResponse;

@Service
public class LogEntryService {
   private final LogEntryRepository logEntryRepository;

   public LogEntryService(LogEntryRepository logEntryRepository) {
      this.logEntryRepository = logEntryRepository;
   }

   public void createHealthLog() {
      // LogEntry log = new LogEntry();
      // log.setLevel("ERROR");
      // log.setMessage("Health endpoint called");
      // log.setService("pulsewatch-backend");
      // log.setTimestamp(System.currentTimeMillis());

      // return logEntryRepository.save(log);
   }

   private LogResponse mapToLogResponse(LogEntry logEntry) {
      return new LogResponse(
            logEntry.getId(),
            logEntry.getLevel(),
            logEntry.getMessage(),
            logEntry.getService(),
            logEntry.getTimestamp());
   }

   private PagedLogResponse mapToPagedLogResponse(Page<LogResponse> page) {
      return new PagedLogResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast());
   }

   public LogResponse saveLog(LogIngestionRequest request) {
      LogEntry logEntry = new LogEntry();
      logEntry.setLevel(request.getLevel());
      logEntry.setMessage(request.getMessage());
      logEntry.setService(request.getService());
      logEntry.setTimestamp(request.getTimestamp());

      LogEntry savedLog = logEntryRepository.save(logEntry);
      return mapToLogResponse(savedLog);
   }

   public PagedLogResponse getLogs(String service, Long from, Long to, Pageable pageable) {
      boolean hasService = service != null && !service.isBlank();
      boolean hasFrom = from != null;
      boolean hasTo = to != null;

      if (hasFrom ^ hasTo) {
         throw new IllegalArgumentException("Both 'from' and 'to' must be provided together.");
      }

      if (hasFrom && hasTo && from > to) {
         throw new IllegalArgumentException("'from' timestamp must be less than or equal to 'to' timestamp.");
      }

      Page<LogResponse> page;

      if (hasService && hasFrom && hasTo) {
         page = getLogsByServiceAndTimestamp(service, from, to, pageable);
      } else if (hasFrom && hasTo) {
         page = getLogsByTimeStamp(from, to, pageable);
      } else if (hasService) {
         page = getLogsByService(service, pageable);
      } else {
         page = getLogs(pageable);
      }

      return mapToPagedLogResponse(page);
   }

   public Page<LogResponse> getLogs(Pageable pageable) {
      return logEntryRepository
            .findAll(pageable)
            .map(this::mapToLogResponse);
   }

   public Page<LogResponse> getLogsByService(String service, Pageable pageable) {
      return logEntryRepository
            .findByService(service, pageable)
            .map(this::mapToLogResponse);
   }

   public Page<LogResponse> getLogsByTimeStamp(Long from, Long to, Pageable pageable) {
      return logEntryRepository
            .findByTimestampBetween(from, to, pageable)
            .map(this::mapToLogResponse);
   }

   public Page<LogResponse> getLogsByServiceAndTimestamp(String service, Long from, Long to, Pageable pageable) {
      return logEntryRepository
            .findByServiceAndTimestampBetween(service, from, to, pageable)
            .map(this::mapToLogResponse);
   }
}
