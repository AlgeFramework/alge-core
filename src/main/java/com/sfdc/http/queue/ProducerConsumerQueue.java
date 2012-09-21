package com.sfdc.http.queue;

import poc.SessionIdReader;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/17/12
 *         Time: 2:57 PM
 */
public class ProducerConsumerQueue {
    private final ProducerConsumerQueueConfig config;
    private final Producer producer;
    private final Consumer consumer;
    private final LinkedBlockingDeque<WorkItem> queue;
    private final Semaphore concurrencyPermit;

    public ProducerConsumerQueue(ProducerConsumerQueueConfig config) throws Exception {
        this.config = config;
        queue = new LinkedBlockingDeque<WorkItem>();
        SessionIdReader sessionIdReader = config.getSessionIdReader(config.sessionsFile);
        producer = new Producer(queue, config.numHandshakes, sessionIdReader, config.instance);
        concurrencyPermit = new Semaphore(config.concurrency);
        consumer = new Consumer(queue, concurrencyPermit);
    }

    public void start() throws Exception {

        //Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        //producerThread.start();
        consumerThread.start();
        System.out.println("started producers and consumers");
        //producerThread.join();
        consumerThread.join();
    }
}
