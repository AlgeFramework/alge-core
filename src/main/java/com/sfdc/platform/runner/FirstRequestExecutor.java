package com.sfdc.platform.runner;

import com.sfdc.platform.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author psrinivasan
 *         Date: 3/4/13
 *         Time: 7:18 PM
 */
public class FirstRequestExecutor {

    private final ExecutorService executorService;

    public FirstRequestExecutor() {
        executorService = Executors.newFixedThreadPool(1);//TODO: parameterize the number of initial threads to be the number of groups perhaps
    }

    public void execute(final User u) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                u.executeNextRequest();
            }
        });
    }
}
