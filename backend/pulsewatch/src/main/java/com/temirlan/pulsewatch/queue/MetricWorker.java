package com.temirlan.pulsewatch.queue;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import com.temirlan.pulsewatch.service.MetricEntryService;

@Component
public class MetricWorker {
    
    private final MetricEntryService metricEntryService;

    public MetricWorker(MetricEntryService metricEntryService) {
        this.metricEntryService = metricEntryService;
    }

    @PostConstruct
    public void startWorked() {
        Thread workerThread = new Thread(() -> {
            while (true) {
                try {
                    QueuedMetricRequest queued = MetricQueue.QUEUE.take();

                    try {
                        metricEntryService.saveMetric(queued.getRequest());
                    } catch (Exception e) {

                        int retries = queued.getRetryCount();

                        if (retries < 3) {
                            System.out.println("Retrying meitrc, attempt: " + (retries + 1));
                            MetricQueue.QUEUE.offer(new QueuedMetricRequest(queued.getRequest(), retries + 1));
                        } else {
                            System.err.println("Metric failed after retries: " + queued.getRequest());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        workerThread.setDaemon(true);
        workerThread.start();
    }

}
