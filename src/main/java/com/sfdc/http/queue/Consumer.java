package com.sfdc.http.queue;

import com.ning.http.client.Cookie;
import com.sfdc.http.client.NingAsyncHttpClientImpl;
import com.sfdc.http.client.handler.StatefulHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 9:20 PM
 *         <p/>
 *         An instance of the consumer will spawn one instance of the HttpClient.
 *         This is good insurance against any scalability bottlenecks in the HttpClient itself.
 *         We expect to have only a few instances of Consumer.
 */
public class Consumer implements Runnable {
    private final BlockingQueue<WorkItem> queue;
    private final Semaphore concurrencyPermit;
    private final NingAsyncHttpClientImpl httpClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private volatile boolean run;

    public Consumer(BlockingQueue queue, Semaphore concurrencyPermit) {
        this.run = true;
        this.concurrencyPermit = concurrencyPermit;
        httpClient = new NingAsyncHttpClientImpl(concurrencyPermit);
        this.queue = queue;
        LOGGER.info("Started Request Consumer.  Max Concurrency: " + concurrencyPermit.availablePermits());
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
                    processWorkItem(work);
                } else {
                    concurrencyPermit.release();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

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
                LOGGER.info("Beginning handshake");
                httpClient.streamingHandshake(instance, sessionId, handler);
                break;
            case CONNECT:
                LOGGER.info("Beginning connect");
                httpClient.streamingConnect(instance, sessionId, cookies, clientID, handler);
                break;
            case SUBSCRIBE:
                LOGGER.info("Beginning subscribe");
                httpClient.streamingSubscribe(instance, sessionId, cookies, clientID, subscriptionChannel, handler);
                break;
            case DISCONNECT:
                break;
            case UNSUBSCRIBE:
                break;
        }
    }

    public void stop() {
        run = false;
    }
}
