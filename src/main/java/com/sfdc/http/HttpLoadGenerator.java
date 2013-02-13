package com.sfdc.http;

import com.sfdc.http.queue.ProducerConsumerQueue;
import com.sfdc.http.queue.ProducerConsumerQueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author psrinivasan
 *         Date: 11/29/12
 *         Time: 10:52 AM
 */
public class HttpLoadGenerator {
    private static final Logger LOGGER;

    private static HttpLoadGenerator ourInstance;
    private Thread pcThread;

    static {
        LOGGER = LoggerFactory.getLogger(HttpLoadGenerator.class);
        try {
            ourInstance = new HttpLoadGenerator();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("FATAL: COULD NOT INITIALIZE LOAD GENERATOR!");
        }
    }

    private ProducerConsumerQueueConfig producerConsumerQueueConfig;
    private ProducerConsumerQueue producerConsumerQueue;

    public static HttpLoadGenerator getInstance() {
        return ourInstance;
    }

    private HttpLoadGenerator() throws Exception {
        producerConsumerQueueConfig = new ProducerConsumerQueueConfig(getConfigFileLocation());
        producerConsumerQueue = new ProducerConsumerQueue(producerConsumerQueueConfig);
    }

    public HttpLoadGenerator initialize() {
        return this;
    }

    public void start() {
        pcThread = new Thread(producerConsumerQueue);
        pcThread.start();
    }

    public boolean isStarted() {
        /*
         * at the moment this only checks to see if the
         * ProducerConsumerQueue is started.
         */
        return producerConsumerQueue.isStarted();
    }

    public void stop() {
        producerConsumerQueue.gracefulShutdown();
    }

    public ProducerConsumerQueue getProducerConsumerQueue() {
        return producerConsumerQueue;
    }

    private String getConfigFileLocation() {
        return System.getProperty("config.properties", "src/main/resources/config.properties");
    }
}
