package com.sfdc.http.client.handler;


import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author psrinivasan
 *         Date: 8/25/12
 *         Time: 10:28 PM
 *         <p/>
 *         This handler is expected to be instantiated once per request.  Cant think of a better way
 *         to maintain request state at the moment.  We should keep this object light.
 */
public class GenericAsyncHandler implements com.ning.http.client.AsyncHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericAsyncHandler.class);
    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
    private StopWatch stopWatch;

    public GenericAsyncHandler() {
        stopWatch = new StopWatch();
    }

    /*
    * Timer is started from a request filter, this ensures that we start timing the request at the last possible
    * moment before sending the request down the wire.
    */
    public void startRequestTimer() {
        stopWatch.start();
    }

    @Override
    public void onThrowable(Throwable throwable) {
    }

    @Override
    public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
        builder.accumulate(httpResponseBodyPart);
        return STATE.CONTINUE;

    }

    @Override
    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
        builder.reset();
        builder.accumulate(status);
        return STATE.CONTINUE;
    }

    @Override
    public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
        builder.accumulate(headers);
        return STATE.CONTINUE;

    }

    @Override
    public Object onCompleted() throws Exception {
        stopWatch.stop();
        LOGGER.info("elapsed time: " + stopWatch.getTime());
        Response r = builder.build();
        byte[] bytes = r.getResponseBodyAsBytes();
        System.out.println("status code = " + r.getStatusCode());
        System.out.println("status text = " + r.getStatusText());
        System.out.println("output bytes = " + r.getResponseBody());
        System.out.println("thread name: " + Thread.currentThread().getName());
        return r;
    }
}
