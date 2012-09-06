/**
 * @author psrinivasan
 *         Date: 8/25/12
 *         Time: 9:35 PM
 */

package com.sfdc.http.client;

import com.ning.http.client.*;
import com.ning.http.client.Cookie;
import com.sfdc.http.client.filter.ThrottlingRequestFilter;
import com.sfdc.http.client.filter.ThrottlingResponseFilter;
import com.sfdc.http.client.handler.GenericAsyncHandler;
import com.sfdc.http.client.handler.ThrottlingGenericAsyncHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class NingAsyncHttpClientImpl extends com.ning.http.client.AsyncHttpClient {

    private static final boolean BLOCKING = false;
    private static final boolean THREADED = false;
    private static final String PUSH_ENDPOINT = "/cometd/25.0";
    private static final int IO_THREAD_MULTIPLIER = 10;
    private static final int MAX_CONNECTIONS_TOTAL = 100000;
    private static final int MAX_CONNECTIONS_PER_HOST = 100000;
    private Semaphore semaphore;

    public NingAsyncHttpClientImpl(Semaphore concurrencyPermit) {
        super(new AsyncHttpClientConfig.Builder()
                .setIOThreadMultiplier(IO_THREAD_MULTIPLIER)
                .setMaximumConnectionsTotal(MAX_CONNECTIONS_TOTAL)
                .setMaximumConnectionsPerHost(MAX_CONNECTIONS_PER_HOST)
                .addRequestFilter(new ThrottlingRequestFilter())
                .addResponseFilter(new ThrottlingResponseFilter(concurrencyPermit))
                .build());
    }

    public NingAsyncHttpClientImpl() {
        super(new AsyncHttpClientConfig.Builder()
                .setIOThreadMultiplier(IO_THREAD_MULTIPLIER)
                .setMaximumConnectionsTotal(MAX_CONNECTIONS_TOTAL)
                .setMaximumConnectionsPerHost(MAX_CONNECTIONS_PER_HOST)
                .addRequestFilter(new ThrottlingRequestFilter())
                .build());
        semaphore = null;
    }

    public BoundRequestBuilder prepareQuery(String instance, String soql, String sessionId) {
        StringBuilder builder = new StringBuilder();
        builder.append(instance).append(SfdcConstants.REST_QUERY_URI);
        return prepareGet(builder.toString())
                .addHeader("Authorization", "Bearer " + sessionId)
                .addQueryParameter("q", soql);
    }

    public Future<Response> soql(String instance, String soql, String sessionId) {
        ListenableFuture<Response> future = null;
        try {
            future = prepareQuery(instance, soql, sessionId).execute(returnAppropriateHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (BLOCKING) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return future;
    }

    public Future<Response> streamingHandshake(String instance, String sessionId) {
        return streamingHandshake(instance, sessionId, returnAppropriateHandler());
    }

    public Future<Response> streamingHandshake(String instance, String sessionId, AsyncHandler asyncHandler) {
        ListenableFuture<Response> future = null;

        try {
            future = preparePost(instance + SfdcConstants.DEFAULT_PUSH_ENDPOINT + SfdcConstants.HANDSHAKE)
                    .addHeader("Authorization", "Bearer " + sessionId)
                    .addHeader("Content-Type", "application/json")
                    .setBody(SfdcConstants.HANDSHAKE_MESSAGE)
                    .execute(asyncHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (BLOCKING) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return future;
    }

    public Future<Response> streamingConnect(String instance, String sessionId, List<Cookie> cookies, String clientId) {
        Future<Response> future = null;
        BoundRequestBuilder requestBuilder = preparePost(instance + SfdcConstants.DEFAULT_PUSH_ENDPOINT + SfdcConstants.CONNECT)
                .addHeader("Authorization", "Bearer " + sessionId)
                .addHeader("Content-Type", "application/json")
                .setBody(SfdcConstants.CONNECT_PREFIX_MESSAGE + clientId + SfdcConstants.CONNECT_POST_MESSAGE);
        for (Cookie cookie : cookies) {
            requestBuilder.addCookie(cookie);
        }
        try {
            future = requestBuilder.execute(returnAppropriateHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return future;
    }

    public Future<Response> streamingSubscribe(String instance, String sessionId, List<Cookie> cookies, String clientId, String channel) {
        return streamingSubscribe(instance, sessionId, cookies, clientId, channel, returnAppropriateHandler());
    }

    public Future<Response> streamingSubscribe(String instance, String sessionId, List<Cookie> cookies, String clientId, String channel, AsyncHandler asyncHandler) {
        ListenableFuture<Response> future = null;
        BoundRequestBuilder requestBuilder = preparePost(instance + SfdcConstants.DEFAULT_PUSH_ENDPOINT + SfdcConstants.SUBSCRIBE)
                .addHeader("Authorization", "Bearer " + sessionId)
                .addHeader("Content-Type", "application/json")
                .setBody(SfdcConstants.SUBSCRIBE_PREFIX_MESSAGE + channel + SfdcConstants.SUBSCRIBE_IN_1_MESSAGE + clientId + SfdcConstants.SUBSCRIBE_POST_MESSAGE);
        for (Cookie cookie : cookies) {
            requestBuilder.addCookie(cookie);
        }
        try {
            future = requestBuilder.execute(asyncHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return future;
    }

    public Future<Response> streamingDisconnect(String instance, String sessionId, List<Cookie> cookies, String clientId) {
        return streamingDisconnect(instance, sessionId, cookies, clientId, returnAppropriateHandler());
    }

    public Future<Response> streamingDisconnect(String instance, String sessionId, List<Cookie> cookies, String clientId, AsyncHandler asyncHandler) {
        ListenableFuture<Response> future = null;
        BoundRequestBuilder requestBuilder = preparePost(instance + SfdcConstants.DEFAULT_PUSH_ENDPOINT + SfdcConstants.DISCONNECT)
                .addHeader("Authorization", "Bearer " + sessionId)
                .addHeader("Content-Type", "application/json")
                .setBody(SfdcConstants.DISCONNECT_PRE_MESSAGE + clientId + SfdcConstants.DISCONNECT_POST_MESSAGE);
        for (Cookie cookie : cookies) {
            requestBuilder.addCookie(cookie);
        }
        try {
            future = requestBuilder.execute(asyncHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return future;
    }

    private AsyncHandler returnAppropriateHandler() {
        return (semaphore == null) ? new GenericAsyncHandler() : new ThrottlingGenericAsyncHandler(semaphore);
    }
}
