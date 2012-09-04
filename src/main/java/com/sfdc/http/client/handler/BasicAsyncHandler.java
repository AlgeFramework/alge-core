package com.sfdc.http.client.handler;

import com.ning.http.client.*;
import com.sfdc.http.client.NingResponse;
import org.apache.commons.lang3.time.StopWatch;

/**
 * @author psrinivasan
 *         Date: 8/30/12
 *         Time: 10:29 AM
 */
public class BasicAsyncHandler implements com.ning.http.client.AsyncHandler {

    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
    private StopWatch stopWatch;

    public BasicAsyncHandler() {
        stopWatch = new StopWatch();
        stopWatch.start();
    }

    @Override
    public void onThrowable(Throwable throwable) {
    }

    @Override
    public AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
        builder.accumulate(httpResponseBodyPart);
        return AsyncHandler.STATE.CONTINUE;

    }

    @Override
    public AsyncHandler.STATE onStatusReceived(HttpResponseStatus status) throws Exception {
        builder.reset();
        builder.accumulate(status);
        return AsyncHandler.STATE.CONTINUE;
    }

    @Override
    public AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
        builder.accumulate(headers);
        return AsyncHandler.STATE.CONTINUE;

    }

    @Override
    public Object onCompleted() throws Exception {
        stopWatch.stop();
        Response r = builder.build();
        NingResponse sr = new NingResponse(r);
        System.out.println("elapsed time: " + stopWatch.getTime() + "; status code = " + r.getStatusCode() + "; client Id = " + sr.getClientId() + "; successful = " + sr.getBayeuxSuccessResponseField());
        return r;
    }
}
