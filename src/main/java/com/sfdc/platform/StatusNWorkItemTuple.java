package com.sfdc.platform;

import com.sfdc.http.queue.HttpWorkItem;

/**
 * @author psrinivasan
 *         Date: 2/20/13
 *         Time: 11:18 PM
 */
public interface StatusNWorkItemTuple {
    public int getUserStatus();

    public HttpWorkItem getWorkItem();
}
