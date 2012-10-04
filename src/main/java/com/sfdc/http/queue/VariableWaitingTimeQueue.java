package com.sfdc.http.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author psrinivasan
 *         Date: 10/2/12
 *         Time: 9:19 PM
 *         This queue holds requests for a certain amount of time before making its entry available to the
 *         queue consumers.
 */
public class VariableWaitingTimeQueue<E> extends LinkedBlockingDeque<E> implements BlockingQueue<E> {
    private DelayLogic delayLogic;

    @Override
    public boolean add(E e) {
        delayLogic.delay();
        return super.add(e);
    }

    public void setDelayLogic(DelayLogic delayLogic) {
        this.delayLogic = delayLogic;

    }
}
