/**
 * @author psrinivasan
 *         Date: 8/25/12
 *         Time: 9:35 PM
 */

package com.sfdc.http.client;

import com.ning.http.client.*;
import com.sfdc.http.client.handler.GenericAsyncHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AsyncHttpClient extends com.ning.http.client.AsyncHttpClient {

    private static final boolean BLOCKING = false;
    private static final boolean THREADED = false;
    private static final String PUSH_ENDPOINT = "/cometd/25.0";
    private static final String API_VERSION = "25.0";
    private static final String REST_URI_PREFIX = "/services/data/v";
    private static final String QUERY = "/query/";
    private static final String REST_URI = REST_URI_PREFIX + API_VERSION;
    private static final String REST_QUERY_URI = REST_URI + QUERY;
    private static final String DEFAULT_PUSH_ENDPOINT = "/cometd/25.0";
    private static final String HANDSHAKE_MESSAGE =
            "[{\"version\":\"1.0\",\"minimumVersion\":\"0.9\",\"channel\":\"/meta/handshake\",\"supportedConnectionTypes\": [\"long-polling\"]}]";
    private static final String CONNECT_PREFIX_MESSAGE = "[{\"channel\":\"/meta/connect\",\"clientId\":\"";
    private static final String CONNECT_POST_MESSAGE = "\",\"connectionType\":\"long-polling\"}]";
    private static final String SUBSCRIBE_PREFIX_MESSAGE = "[{\"channel\":\"/meta/subscribe\",\"subscription\":\"";
    private static final String SUBSCRIBE_IN_1_MESSAGE = "\",\"clientId\":\"";
    private static final String SUBSCRIBE_POST_MESSAGE = "\"}]";
    private static final String DISCONNECT_PRE_MESSAGE = "[{\"channel\":\"/meta/disconnect\",\"clientId\":\"";
    private static final String DISCONNECT_POST_MESSAGE = "\"}]";

    /* These URI suffixes are there only to make debugging server logs easier */
    private static final String HANDSHAKE = "/handshake";
    private static final String CONNECT = "/connect";
    private static final String SUBSCRIBE = "/connect";
    private static final String DISCONNECT = "/disconnect";

    public AsyncHttpClient() {
        super(new AsyncHttpClientConfig.Builder()
                .setIOThreadMultiplier(5).build());
    }

    public BoundRequestBuilder prepareQuery(String instance, String soql, String sessionId) {
        StringBuilder builder = new StringBuilder();
        builder.append(instance).append(REST_QUERY_URI);
        return prepareGet(builder.toString())
                .addHeader("Authorization", "Bearer " + sessionId)
                .addQueryParameter("q", soql);
    }

    public ListenableFuture<Response> soql(String instance, String soql, String sessionId) {
        ListenableFuture<Response> future = null;
        try {
            future = prepareQuery(instance, soql, sessionId).execute(new GenericAsyncHandler());
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

    public ListenableFuture<Response> streamingHandshake(String instance, String sessionId) {
        return streamingHandshake(instance, sessionId, new GenericAsyncHandler());
    }

    public ListenableFuture<Response> streamingHandshake(String instance, String sessionId, AsyncHandler asyncHandler) {
        ListenableFuture<Response> future = null;

        try {
            future = preparePost(instance + DEFAULT_PUSH_ENDPOINT + HANDSHAKE)
                    .addHeader("Authorization", "Bearer " + sessionId)
                    .addHeader("Content-Type", "application/json")
                    .setBody(HANDSHAKE_MESSAGE)
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

    public ListenableFuture<Response> streamingConnect(String instance, String sessionId, List<Cookie> cookies, String clientId) {
        ListenableFuture<Response> future = null;
        BoundRequestBuilder requestBuilder = preparePost(instance + DEFAULT_PUSH_ENDPOINT + CONNECT)
                .addHeader("Authorization", "Bearer " + sessionId)
                .addHeader("Content-Type", "application/json")
                .setBody(CONNECT_PREFIX_MESSAGE + clientId + CONNECT_POST_MESSAGE);
        for (Cookie cookie : cookies) {
            requestBuilder.addCookie(cookie);
        }
        try {
            future = requestBuilder.execute(new GenericAsyncHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return future;
    }

    public ListenableFuture<Response> streamingSubscribe(String instance, String sessionId, List<Cookie> cookies, String clientId, String channel) {
        return streamingSubscribe(instance, sessionId, cookies, clientId, channel, new GenericAsyncHandler());
    }

    public ListenableFuture<Response> streamingSubscribe(String instance, String sessionId, List<Cookie> cookies, String clientId, String channel, AsyncHandler asyncHandler) {
        ListenableFuture<Response> future = null;
        BoundRequestBuilder requestBuilder = preparePost(instance + DEFAULT_PUSH_ENDPOINT + SUBSCRIBE)
                .addHeader("Authorization", "Bearer " + sessionId)
                .addHeader("Content-Type", "application/json")
                .setBody(SUBSCRIBE_PREFIX_MESSAGE + channel + SUBSCRIBE_IN_1_MESSAGE + clientId + SUBSCRIBE_POST_MESSAGE);
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

    public ListenableFuture<Response> streamingDisconnect(String instance, String sessionId, List<Cookie> cookies, String clientId) {
        return streamingDisconnect(instance, sessionId, cookies, clientId, new GenericAsyncHandler());
    }

    public ListenableFuture<Response> streamingDisconnect(String instance, String sessionId, List<Cookie> cookies, String clientId, AsyncHandler asyncHandler) {
        ListenableFuture<Response> future = null;
        BoundRequestBuilder requestBuilder = preparePost(instance + DEFAULT_PUSH_ENDPOINT + DISCONNECT)
                .addHeader("Authorization", "Bearer " + sessionId)
                .addHeader("Content-Type", "application/json")
                .setBody(DISCONNECT_PRE_MESSAGE + clientId + DISCONNECT_POST_MESSAGE);
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
}
