package com.sfdc.http.client;

import com.ning.http.client.Cookie;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.sfdc.http.util.SoapLoginUtil;
import junit.framework.TestCase;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 8/25/12
 *         Time: 10:56 PM
 */
public class AsyncHttpClientTest extends TestCase {
    private String sessionId;
    private String instance;
    private AsyncHttpClient asyncHttpClient;

    public void setUp() throws Exception {
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        sessionId = credentials[0];
        instance = credentials[1];
        asyncHttpClient = new AsyncHttpClient();
    }

    public void tearDown() throws Exception {

    }

    public void testSoql() throws Exception {
        String soql = "select Id from Account limit 1";
        ListenableFuture<Response> future = asyncHttpClient.soql(instance, soql, sessionId);
        Response response = future.get();
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getStatusText());
    }

    public void testStreamingHandshake() throws ExecutionException, InterruptedException, IOException {
        ListenableFuture<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        String responseBody = response.getResponseBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        System.out.println("size of array = " + rootNode.size());
        System.out.println("clientId: " + rootNode.get(0).path("clientId").asText());
        System.out.println("successful: " + rootNode.get(0).path("successful").asText());
        List<Cookie> cookies = response.getCookies();
        System.out.println("number of cookies = " + cookies.size());
        for (int i = 0; i < cookies.size(); i++) {
            System.out.println(cookies.get(i).getName());
        }
        assertEquals(1, rootNode.size());
        assertEquals("true", rootNode.get(0).path("successful").asText());
        assertEquals("/meta/handshake", rootNode.get(0).path("channel").asText());

    }

    public void testConnect() throws ExecutionException, InterruptedException, IOException {
        ListenableFuture<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
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
        String clientID = rootNode.get(0).path("clientId").asText();
        System.out.println("clientId: " + clientID);
        ListenableFuture<Response> connectFuture = asyncHttpClient.streamingConnect(instance, sessionId, cookies, clientID);
        Response connectResponse = connectFuture.get();
        System.out.println("response body: " + connectResponse.getResponseBody());


    }
}
