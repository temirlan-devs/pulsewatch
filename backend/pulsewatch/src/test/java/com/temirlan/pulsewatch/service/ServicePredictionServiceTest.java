package com.temirlan.pulsewatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.temirlan.pulsewatch.dto.MetricWindowAnalysisResponse;
import com.temirlan.pulsewatch.dto.ServiceHealthResponse;
import com.temirlan.pulsewatch.dto.ServicePredictionResponse;
import com.temirlan.pulsewatch.enums.ServiceStatus;

public class ServicePredictionServiceTest {
    @Test
    void predictServiceRisk_shouldReturnNoDataWhenNoCurrentMetrics() {
        MetricEntryService metricEntryService = Mockito.mock(MetricEntryService.class);
        ServiceHealthService serviceHealthService = Mockito.mock(ServiceHealthService.class);
        MetricWindowAnalysisService metricWindowAnalysisService = Mockito.mock(MetricWindowAnalysisService.class);

        ServicePredictionService service = new ServicePredictionService(
                metricEntryService,
                serviceHealthService,
                metricWindowAnalysisService);

        String serviceName = "auth-service";

        ServiceHealthResponse healthResponse = new ServiceHealthResponse(
                serviceName,
                ServiceStatus.NO_DATA,
                0.0,
                0.0,
                0L,
                0L,
                null,
                null,
                0L,
                0L);

        MetricWindowAnalysisResponse analysisResponse = new MetricWindowAnalysisResponse(
                false,
                false,
                0.0,
                0.0,
                0.0,
                0.0);

        when(serviceHealthService.getServiceHealth(serviceName)).thenReturn(healthResponse);
        when(metricWindowAnalysisService.analyze(serviceName)).thenReturn(analysisResponse);

        ServicePredictionResponse result = service.predictServiceRisk(serviceName);

        assertEquals("NO_DATA", result.getPredictedStatus());
        assertEquals(0.0, result.getRiskScore(), 0.0001);
        assertEquals(0.2, result.getConfidence(), 0.0001);
        assertEquals("No recent metrics available for prediction", result.getReason());
    }

    @Test
    void predictServiceRisk_shouldIncreaseRiskWhenCurrentMetricsWorsenAndAlertsExist() {
        MetricEntryService metricEntryService = Mockito.mock(MetricEntryService.class);
        ServiceHealthService serviceHealthService = Mockito.mock(ServiceHealthService.class);
        MetricWindowAnalysisService metricWindowAnalysisService = Mockito.mock(MetricWindowAnalysisService.class);
        ServicePredictionService service = new ServicePredictionService(
                metricEntryService,
                serviceHealthService,
                metricWindowAnalysisService);

        String serviceName = "auth-service";

        ServiceHealthResponse healthResponse = new ServiceHealthResponse(
                serviceName,
                ServiceStatus.WARN,
                0.06,
                600.0,
                1000L,
                1000L,
                "2026-03-19 12:00:00",
                "2026-03-19 12:00:00",
                1L,
                0L);

        MetricWindowAnalysisResponse analysisResponse = new MetricWindowAnalysisResponse(
                true,
                true,
                0.06,
                0.02,
                600.0,
                300.0);

        when(serviceHealthService.getServiceHealth(serviceName)).thenReturn(healthResponse);
        when(metricWindowAnalysisService.analyze(serviceName)).thenReturn(analysisResponse);

        ServicePredictionResponse result = service.predictServiceRisk(serviceName);

        assertEquals("ERROR", result.getPredictedStatus());
        assertEquals(0.8, result.getRiskScore(), 0.0001);
        assertEquals(0.9, result.getConfidence(), 0.0001);
        assertEquals(
                "Error rate is more than 2x the baseline; Latency is more than 1.5x the baseline; Open alerts are still active; Service is already in WARN state",
                result.getReason());
    }
}