package com.sfdc.platform;

import com.sfdc.http.queue.HttpWorkItem;

/**
 * @author psrinivasan
 *         Date: 2/13/13
 *         Time: 6:44 PM
 */

public interface User {
    void executeNextRequest();

    void execute(HttpWorkItem w);
}
