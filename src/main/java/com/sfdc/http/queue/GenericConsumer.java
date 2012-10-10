package com.sfdc.http.queue;

import com.sfdc.http.client.NingAsyncHttpClientImpl;
import com.sfdc.stats.StatsManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author psrinivasan
 *         Date: 10/4/12
 *         Time: 9:35 PM
 */
public abstract class GenericConsumer implements ConsumerInterface {
    protected final BlockingQueue<WorkItem> queue;
    protected final Semaphore concurrencyPermit;
    protected final NingAsyncHttpClientImpl httpClient;
    protected volatile boolean run;
    protected final boolean collectQueueStats;
    protected final StatsManager statsManager;

    public GenericConsumer(BlockingQueue queue, Semaphore concurrencyPermit, boolean collectQueueStats, StatsManager statsManager) {
        this.run = true;
        this.queue = queue;
        this.concurrencyPermit = concurrencyPermit;
        httpClient = new NingAsyncHttpClientImpl(concurrencyPermit);
        this.collectQueueStats = collectQueueStats;
        this.statsManager = statsManager;

        if (collectQueueStats && statsManager != null) {
            statsManager.createCustomStats(ProducerConsumerQueue.QUEUE_STATS_METRIC);
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (run) {
            try {
                /*
                 * Wait to make sure that one gets the concurrency permit since we don't want a higher load concurrency
                 * than specified.
                 */
                concurrencyPermit.acquire();
                /*
                 * wait for a bit, and if we don't find work to do,
                 * release our permit, and retry.
                 *  This ensures that we can stop if someone has called "stop"
                 *  and also reduces the chances that we will hog up
                 */
                WorkItem work = queue.poll(2, TimeUnit.SECONDS);
                if (work != null) {
                    if (collectQueueStats && statsManager != null) {
                        statsManager.incrementCustomStats(ProducerConsumerQueue.QUEUE_STATS_METRIC);
                    }
                    processWorkItem(work);
                } else {
                    concurrencyPermit.release();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void stop() {
        run = false;
    }
}
