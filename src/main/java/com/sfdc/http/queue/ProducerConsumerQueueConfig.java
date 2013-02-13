package com.sfdc.http.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/17/12
 *         Time: 2:59 PM
 */
public class ProducerConsumerQueueConfig {

    public final int concurrency;
    public final boolean collectQueueStats;
    public final boolean collectConcurrencyPermitStats;
    public final long runtime;
    public final Date endDate;
    /*
     * concurrencyPermit sets the maximum concurrency of a particular
     * concurrencyPermit is interesting.  It's initialized in this class, and it's permits are acquired in the
     * GenericConsumer, and are released in the ThrottlingGenericAsyncHandler.
     */
    private final Semaphore concurrencyPermit;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConsumerQueueConfig.class);

    public Semaphore getConcurrencyPermit() {
        return concurrencyPermit;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public ProducerConsumerQueueConfig(int concurrency,
                                       boolean collectQueueStats,
                                       long runtime,
                                       boolean collectConcurrencyPermitStats
    ) {
        this.concurrency = concurrency;
        this.concurrencyPermit = new Semaphore(concurrency);
        this.collectQueueStats = collectQueueStats;
        this.collectConcurrencyPermitStats = collectConcurrencyPermitStats;
        this.runtime = runtime;
        this.endDate = new Date(new Date().getTime() + runtime);
    }

    public ProducerConsumerQueueConfig(String fileName) throws IOException {
        Properties p = loadConfigProperties(fileName);
        concurrency = Integer.parseInt(p.getProperty("http_client.max.concurrency", "10000"));
        this.concurrencyPermit = new Semaphore(concurrency);
        runtime = Long.parseLong(p.getProperty("runtime"));
        this.endDate = new Date(new Date().getTime() + runtime);
        System.out.println("run time: " + runtime);
        LOGGER.info("run time: " + runtime);
        collectQueueStats = Boolean.parseBoolean(p.getProperty("collect_queue_stats"));
        collectConcurrencyPermitStats = Boolean.parseBoolean(p.getProperty("collect_concurrency_stats"));

        System.out.println("max concurrency = " + concurrency);
        LOGGER.info("max concurrency = " + concurrency);
        System.out.println("Queue stats collection = " + collectQueueStats);
        LOGGER.info("Queue stats collection = " + collectQueueStats);
    }

    public Properties loadConfigProperties(String fileName) throws IOException {
        Properties p = new Properties();
        //TODO: better way to specify resource paths
        p.load(new FileInputStream(fileName));
        return p;
    }
}
