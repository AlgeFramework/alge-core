package com.sfdc.http.client;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;
import junit.framework.TestCase;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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
    private final String userName = "admin@ist8.streaming.20.systest.org501";
    private final String password = "123456";
    private final String loginInstanceUrl = "https://ist8.soma.salesforce.com/";

    public void setUp() throws Exception {
        //String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        //String[] credentials = SoapLoginUtil.login("dpham@178.private.streaming.20.org2006", "123456", "https://ist6.soma.salesforce.com/");
        //String[] credentials = SoapLoginUtil.login("admin@ist8.streaming.20.systest.org10496", "123456", "https://ist8.soma.salesforce.com/");


        sessionId = "00D30000001Il2K!ARcAQFc_aQmTZWazRtnG7sZp7QkraM55Ei7N6sRt3v95bK2tugFm2o71I5GDqgT6HQWyDsPKuPrWiryUdlOj9DtycwaSJ2IA";//credentials[0];
        //sessionId = "00D30000001IZ8g!AREAQLoEY0wC60qdHyw07EarfsG9s.MnbBVAq0.49FSQNoy0oyj5SaC7sqtvUEhpNvlMojZgOUKTy3hWbLIoqoVzwaUJ8Xns";
        instance = "https://ist8.soma.salesforce.com/";//credentials[1];
        asyncHttpClient_base = new NingAsyncHttpClientImpl();
        //asyncHttpClient_concurrencyControl_base = new NingAsyncHttpClientImpl(new Semaphore(1));

    }

    public void tearDown() throws Exception {

    }


    public void testGet() throws ExecutionException, InterruptedException {
        Future<Response> future = asyncHttpClient_base.startGet("http://www.gnu.org/", null, null, null, null);
        Response response = future.get();
        System.out.println(response.getStatusCode());
        System.out.println(response.getStatusText());
    }


    public void testLogin_no_concurrency_control() throws Exception {
        login_base_test(asyncHttpClient_base);
    }

//    public void testLogin_concurrency_control() throws Exception {
//        login_base_test(asyncHttpClient_concurrencyControl_base);
//    }

    public void login_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws ExecutionException, InterruptedException, IOException, SAXException {
        Future<Response> future = asyncHttpClient.login(loginInstanceUrl, userName, password);
        Response response = future.get();
        String[] credentials = asyncHttpClient.getLoginCredentials(response.getResponseBody());
        assertEquals("https://ist8.soma.salesforce.com", credentials[1]);
        assertNotNull(credentials[0]);
        System.out.println("session id " + credentials[0] + " instance " + credentials[1]);
        //System.out.println(response.getResponseBody());

    }

    public void test_http_get(NingAsyncHttpClientImpl asyncHttpClient) {
        Future<Response> future = asyncHttpClient.startGet("http://adhoc-app1-17-sfm.ops.sfdc.net:8080/", null, null, null, null);
        Response response = null;
        try {
            response = future.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getStatusText());
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

//    public void testSOQL_concurrency_control() throws Exception {
//        soql_base_test(asyncHttpClient_concurrencyControl_base);
//    }

    public void streamingHandshake_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        long time1 = System.currentTimeMillis();
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        System.out.println("Time taken = " + (System.currentTimeMillis() - time1));
        String responseBody = response.getResponseBody();
        System.out.println(responseBody);
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
        StreamingResponse sr = new StreamingResponse(response);
        assertTrue(sr.getBayeuxSuccessResponseField());
        assertEquals("/meta/handshake", sr.getChannels().get(0));
    }

    public void testStreamingHandshake_no_concurrency_control() throws Exception {
        streamingHandshake_base_test(asyncHttpClient_base);
    }

//    public void testStreamingHandshake_concurrency_control() throws Exception {
//        streamingHandshake_base_test(asyncHttpClient_concurrencyControl_base);
//    }

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
        StreamingResponse streamingResponse = new StreamingResponse(response);
        String clientID = streamingResponse.getClientId();
        System.out.println("clientId: " + clientID);

        //subscribe
        Future<Response> subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/accountTopic");
        Response subscribeResponse = subscribeFuture.get();
        StreamingResponse srSubscribe = new StreamingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/subscribe", srSubscribe.getChannels().get(0));
        assertEquals("/topic/accountTopic", srSubscribe.getSubscription());

        //now connect ...
        Future<Response> connectFuture = asyncHttpClient.streamingConnect(instance, sessionId, cookies, clientID);
        Response connectResponse = connectFuture.get();
        System.out.println("response body: " + connectResponse.getResponseBody());
        StreamingResponse sr = new StreamingResponse(connectResponse);
        assertTrue(sr.getBayeuxSuccessResponseField());
        System.out.println("success? " + sr.getBayeuxSuccessResponseField());
    }

    public void testConnect_no_concurrency_control() throws Exception {
        connect_base_test(asyncHttpClient_base);
    }

//    public void testConnect_concurrency_control() throws Exception {
//        connect_base_test(asyncHttpClient_concurrencyControl_base);
//    }

    public void subscribe_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        /* HANDSHAKE */
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        StreamingResponse streamingResponse = new StreamingResponse(response);
        String clientID = streamingResponse.getClientId();
        /* SUBSCRIBE */
        //Future<Response> subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/accountTopic");
        //Response subscribeResponse = subscribeFuture.get();
        Future<Response> subscribeFuture = asyncHttpClient.streamingSubscribe(instance, sessionId, cookies, clientID, "/topic/PollNotice0");
        Response subscribeResponse = subscribeFuture.get();
        StreamingResponse srSubscribe = new StreamingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/subscribe", srSubscribe.getChannels().get(0));
        //assertEquals("/topic/accountTopic", srSubscribe.getSubscription());

    }

    public void testSubscribe_no_concurrency_control() throws Exception {
        subscribe_base_test(asyncHttpClient_base);
    }

//    public void testSubscribe_concurrency_control() throws Exception {
//        subscribe_base_test(asyncHttpClient_concurrencyControl_base);
//    }

    public void disconnect_base_test(NingAsyncHttpClientImpl asyncHttpClient) throws Exception, InterruptedException, IOException {
        /* HANDSHAKE */
        Future<Response> future = asyncHttpClient.streamingHandshake(instance, sessionId);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        StreamingResponse streamingResponse = new StreamingResponse(response);
        String clientID = streamingResponse.getClientId();
        /* DISCONNECT */
        Future<Response> subscribeFuture = asyncHttpClient.streamingDisconnect(instance, sessionId, cookies, clientID);
        Response subscribeResponse = subscribeFuture.get();
        StreamingResponse srSubscribe = new StreamingResponse(subscribeResponse);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/disconnect", srSubscribe.getChannels().get(0));
    }

    public void testDisconnect_no_concurrency_control() throws Exception {
        disconnect_base_test(asyncHttpClient_base);
    }

//    public void testDisconnectconcurrency_control() throws Exception {
//        disconnect_base_test(asyncHttpClient_concurrencyControl_base);
//    }

}
