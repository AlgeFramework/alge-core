package com.sfdc.http.loadgen;

import com.sfdc.http.queue.Consumer;
import com.sfdc.http.queue.Producer;
import com.sfdc.http.queue.WorkItem;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 12:41 PM
 */
public class Main {
    public static void main(String[] args) throws Exception {
        LinkedBlockingDeque<WorkItem> queue = new LinkedBlockingDeque<WorkItem>();
        Semaphore concurrencyPermit = new Semaphore(10000);
        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue, concurrencyPermit);

    }
}
