package com.sfdc.platform.impl;

import com.sfdc.http.queue.HttpWorkItem;
import com.sfdc.platform.StatusNWorkItemTuple;

/**
 * @author psrinivasan
 *         Date: 2/20/13
 *         Time: 11:19 PM
 */
public class Tuple implements StatusNWorkItemTuple {

    private int status;
    private HttpWorkItem workItem;

    public Tuple(int status, HttpWorkItem workItem) {
        this.status = status;
        this.workItem = workItem;
    }

    @Override
    public int getUserStatus() {
        return 0;
    }

    @Override
    public HttpWorkItem getWorkItem() {
        return null;
    }
}
