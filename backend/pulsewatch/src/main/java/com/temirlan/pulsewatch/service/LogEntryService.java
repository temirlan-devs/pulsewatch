package com.temirlan.pulsewatch.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.temirlan.pulsewatch.dto.LogIngestionRequest;
import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.repository.LogEntryRepository;

@Service
public class LogEntryService {
   private final LogEntryRepository logEntryRepository;
   
   public LogEntryService(LogEntryRepository logEntryRepository) {
    this.logEntryRepository = logEntryRepository;
   }

   public void createHealthLog() {
    //LogEntry log = new LogEntry();
    //log.setLevel("ERROR");
    //log.setMessage("Health endpoint called");
    //log.setService("pulsewatch-backend");
    //log.setTimestamp(System.currentTimeMillis());

    //return logEntryRepository.save(log);
   }

   public LogEntry saveLog(LogIngestionRequest request) {
      LogEntry logEntry = new LogEntry();
      logEntry.setLevel(request.getLevel());
      logEntry.setMessage(request.getMessage());
      logEntry.setService(request.getService());
      logEntry.setTimestamp(request.getTimestamp());
    return logEntryRepository.save(logEntry);
   }

   public Page<LogEntry> getLogs(Pageable pageable) {
      return logEntryRepository.findAll(pageable);
   }

   public Page<LogEntry> getLogsByService(String service, Pageable pageable) {
      return logEntryRepository.findByService(service, pageable);
   }
}
