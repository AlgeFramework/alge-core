package com.sfdc.http.queue;

import com.sfdc.stats.StatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import poc.SessionIdReader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/17/12
 *         Time: 2:57 PM
 */
public class ProducerConsumerQueue implements Runnable {
    private final ProducerConsumerQueueConfig config;
    private final Producer producer;
    private ConsumerInterface consumer;
    //private final LinkedBlockingDeque<WorkItem> queue;
    private final BlockingQueue<WorkItem> queue;
    private final Semaphore concurrencyPermit;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConsumerQueue.class);
    private Thread consumerThread;
    public static final String QUEUE_STATS_METRIC = "Queue-Num-Elements";
    public static final String CONCURRENCY_STATS_METRIC = "Num-ConcurrencyPermits-Used";


    public ProducerConsumerQueue(ProducerConsumerQueueConfig config) throws Exception {
        this.config = config;
        queue = new LinkedBlockingDeque<WorkItem>();
        SessionIdReader sessionIdReader = config.getSessionIdReader(config.sessionsFile);
        producer = new Producer(queue, config.collectQueueStats, StatsManager.getInstance());
        concurrencyPermit = config.getConcurrencyPermit();
    }

    public ProducerConsumerQueue initializeConsumer() {
        consumer = new StreamingConsumer(queue, concurrencyPermit, config.collectQueueStats, StatsManager.getInstance(), config.collectConcurrencyPermitStats);
        return this;
    }

    public ProducerConsumerQueueConfig getConfig() {
        return config;
    }

    public Producer getProducer() {
        return producer;
    }

    public ConsumerInterface getConsumer() {
        return consumer;
    }

    public BlockingQueue<WorkItem> getQueue() {
        return queue;
    }

    public Semaphore getConcurrencyPermit() {
        return concurrencyPermit;
    }

    public void startPCQueue() throws Exception {

        //Thread producerThread = new Thread(producer);
        consumerThread = new Thread(consumer);

        //producerThread.start();
        consumerThread.start();
        System.out.println("started producers and consumers");
        //producerThread.join();
        //consumerThread.join();
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
            LOGGER.info("Some connections are in flight, waiting 10 seconds before shutting down");
            try {
                Thread.sleep(10000);
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
