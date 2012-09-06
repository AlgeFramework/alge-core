package com.sfdc.http.client.filter;

import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.ResponseFilter;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 6:33 PM
 *         Ok, we're not using this class any more.  We don't want to release the concurrency permit
 *         semaphore in the response filter since the response has not completely been received here yet.
 *         <p/>
 *         We're moving the semaphore release code to an AsyncHandlers onCompleted method.
 */
public class ThrottlingResponseFilter implements ResponseFilter {
    private Semaphore concurrencyPermit;

    public ThrottlingResponseFilter(Semaphore s) {
        concurrencyPermit = s;
    }

    @Override
    public FilterContext filter(FilterContext filterContext) throws FilterException {
        concurrencyPermit.release();
        return filterContext;
    }
}
