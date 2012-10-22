package com.sfdc.http.queue;

/**
 * @author psrinivasan
 *         Date: 10/21/12
 *         Time: 9:05 PM
 */
public interface StreamingWorkItemInterface extends WorkItemInterface {

    void setClientId(String c);

    void setChannel(String channel);

    String getClientId();

    String getChannel();

}
