package com.sfdc.http.queue;

import com.sfdc.http.client.NingAsyncHttpClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 9:20 PM
 *         <p/>
 *         An instance of the consumer will spawn one instance of the HttpClient.
 *         This is good insurance against any scalability bottlenecks in the HttpClient itself.
 *         We expect to have only a few instances of Consumer though.
 */
public class Consumer implements Runnable {
    private final BlockingQueue<WorkItem> queue;
    private Semaphore concurrencyPermit;
    NingAsyncHttpClientImpl httpClient = new NingAsyncHttpClientImpl(concurrencyPermit);
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    public Consumer(BlockingQueue queue, Semaphore concurrencyPermit) {
        this.concurrencyPermit = concurrencyPermit;
        this.queue = queue;
        LOGGER.info("Started Request Consumer.  Concurrency: %s", concurrencyPermit.availablePermits());
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
        while (true) {
            try {
                /*
                 * Wait to make sure that one gets the concurrency permit since we don't want a higher load concurrency
                 * than specified.
                 */
                concurrencyPermit.acquire();
                /*
                 * If we get a concurrency permit, then wait for work.  There is a chance, when there is no work.
                 * that we will lock up a concurrency permit and not do any work, but that's not a big issue since it
                 * will lock up only one permit per consumer (we dont expect many consumer instances).
                 * We do want to make this better if we can.
                 */
                WorkItem work = queue.take();
                processWorkItem(work);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void processWorkItem(WorkItem work) {
        String instance = work.getInstance();
        String sessionId = work.getSessionId();
        String clientID = work.getClientId();
        WorkItem.Operation operation = work.getOperation();
        switch (operation) {
            case HANDSHAKE:
                httpClient.streamingHandshake(instance, sessionId);
                break;
            case CONNECT:
                break;
            case SUBSCRIBE:
                break;
            case DISCONNECT:
                break;
            case UNSUBSCRIBE:
                break;
        }
    }
}
