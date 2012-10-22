package com.sfdc.http.queue;

/**
 * @author psrinivasan
 *         Date: 10/21/12
 *         Time: 7:16 PM
 */
public interface ProducerInterface {
    void publish(WorkItemInterface w);

    void stop();
}
