package com.sfdc.http.client;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 8/30/12
 *         Time: 10:31 PM
 */
public interface AsyncHttpClient {
    public Future soql(String instance, String soql, String sessionId);

    public Future streamingHandshake(String instance, String sessionId);

    public Future streamingConnect(String instance, String sessionId, List<Cookie> cookies, String clientId);

    public Future streamingSubscribe(String instance, String sessionId, List<Cookie> cookies, String clientId, String channel);

    public Future<Response> streamingDisconnect(String instance, String sessionId, List<Cookie> cookies, String clientId);
}
