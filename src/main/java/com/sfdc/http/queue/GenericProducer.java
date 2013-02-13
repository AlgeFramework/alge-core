package com.sfdc.http.queue;

import com.sfdc.stats.StatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 11:01 AM
 *         Pushes requests into the work queue for consumption by
 *         consumers.
 */
public class GenericProducer implements ProducerInterface {

    private final BlockingQueue<HttpWorkItem> queue;
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericProducer.class);
    private volatile boolean run;
    private final boolean collectQueueStats;
    private final StatsManager statsManager;

    public GenericProducer(BlockingQueue<HttpWorkItem> queue, boolean collectQueueStats, StatsManager statsManager) {
        this.statsManager = statsManager;
        run = true;
        this.queue = queue;
        this.collectQueueStats = collectQueueStats;

        if (collectQueueStats && statsManager != null) {
            initializeQueueStatsCollection();
        }
    }

    private void initializeQueueStatsCollection() {
        statsManager.createCustomStats(ProducerConsumerQueue.QUEUE_STATS_METRIC);
    }

    @Override
    public void publish(HttpWorkItem w) {
        if (!run) {
            LOGGER.warn("PRODUCER IS STOPPED BUT ATTEMPT TO PUBLISH WAS MADE");
            return;
        }
        if (!queue.add(w)) {
            LOGGER.warn("Failed to publish request to queue");
        } else {
            if (collectQueueStats && statsManager != null) {
                statsManager.incrementCustomStats(ProducerConsumerQueue.QUEUE_STATS_METRIC);
            }
        }
    }

    @Override
    public void stop() {
        run = false;
    }
}
