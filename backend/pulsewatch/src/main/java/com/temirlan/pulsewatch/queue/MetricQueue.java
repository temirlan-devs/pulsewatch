package com.temirlan.pulsewatch.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MetricQueue { 
    public static final BlockingQueue<QueuedMetricRequest> QUEUE = new LinkedBlockingDeque<>();
}
