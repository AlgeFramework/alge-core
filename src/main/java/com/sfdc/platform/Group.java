package com.sfdc.platform;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 2/13/13
 *         Time: 6:45 PM
 */
public interface Group {
    public int getMaxConcurrency();

    public int getConcurrency();

    public Semaphore getConcurrencySemaphore();
}
