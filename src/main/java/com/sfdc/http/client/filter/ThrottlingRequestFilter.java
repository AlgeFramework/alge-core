package com.sfdc.http.client.filter;

import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.RequestFilter;
import com.sfdc.http.client.handler.GenericAsyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 6:33 PM
 *         ONLY THING THIS FILTER DOES IS TO GATHER TIMESTAMP JUST BEFORE SENDING THE REQUEST OUT.
 *         Throttling RequestFilter does NOT throttle.  Throttling happens at the StreamingConsumer.
 *         This request filter is still useful to gather stats such as the timestamp when a request
 *         was sent to the server.
 *         We can't store pre request/response info here, since it appears only a single instance
 *         of a filter is created per http client instance.
 *         We should store per-request information in the handler,since we instantiate that once per connection.
 */
public class ThrottlingRequestFilter implements RequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingRequestFilter.class);


    public ThrottlingRequestFilter() {
    }

    /*
     * we start timing the request here, using the stop watch in the Handler.
     */
    @Override
    public FilterContext filter(FilterContext filterContext) throws FilterException {
        ((GenericAsyncHandler) filterContext.getAsyncHandler()).startRequestTimer();
        LOGGER.debug("Request Filter done.");
        return filterContext;
    }
}
