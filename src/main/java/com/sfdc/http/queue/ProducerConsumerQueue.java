package com.sfdc.http.queue;

import com.sfdc.stats.StatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author psrinivasan
 *         Date: 9/17/12
 *         Time: 2:57 PM
 */
public class ProducerConsumerQueue implements Runnable {
    private final ProducerConsumerQueueConfig config;
    private final ProducerInterface producer;
    private ConsumerInterface consumer;
    private final BlockingQueue<HttpWorkItem> queue;
    private final Semaphore concurrencyPermit;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConsumerQueue.class);
    private Thread consumerThread;
    public static final String QUEUE_STATS_METRIC = "Queue-Num-Elements";
    public static final String CONCURRENCY_STATS_METRIC = "Num-ConcurrencyPermits-Used";
    private AtomicBoolean isStarted;


    public ProducerConsumerQueue(ProducerConsumerQueueConfig config) throws Exception {
        this.config = config;
        queue = new LinkedBlockingDeque<HttpWorkItem>();
        producer = new GenericProducer(queue, config.collectQueueStats, StatsManager.getInstance());
        concurrencyPermit = config.getConcurrencyPermit();
        consumer = new GenericConsumer(queue, concurrencyPermit, config.collectQueueStats, StatsManager.getInstance(), config.collectConcurrencyPermitStats);
        isStarted = new AtomicBoolean(false);

    }


    public ProducerConsumerQueueConfig getConfig() {
        return config;
    }

    public ProducerInterface getProducer() {
        return producer;
    }

    public ConsumerInterface getConsumer() {
        return consumer;
    }

    public BlockingQueue<HttpWorkItem> getQueue() {
        return queue;
    }

    public Semaphore getConcurrencyPermit() {
        return concurrencyPermit;
    }

    public void startPCQueue() throws Exception {
        if (!isStarted.compareAndSet(false, true)) {
            LOGGER.error("DUPLICATE ATTEMPT TO START PRODUCER/CONSUMER!");
        }
        consumerThread = new Thread(consumer);
        consumerThread.start();
        System.out.println("Started Consumer");
    }

    public boolean isStarted() {
        return isStarted.get();
    }

    /*
     * So make sure there are no requests in flight, and then stop the producer and consumer.
     */
    public void gracefulShutdown() {
        //don't allow requests to be queued any more.
        producer.stop();
        if (queue.size() > 0) {
            //wait before stopping the consumer since work items are pending.
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted while waiting for producer queue to ");
                e.printStackTrace();
            }
        }
        if (queue.size() > 0) {
            LOGGER.warn("Queue did not drain after a wait period, proceeding to stop consumer.  May lose requests");
        }
        int num_permits_available = concurrencyPermit.availablePermits();
        if (num_permits_available < config.concurrency) {
            LOGGER.info("Some connections are in flight, " + (config.concurrency - num_permits_available) + " permits are given out.  waiting 10 seconds before shutting down");
            try {
                Thread.sleep(2100);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted while stopping producer/consumer/queue. May lose requests");
                e.printStackTrace();
            }
            consumer.stop();
        }
    }

    /*
    * Be rude and just kill the threads.
    */
    public void nuke() {
        consumerThread.interrupt();

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
        try {
            startPCQueue();
        } catch (Exception e) {
            LOGGER.error("Exception happened when attempting to start producer/consumer/queue.  Exiting.");
            e.printStackTrace();
            LOGGER.error("Bye.");
            System.exit(1);
        }
    }
}
