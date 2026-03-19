package com.temirlan.pulsewatch.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServiceInsightsResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.enums.LogLevel;
import com.temirlan.pulsewatch.enums.ServiceStatus;
import com.temirlan.pulsewatch.model.LogEntry;
import com.temirlan.pulsewatch.repository.LogEntryRepository;

public class ServiceInsightsServiceTest {
    @Test
    void getServiceInsights_shouldUseLogSignalsInLikelyCause() {
        ServiceHealthService healthService = Mockito.mock(ServiceHealthService.class);
        ServicePredictionService predictionService = Mockito.mock(ServicePredictionService.class);
        LogEntryRepository logRepo = Mockito.mock(LogEntryRepository.class);

        ServiceInsightsService service = new ServiceInsightsService(
                healthService,
                predictionService,
                logRepo);

        ReflectionTestUtils.setField(service, "freshnessThresholdMinutes", 5L);
        ReflectionTestUtils.setField(service, "logWindowMultipler", 3L);
        ReflectionTestUtils.setField(service, "logLimit", 100);

        String serviceName = "auth-service";

        ServiceHealthResponse health = new ServiceHealthResponse(
                serviceName,
                ServiceStatus.ERROR,
                0.10,
                600.0,
                1000L,
                1000L,
                "2026-01-01 12:00:00",
                "2026-01-01 12:00:00",
                1L,
                0L);

        ServicePredictionResponse prediction = new ServicePredictionResponse(
                serviceName,
                "ERROR",
                0.9,
                0.9,
                "Prediction reason");

        LogEntry log = new LogEntry();
        log.setService(serviceName);
        log.setLevel(LogLevel.ERROR);
        log.setMessage("database timeout occurred");
        log.setTimestamp(System.currentTimeMillis());

        when(healthService.getServiceHealth(serviceName)).thenReturn(health);
        when(predictionService.predictServiceRisk(serviceName)).thenReturn(prediction);
        when(logRepo.findByServiceAndTimestampBetween(
                Mockito.eq(serviceName),
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(log)));

        ServiceInsightsResponse result = service.getServiceInsights(serviceName);

        assertTrue(result.getLikelyCause().toLowerCase().contains("timeout"));
        assertTrue(result.getLikelyCause().toLowerCase().contains("database"));
    }
}