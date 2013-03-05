package com.sfdc.platform.impl;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 2/17/13
 *         Time: 9:04 AM
 */
public class Group implements com.sfdc.platform.Group {

    //private final ArrayList<com.sfdc.platform.User> users;
    private final int maxConcurrency;
    private final Semaphore concurrencySemaphore;

    public Group(int maxConcurrency) {
        //this.users = users;
        this.maxConcurrency = maxConcurrency;
        this.concurrencySemaphore = new Semaphore(this.maxConcurrency);
    }

    @Override
    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    @Override
    public int getConcurrency() {
        return maxConcurrency - concurrencySemaphore.availablePermits();
    }

    @Override
    public Semaphore getConcurrencySemaphore() {
        return concurrencySemaphore;
    }
}
