package com.sfdc.state;

import java.util.concurrent.CountDownLatch;

/**
 * @author psrinivasan
 *         Date: 2/27/13
 *         Time: 10:44 AM
 *         <p/>
 *         Each User object will call countdown on this latch
 *         after they have reached their DONE state.
 *         <p/>
 *         If the User object for some reason finds out that it will not
 *         reach it's DONE state it should call countdown as soon as it finds
 *         that out.
 */
public class ShutdownBarrier {
    private static ShutdownBarrier ourInstance = new ShutdownBarrier();
    private CountDownLatch userLatch;

    public static ShutdownBarrier getInstance() {
        return ourInstance;
    }

    private ShutdownBarrier() {
    }

    public void setNumUsers(int num) {
        this.userLatch = new CountDownLatch(num);
    }

    public void countdown() {
        userLatch.countDown();
    }

    public void await() throws InterruptedException {
        userLatch.await();
        System.out.println("================ Crossed the shutdown barrier ================");
    }
}
