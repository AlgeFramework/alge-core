package com.sfdc.http.queue;

/**
 * @author psrinivasan
 *         Date: 10/5/12
 *         Time: 10:47 AM
 */
public interface ConsumerInterface extends Runnable {
    @Override
    void run();

    void processWorkItem(HttpWorkItem work);

    void stop();
}
