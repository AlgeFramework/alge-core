package com.sfdc.http.client.filter;

import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.ResponseFilter;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 6:33 PM
 */
public class ThrottlingResponseFilter implements ResponseFilter {
    private Semaphore concurrencyPermit;

    public ThrottlingResponseFilter(Semaphore s) {
        concurrencyPermit = s;
    }

    @Override
    public <T> FilterContext<T> filter(FilterContext<T> tFilterContext) throws FilterException {
        concurrencyPermit.release();
        return tFilterContext;
    }
}
