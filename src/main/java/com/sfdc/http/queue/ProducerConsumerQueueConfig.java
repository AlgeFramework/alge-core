package com.sfdc.http.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import poc.SessionIdReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public final int numHandshakes;
    public final String sessionsFile;
    public final String[] topics;
    public final String instance;
    public final boolean collectQueueStats;
    public final boolean collectConcurrencyPermitStats;
    public final long runtime;
    public final Date endDate;
    /*
     * concurrencyPermit is interesting.  It's initialized in this class, and it's permits are acquired in the
     * GenericConsumer, and are released in the ThrottlingGenericAsyncHandler.
     */
    private final Semaphore concurrencyPermit;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConsumerQueueConfig.class);


    public int getMaxHandshakeConcurrency() {
        return maxHandshakeConcurrency;
    }

    public final int maxHandshakeConcurrency;

    public Semaphore getConcurrencyPermit() {
        return concurrencyPermit;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public int getNumHandshakes() {
        return numHandshakes;
    }

    public String getSessionsFile() {
        return sessionsFile;
    }

    public String[] getTopics() {
        return topics;
    }

    public String getInstance() {
        return instance;
    }

    public ProducerConsumerQueueConfig(int concurrency, int numHandshakes, String sessionsFile, String[] topics,
                                       String instance,
                                       int maxHandshakeConcurrency,
                                       boolean collectQueueStats,
                                       long runtime,
                                       boolean collectConcurrencyPermitStats
    ) {
        this.concurrency = concurrency;
        this.concurrencyPermit = new Semaphore(concurrency);
        this.numHandshakes = numHandshakes;
        this.sessionsFile = sessionsFile;
        this.topics = topics;
        this.instance = instance;
        this.maxHandshakeConcurrency = maxHandshakeConcurrency;
        this.collectQueueStats = collectQueueStats;
        this.collectConcurrencyPermitStats = collectConcurrencyPermitStats;
        this.runtime = runtime;
        this.endDate = new Date(new Date().getTime() + runtime);
    }

    public ProducerConsumerQueueConfig(String fileName) throws IOException {
        Properties p = loadConfigProperties(fileName);
        concurrency = Integer.parseInt(p.getProperty("http_client.max.concurrency", "10000"));
        this.concurrencyPermit = new Semaphore(concurrency);
        numHandshakes = Integer.parseInt(p.getProperty("producer.handshake.count", "10000"));
        sessionsFile = p.getProperty("sessions.file", "NO_SESSIONS_FILE_SPECIFIED_IN_config.properties");
        instance = p.getProperty("instance", "NO_INSTANCE_SPECIFIED_IN_config.properties");
        runtime = Long.parseLong(p.getProperty("runtime"));
        this.endDate = new Date(new Date().getTime() + runtime);
        System.out.println("run time(applies to connects): " + runtime);
        LOGGER.info("run time(applies to connects): " + runtime);
        String topicList = p.getProperty("channels");
        topics = topicList.split(",");
        maxHandshakeConcurrency = Integer.parseInt(p.getProperty("max.handshake.concurrency"));
        collectQueueStats = Boolean.parseBoolean(p.getProperty("collect_queue_stats"));
        collectConcurrencyPermitStats = Boolean.parseBoolean(p.getProperty("collect_concurrency_stats"));

        System.out.println("max concurrency = " + concurrency);
        LOGGER.info("max concurrency = " + concurrency);
        System.out.println("handshake count = " + numHandshakes);
        LOGGER.info("handshake count = " + numHandshakes);
        System.out.println("handshake concurrency = " + maxHandshakeConcurrency);
        LOGGER.info("handshake concurrency = " + maxHandshakeConcurrency);
        System.out.println("Queue stats collection = " + collectQueueStats);
        LOGGER.info("Queue stats collection = " + collectQueueStats);
    }

    public SessionIdReader getSessionIdReader(String sessionIdFile) throws FileNotFoundException {
        return new SessionIdReader(sessionIdFile);
    }

    public SessionIdReader getSessionIdReader() throws FileNotFoundException {
        return new SessionIdReader(sessionsFile);
    }

    public Properties loadConfigProperties(String fileName) throws IOException {
        Properties p = new Properties();
        //TODO: better way to specify resource paths
        p.load(new FileInputStream(fileName));
        return p;
    }
}
