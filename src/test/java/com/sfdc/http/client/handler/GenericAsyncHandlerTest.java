package com.sfdc.http.client.handler;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;
import com.sfdc.http.client.NingAsyncHttpClientImpl;
import com.sfdc.http.client.StreamingResponse;
import com.sfdc.http.util.SoapLoginUtil;
import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 9/11/12
 *         Time: 10:17 PM
 */
public class GenericAsyncHandlerTest extends TestCase {
    private GenericAsyncHandler handler;
    private NingAsyncHttpClientImpl httpClient;
    private String sessionId;
    private String instance;

    public void setUp() throws Exception {
        handler = new GenericAsyncHandler();
        httpClient = new NingAsyncHttpClientImpl();
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        sessionId = credentials[0];
        instance = credentials[1];


    }

    public void tearDown() throws Exception {

    }

    public void testOnCompleted_handshake() throws Exception {
        Future<Response> future = httpClient.streamingHandshake(instance, sessionId, handler);
        Response response = future.get();
        List<Cookie> cookies = response.getCookies();
        Cookie streamingCookie = null;
        Cookie bayeuxBrowserCookie = null;
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
        StreamingResponse sr = new StreamingResponse(response);
        assertTrue(sr.getBayeuxSuccessResponseField());
        assertEquals("/meta/handshake", sr.getChannels().get(0));
    }

    public void testOnCompleted_subscribe() throws Exception {

    }

    public void testGetOperationType() throws Exception {

    }
}
