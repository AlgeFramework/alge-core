package com.sfdc.globals;

import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 3/5/13
 *         Time: 2:37 PM
 */
public class SystemWideConcurrencyLimit {
    private static SystemWideConcurrencyLimit ourInstance = new SystemWideConcurrencyLimit();
    private Semaphore systemConcurrencyLimit;
    private boolean calledOnce = false;

    public static SystemWideConcurrencyLimit getInstance() {
        return ourInstance;
    }

    private SystemWideConcurrencyLimit() {
        systemConcurrencyLimit = null;
    }

    public void setLimit(int num) {
        if (calledOnce == true) {
            return;
        }
        calledOnce = true;
        systemConcurrencyLimit = new Semaphore(num);
    }

    public Semaphore getSystemConcurrencyLimitSemaphore() {
        return systemConcurrencyLimit;
    }
}
