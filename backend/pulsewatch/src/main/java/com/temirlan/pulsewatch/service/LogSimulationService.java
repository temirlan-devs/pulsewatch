package com.temirlan.pulsewatch.service;

import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.temirlan.pulsewatch.dto.LogIngestionRequest;
import com.temirlan.pulsewatch.enums.LogLevel;

@Service
public class LogSimulationService {
    
    private final LogEntryService logEntryService;
    private final Random random = new Random();

    public LogSimulationService(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @Scheduled(fixedRate = 7000)
    public void simulateLogs() {
        simulateService("auth-service");
        simulateService("payment-service");
        simulateService("order-service");
    }

    private void simulateService(String service) {
        boolean errorLog = random.nextInt(10) == 0;

        LogIngestionRequest request = new LogIngestionRequest();
        request.setService(service);
        request.setTimestamp(System.currentTimeMillis());

        if (errorLog) {
            request.setLevel(LogLevel.ERROR);

            String[] errorMessages = {
                "database timeout",
                "authentication failure",
                "downstream service timeout",
                "database connection refused"
            };

            request.setMessage(errorMessages[random.nextInt(errorMessages.length)]);
        } else {
            request.setLevel(LogLevel.INFO);

            String[] infoMessages = {
                "request processed successfully",
                "background task completed",
                "heaalth check passed",
                "cache refreshed"
            };

            request.setMessage(infoMessages[random.nextInt(infoMessages.length)]);
        }

        logEntryService.saveLog(request);
    }

}
