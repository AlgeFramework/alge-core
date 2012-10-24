package com.sfdc.http.queue;

/**
 * @author psrinivasan
 *         Date: 10/21/12
 *         Time: 9:05 PM
 *         DEPRECATED.  THE CODE DOESN"T USE THIS ANYWHERE.  CLASS NEEDS TO BE REMOVED FROM BUILD.
 */
public interface StreamingWorkItemInterface extends WorkItemInterface {

    void setClientId(String c);

    void setChannel(String channel);

    String getClientId();

    String getChannel();


}
