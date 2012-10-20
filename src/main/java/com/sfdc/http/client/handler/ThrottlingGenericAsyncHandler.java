package com.sfdc.http.client.handler;

import com.sfdc.http.queue.ProducerConsumerQueue;
import com.sfdc.stats.StatsManager;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 9:52 PM
 */
public class ThrottlingGenericAsyncHandler extends GenericAsyncHandler {
    private final Semaphore concurrencyPermit;
    private final StatsManager statsManager;

    public ThrottlingGenericAsyncHandler(Semaphore concurrencyPermit, StatsManager statsManager) {
        super();
        this.concurrencyPermit = concurrencyPermit;
        this.statsManager = statsManager;
    }

    @Override
    public Object onCompleted() throws Exception {
        concurrencyPermit.release();
        if (statsManager != null) {
            statsManager.decrementCustomStats(ProducerConsumerQueue.CONCURRENCY_STATS_METRIC);
        }
        return super.onCompleted();
    }
}
