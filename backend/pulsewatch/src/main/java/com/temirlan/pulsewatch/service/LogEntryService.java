package com.temirlan.pulsewatch.service;

import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

@Service
public class LogEntryService {
   private final LogEntryRepository logEntryRepository;
   
   public LogEntryService(LogEntryRepository logEntryRepository) {
    this.logEntryRepository = logEntryRepository;
   }

   public LogEntry createHealthLog() {
    LogEntry log = new LogEntry();
    log.setLevel("INFO");
    log.setMessage("Health endpoint called");
    log.setService("pulsewatch-backend");
    log.setTimestamp(System.currentTimeMillis());

    return logEntryRepository.save(log);
   }

   public LogEntry saveLog(LogEntry logEntry) {
    return logEntryRepository.save(logEntry);
   }
}
