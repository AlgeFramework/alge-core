package com.sfdc.http.client.handler;

import com.ning.http.client.Response;
import com.sfdc.http.HttpClient;
import com.sfdc.http.queue.ProducerConsumerQueue;
import com.sfdc.stats.StatsManager;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 9:52 PM
 */
public class ThrottlingGenericAsyncHandler extends GenericAsyncHandler {
    private final Semaphore concurrencyPermit;
    private final StatsManager statsManager;
    private final ResponseHandler responseHandler;
    private final HttpClient httpClient;

    public ThrottlingGenericAsyncHandler(Semaphore concurrencyPermit, StatsManager statsManager, ResponseHandler responseHandler, HttpClient httpClient) {
        super();
        this.concurrencyPermit = concurrencyPermit;
        this.statsManager = statsManager;
        this.responseHandler = responseHandler;
        this.httpClient = httpClient;
    }

    @Override
    public Object onCompleted() throws Exception {
        Object obj = super.onCompleted();
        concurrencyPermit.release();
        if (statsManager != null) {
            statsManager.decrementCustomStats(ProducerConsumerQueue.CONCURRENCY_STATS_METRIC);
        }
        Response r = (Response) obj;
        System.out.println("GOT RESPONSE!" + r.getResponseBody());
        responseHandler.onCompleted(r.getStatusCode(), r.getStatusText(), r.getResponseBody(), r.getContentType(), httpClient);
        return obj;
    }
}
