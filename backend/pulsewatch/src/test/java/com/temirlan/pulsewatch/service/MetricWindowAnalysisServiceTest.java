package com.temirlan.pulsewatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.temirlan.pulsewatch.dto.MetricWindowAnalysisResponse;
import com.temirlan.pulsewatch.model.MetricEntry;
import com.temirlan.pulsewatch.repository.MetricEntryRepository;

public class MetricWindowAnalysisServiceTest {
    @Test
    void analyze_shouldCalculateCurrentAndBaselineMetrics() {
        MetricEntryRepository metricEntryRepository = Mockito.mock(MetricEntryRepository.class);
        MetricWindowAnalysisService service = new MetricWindowAnalysisService(metricEntryRepository);

        String serviceName = "auth-service";
        long now = System.currentTimeMillis();

        MetricEntry currentMetric1 = new MetricEntry();
        currentMetric1.setService(serviceName);
        currentMetric1.setRequestCount(100);
        currentMetric1.setErrorCount(10);
        currentMetric1.setAverageLatency(300.0);
        currentMetric1.setTimestamp(now - 60_000);

        MetricEntry currentMetric2 = new MetricEntry();
        currentMetric2.setService(serviceName);
        currentMetric2.setRequestCount(100);
        currentMetric2.setErrorCount(0);
        currentMetric2.setAverageLatency(100.0);
        currentMetric2.setTimestamp(now - 120_000);

        MetricEntry baselineMetric1 = new MetricEntry();
        baselineMetric1.setService(serviceName);
        baselineMetric1.setRequestCount(200);
        baselineMetric1.setErrorCount(10);
        baselineMetric1.setAverageLatency(100.0);
        baselineMetric1.setTimestamp(now - 600_000);

        MetricEntry baselineMetric2 = new MetricEntry();
        baselineMetric2.setService(serviceName);
        baselineMetric2.setRequestCount(100);
        baselineMetric2.setErrorCount(0);
        baselineMetric2.setAverageLatency(200.0);
        baselineMetric2.setTimestamp(now - 900_000);

        when(metricEntryRepository.findByServiceAndTimestampBetween(
                Mockito.eq(serviceName),
                Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(List.of(currentMetric1, currentMetric2))
                .thenReturn(List.of(baselineMetric1, baselineMetric2));

        MetricWindowAnalysisResponse response = service.analyze(serviceName);

        assertTrue(response.isHasCurrentData());
        assertTrue(response.isHasBaselineData());

        assertEquals(0.05, response.getCurrentErrorRate(), 0.0001);
        assertEquals(10.0 / 300.0, response.getBaselineErrorRate(), 0.0001);

        assertEquals(200.0, response.getCurrentAverageLatency(), 0.0001);
        assertEquals(150.0, response.getBaselineAverageLatency(), 0.0001);
    }
}