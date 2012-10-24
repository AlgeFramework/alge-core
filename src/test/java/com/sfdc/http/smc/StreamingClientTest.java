package com.sfdc.http.smc;

import com.sfdc.http.util.SoapLoginUtil;
import junit.framework.TestCase;

/**
 * @author psrinivasan
 *         Date: 9/11/12
 *         Time: 1:37 PM
 */
public class StreamingClientTest extends TestCase {
    private StreamingClientImpl streamingClient;
    private String sessionId;
    private String instance;

    public void setUp() throws Exception {
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        sessionId = credentials[0];
        instance = credentials[1];
        streamingClient = new StreamingClientImpl(sessionId, instance);
    }

    public void tearDown() throws Exception {

    }

    /**
     * TODO: THIS TEST IS BROKEN since one can't jump into a specific state in a state machine :(
     *
     * @throws Exception
     * @throws InterruptedException
     */
/*    public void testStartSubscribe() throws Exception, InterruptedException {
        streamingClient.startHandshake();
        Future<Response> responseFuture = streamingClient.getFuture();
        responseFuture.get();
        streamingClient.startSubscribe();
        Future<Response> r = streamingClient.getFuture();
        Response response = r.get();
        StreamingResponse srSubscribe = new StreamingResponse(response);
        assertTrue(srSubscribe.getBayeuxSuccessResponseField());
        assertEquals("/meta/subscribe", srSubscribe.getChannel());
        assertEquals("/topic/accountTopic", srSubscribe.getSubscription());
    }*/
    public void testStart() throws Exception {
        streamingClient.start();
        Thread.sleep(115000); // 110 seconds for connect to expire, and 5 seconds for everything else.
        if (streamingClient.getState() != "FSM.Done") {
            System.out.println("End state not reached yet, sleeping for 10 more seconds.");
            //sleep for a 10 more seconds hoping we'll be done.
            Thread.sleep(10000);
        }
        assertEquals("FSM.Done", streamingClient.getState());
    }
}
