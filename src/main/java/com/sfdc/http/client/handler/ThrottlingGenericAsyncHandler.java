package com.sfdc.http.client.handler;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 9:52 PM
 */
public class ThrottlingGenericAsyncHandler extends GenericAsyncHandler {
    private Semaphore concurrencyPermit;

    public ThrottlingGenericAsyncHandler(Semaphore concurrencyPermit) {
        super();
        this.concurrencyPermit = concurrencyPermit;
    }

    @Override
    public Object onCompleted() throws Exception {
        concurrencyPermit.release();
        return super.onCompleted();
    }
}
