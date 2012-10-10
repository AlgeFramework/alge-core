package com.sfdc.http.queue;

import com.ning.http.client.Cookie;
import com.sfdc.http.client.handler.StatefulHandler;
import com.sfdc.stats.StatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 9:20 PM
 *         <p/>
 *         An instance of the consumer will spawn one instance of the HttpClient.
 *         This is good insurance against any scalability bottlenecks in the HttpClient itself.
 *         We expect to have only a few instances of StreamingConsumer.
 */
public class StreamingConsumer extends GenericConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingConsumer.class);

    public StreamingConsumer(BlockingQueue queue, Semaphore concurrencyPermit, boolean collectQueueStats, StatsManager statsManager1) {
        super(queue, concurrencyPermit, collectQueueStats, statsManager1);
        LOGGER.info("Started Request Consumer.  Max Concurrency: " + concurrencyPermit.availablePermits());
    }

    @Override
    public void processWorkItem(WorkItem work) {
        String instance = work.getInstance();
        String sessionId = work.getSessionId();
        String clientID = work.getClientId();
        List<Cookie> cookies = work.getCookies();
        StatefulHandler handler = work.getHandler();
        String subscriptionChannel = work.getChannel();
        WorkItem.Operation operation = work.getOperation();
        switch (operation) {
            case HANDSHAKE:
                LOGGER.debug("Beginning handshake");
                httpClient.streamingHandshake(instance, sessionId, handler);
                break;
            case CONNECT:
                LOGGER.debug("Beginning connect");
                httpClient.streamingConnect(instance, sessionId, cookies, clientID, handler);
                break;
            case SUBSCRIBE:
                LOGGER.debug("Beginning subscribe");
                httpClient.streamingSubscribe(instance, sessionId, cookies, clientID, subscriptionChannel, handler);
                break;
            case DISCONNECT:
                break;
            case UNSUBSCRIBE:
                break;
        }
    }

}
