package com.sfdc.http.client;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;
import com.sfdc.http.util.SoapLoginUtil;
import junit.framework.TestCase;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;


/**
 * @author psrinivasan
 *         Date: 8/25/12
 *         Time: 10:56 PM
 */
public class NingAsyncHttpClientImplTest extends TestCase {
    private String sessionId;
    private String instance;
    private NingAsyncHttpClientImpl asyncHttpClient_base;
    private NingAsyncHttpClientImpl asyncHttpClient_concurrencyControl_base;

    public void setUp() throws Exception {
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        //String[] credentials = SoapLoginUtil.login("dpham@178.private.streaming.20.org2006", "123456", "https://ist6.soma.salesforce.com/");

        sessionId = credentials[0];
        instance = credentials[1];
        asyncHttpClient_base = new NingAsyncHttpClientImpl();
        asyncHttpClient_concurrencyControl_base = new NingAsyncHttpClientImpl(new Semaphore(1));

    }

    public void tearDown() throws Exception {

    }

    public void soql_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception {
        String soql = "select Id from Account limit 1";
        Future<Response> future = asyncHttpClient.soql(instance, soql, sessionId);
        Response response = future.get();
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getStatusText());
    }

    public void testSOQL_no_concurrency_control() throws Exception {
        soql_base_test(asyncHttpClient_base);
    }

    public void testSOQL_concurrency_control() throws Exception {
        soql_base_test(asyncHttpClient_concurrencyControl_base);
    }

    public void streamingHandshake_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        long time1 = System.currentTimeMillis();
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        System.out.println("Time taken = " + (System.currentTimeMillis() - time1));
        String responseBody = response.getResponseBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        //System.out.println("size of array = " + rootNode.size());
        System.out.println("clientId: " + rootNode.get(0).path("clientId").asText());
        List<Cookie> cookies = response.getCookies();
        Cookie streamingCookie = new Cookie(null, "NULL_TEST_COOKIE", "NULL_TEST_COOKIE", "/", 0, false);
        Cookie bayeuxBrowserCookie = new Cookie(null, "NULL_TEST_COOKIE", "NULL_TEST_COOKIE", "/", 0, false);
        //Cookie streamingCookie = null;
        //Cookie bayeuxBrowserCookie = null;
        for (int i = 0; i < cookies.size(); i++) {
            //System.out.println(cookies.get(i).getName());
            if (cookies.get(i).getName().equals("sfdc-stream")) {
                streamingCookie = cookies.get(i);
            }
            if (cookies.get(i).getName().equals("BAYEUX_BROWSER")) {
                bayeuxBrowserCookie = cookies.get(i);
            }
        }
        assertEquals("sfdc-stream", streamingCookie.getName());
        assertEquals("BAYEUX_BROWSER", bayeuxBrowserCookie.getName());
        assertEquals(1, rootNode.size());
        NingResponse sr = new NingResponse(response);
        assertTrue(sr.getBayeuxSuccessResponseField());
        assertEquals("/meta/handshake", sr.getChannel());
    }

    public void testStreamingHandshake_no_concurrency_control() throws Exception {
        streamingHandshake_base_test(asyncHttpClient_base);
    }

    public void testStreamingHandshake_concurrency_control() throws Exception {
        streamingHandshake_base_test(asyncHttpClient_concurrencyControl_base);
    }

    public void connect_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        //handshake
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        Cookie streamingCookie = null;
        for (int i = 0; i < cookies.size(); i++) {
            if (cookies.get(i).getName().equalsIgnoreCase("sfdc-stream")) {
                streamingCookie = cookies.get(i);
            }
        }
        assertEquals("sfdc-stream", streamingCookie.getName());
        System.out.println("value of sfdc-stream cookie: " + streamingCookie.getValue());
        String responseBody = response.getResponseBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        System.out.println("size of array = " + rootNode.size());
        //String clientID = rootNode.get(0).path("clientId").asText();
        NingResponse ningResponse = new NingResponse(response);
        String clientID = ningResponse.getClientId();
        System.out.println("clientId: " + clientID);

        //subscribe
        Future<Response> subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/accountTopic");
        Response subscribeResponse = subscribeFuture.get();
        NingResponse srSubscribe = new NingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/subscribe", srSubscribe.getChannel());
        assertEquals("/topic/accountTopic", srSubscribe.getSubscription());

        //now connect ...
        Future<Response> connectFuture = asyncHttpClient.streamingConnect(instance, sessionId, cookies, clientID);
        Response connectResponse = connectFuture.get();
        System.out.println("response body: " + connectResponse.getResponseBody());
        NingResponse sr = new NingResponse(connectResponse);
        assertTrue(sr.getBayeuxSuccessResponseField());
        System.out.println("success? " + sr.getBayeuxSuccessResponseField());
    }

    public void testConnect_no_concurrency_control() throws Exception {
        connect_base_test(asyncHttpClient_base);
    }

    public void testConnect_concurrency_control() throws Exception {
        connect_base_test(asyncHttpClient_concurrencyControl_base);
    }

    public void subscribe_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        /* HANDSHAKE */
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        NingResponse ningResponse = new NingResponse(response);
        String clientID = ningResponse.getClientId();
        /* SUBSCRIBE */
        Future<Response> subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/accountTopic");
        Response subscribeResponse = subscribeFuture.get();
        subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/accountTopic");
        subscribeResponse = subscribeFuture.get();
        NingResponse srSubscribe = new NingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/subscribe", srSubscribe.getChannel());
        assertEquals("/topic/accountTopic", srSubscribe.getSubscription());

    }

    public void testSubscribe_no_concurrency_control() throws Exception {
        subscribe_base_test(asyncHttpClient_base);
    }

    public void testSubscribe_concurrency_control() throws Exception {
        subscribe_base_test(asyncHttpClient_concurrencyControl_base);
    }

    public void disconnect_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        /* HANDSHAKE */
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        NingResponse ningResponse = new NingResponse(response);
        String clientID = ningResponse.getClientId();
        /* DISCONNECT */
        Future<Response> subscribeFuture = asyncHttpClient.streamingDisconnect(instance, sessionId, cookies, clientID);
        Response subscribeResponse = subscribeFuture.get();
        NingResponse srSubscribe = new NingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/disconnect", srSubscribe.getChannel());
    }

    public void testDisconnect_no_concurrency_control() throws Exception {
        disconnect_base_test(asyncHttpClient_base);
    }

    public void testDisconnectconcurrency_control() throws Exception {
        disconnect_base_test(asyncHttpClient_concurrencyControl_base);
    }

}
