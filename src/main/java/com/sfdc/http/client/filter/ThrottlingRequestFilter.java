package com.sfdc.http.client.filter;

import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.RequestFilter;
import com.sfdc.http.client.handler.GenericAsyncHandler;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 6:33 PM
 *         Throttling RequestFilter does NOT throttle.  Throttling happens at the Consumer.
 *         This request filter is still useful to gather stats such as the timestamp when a request
 *         was sent to the server.
 *         We can't store pre request/response info here, since it appears(verify!) that only a single instance
 *         of a filter is created per http client instance.
 *         We should store per-request information in the handler,since we instantiate that once per connection.
 */
public class ThrottlingRequestFilter implements RequestFilter {

    public ThrottlingRequestFilter() {
    }

    /*
     * we start timing the request here, using the stop watch in the Handler.
     */
    @Override
    public <T> FilterContext<T> filter(FilterContext<T> tFilterContext) throws FilterException {
        ((GenericAsyncHandler) tFilterContext.getAsyncHandler()).startRequestTimer();
        return tFilterContext;
    }
}
