package com.sfdc.http.loadgen;

import com.sfdc.http.queue.Consumer;
import com.sfdc.http.queue.Producer;
import com.sfdc.http.queue.WorkItem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 12:41 PM
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Properties p = loadConfigProperties();
        int concurrency = Integer.parseInt(p.getProperty("handshake.poc.producer.max.concurrency", "10000"));
        int numHandshakes = Integer.parseInt(p.getProperty("handshake.poc.producer.handshake.count", "10000"));
        System.out.println("max concurrency = " + concurrency);
        System.out.println("handshake count = " + numHandshakes);

        LinkedBlockingDeque<WorkItem> queue = new LinkedBlockingDeque<WorkItem>();
        Semaphore concurrencyPermit = new Semaphore(concurrency);
        Producer producer = new Producer(queue, numHandshakes);
        Consumer consumer = new Consumer(queue, concurrencyPermit);
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        producerThread.start();
        consumerThread.start();
        System.out.println("started producers and consumers");
        producerThread.join();
        consumerThread.join();

    }

    public static Properties loadConfigProperties() throws IOException {
        Properties p = new Properties();
        //TODO: better way to specify resource paths
        p.load(new FileInputStream("src/main/resources/config.properties"));
        return p;
    }
}
