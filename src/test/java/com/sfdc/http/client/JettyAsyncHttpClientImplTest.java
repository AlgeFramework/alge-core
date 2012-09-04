package com.sfdc.http.client;

import com.sfdc.http.client.handler.GenericContentExchange;
import com.sfdc.http.util.SoapLoginUtil;
import junit.framework.TestCase;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpExchange;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 12:07 PM
 */
public class JettyAsyncHttpClientImplTest extends TestCase {
    private String sessionId;
    private String instance;
    private JettyAsyncHttpClientImpl asyncHttpClient;

    public void setUp() throws Exception {
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        //String[] credentials = SoapLoginUtil.login("dpham@178.private.streaming.20.org2006", "123456", "https://ist6.soma.salesforce.com/");

        sessionId = credentials[0];
        instance = credentials[1];

        System.out.println("Session Id: " + sessionId + " instance " + instance);
        asyncHttpClient = new JettyAsyncHttpClientImpl();
    }

    public void tearDown() throws Exception {

    }

    public void testStreamingHandshake() throws Exception {
        long time1 = System.currentTimeMillis();
        GenericContentExchange exchange = asyncHttpClient.streamingHandshake(instance, sessionId);
        int exchangeState = exchange.waitForDone();
        System.out.println("time taken for handshake = " + (System.currentTimeMillis() - time1) + "ms");
        assertEquals(HttpExchange.STATUS_COMPLETED, exchangeState);
        System.out.println("handshake success?: " + exchange.getBayeuxSuccessResponseField() + "; clientId: " + exchange.getClientId());


    }
}
