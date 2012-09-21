package com.sfdc.http.client.handler;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.Response;
import com.sfdc.http.client.NingResponse;
import com.sfdc.http.smc.StreamingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author psrinivasan
 *         Date: 9/10/12
 *         Time: 9:42 PM
 *         Reason this class exists is to store the client's state object, and call it on
 *         completion of a streaming operation.  ie., to make each client responsible for it's
 *         own life cycle since we're in non blocking heaven :)
 *         <p/>
 *         This class, just like it's parent, needs to be very light.
 */
public class StatefulHandler extends GenericAsyncHandler implements AsyncHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatefulHandler.class);
    private StreamingClient streamingClient;

    public StatefulHandler(StreamingClient sc) {
        super();
        this.streamingClient = sc;
    }

    @Override
    public void startRequestTimer() {
        System.out.println("STREAMING STATE BEFORE STARTING REQUEST TIMER: " + streamingClient.getState());
        super.startRequestTimer();
    }

    @Override
    public Object onCompleted() throws Exception {
        Object retVal = super.onCompleted();
        NingResponse response = new NingResponse((Response) retVal);
        if (!isResponseSucessful(response)) {
            LOGGER.warn("Request failed!");
            //TODO:  have more meaningful log line that includes info about which request failed.
            return retVal;
        }
        String s = getOperationType(response);
        System.out.println("RESPONSE OPERATION = " + s);
        if (s.equals("/meta/handshake")) {
            System.out.println("handshake complete - calling onHandshakeComplete ...");
            streamingClient.onHandshakeComplete(response.getCookies(), response.getClientId());

        } else if (s.equals("/meta/subscribe")) {
            System.out.println("subscription complete - calling onSubscribeComplete ...");
            streamingClient.onSubscribeComplete();

        } else if (s.equals("/meta/connect")) {
            System.out.println("connect complete - calling onConnectComplete ...");
            streamingClient.onConnectComplete();

        } else {
            LOGGER.warn("Fell through completed operation recognition! Could not classify response as an expected streaming operation");

        }
        return retVal;

    }

    public boolean isResponseSucessful(NingResponse response) throws Exception {
        return (response.getBayeuxSuccessResponseField() && (response.getStatusCode() == 200));
    }


}
