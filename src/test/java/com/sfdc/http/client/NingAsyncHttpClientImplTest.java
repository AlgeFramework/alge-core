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
    private NingAsyncHttpClientImpl asyncHttpClient;

    public void setUp() throws Exception {
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        //String[] credentials = SoapLoginUtil.login("dpham@178.private.streaming.20.org2006", "123456", "https://ist6.soma.salesforce.com/");

        sessionId = credentials[0];
        instance = credentials[1];
        asyncHttpClient = new NingAsyncHttpClientImpl();
    }

    public void tearDown() throws Exception {

    }

    public void testSoql() throws Exception {
        String soql = "select Id from Account limit 1";
        Future<Response> future = asyncHttpClient.soql(instance, soql, sessionId);
        Response response = future.get();
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getStatusText());
    }

    public void testStreamingHandshake() throws Exception, InterruptedException, IOException {
        long time1 = System.currentTimeMillis();
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        System.out.println("Time taken = " + (System.currentTimeMillis() - time1));
        String responseBody = response.getResponseBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        //System.out.println("size of array = " + rootNode.size());
        System.out.println("clientId: " + rootNode.get(0).path("clientId").asText());
        //System.out.println("successful: " + rootNode.get(0).path("successful").asText());
        List<Cookie> cookies = response.getCookies();
        //System.out.println("number of cookies = " + cookies.size());
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

    public void testConnect() throws Exception, InterruptedException, IOException {
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
        Future<Response> connectFuture = asyncHttpClient.streamingConnect(instance, sessionId, cookies, clientID);
        Response connectResponse = connectFuture.get();
        System.out.println("response body: " + connectResponse.getResponseBody());
        NingResponse sr = new NingResponse(connectResponse);
        assertTrue(sr.getBayeuxSuccessResponseField());
        System.out.println("success? " + sr.getBayeuxSuccessResponseField());
    }

    public void testSubscribe() throws Exception, InterruptedException, IOException {
        /* HANDSHAKE */
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        NingResponse ningResponse = new NingResponse(response);
        String clientID = ningResponse.getClientId();
        /* SUBSCRIBE */
        Future<Response> subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/accountTopic");
        Response subscribeResponse = subscribeFuture.get();
        NingResponse srSubscribe = new NingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/subscribe", srSubscribe.getChannel());
        assertEquals("/topic/accountTopic", srSubscribe.getSubscription());

    }

    public void testDisconnect() throws Exception, InterruptedException, IOException {
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

}
