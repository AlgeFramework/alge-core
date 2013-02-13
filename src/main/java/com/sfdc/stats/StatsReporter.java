package com.sfdc.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author psrinivasan
 *         Date: 9/25/12
 *         Time: 1:35 PM
 */
public class StatsReporter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsReporter.class);
    private int interval = 2000; //milli seconds
    private volatile boolean run;
    private StatsManager statsManager;

    public StatsReporter() {
        run = true;
        statsManager = StatsManager.getInstance();
    }

    @Override
    public void run() {
        while (run) {
            logAggregateStats();
            logCustomStats();
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void logAggregateStats() {
        if (statsManager == null) {
            return;
        }
        LOGGER.info("Stats: handshakes=" + statsManager.getHandshakeCount()
                + " subscribes=" + statsManager.getSubscriptionCount()
                + " connects=" + statsManager.getConnectionCount()
                + " otherHttp200=" + statsManager.getOtherHttp200Count()
                + " unsuccessfulBayeuxResponses=" + statsManager.getUnsuccessfulBayeuxResponseCount()
                + " otherHttpErrors=" + statsManager.getOtherHttpErrorResponseCount()
                + " Http500Errors=" + statsManager.getHttp500Count()


        );
    }

    public void logCustomStats() {
        if (statsManager == null) {
            return;
        }
        ConcurrentHashMap hashMap = statsManager.getCustomStatsHash();
        Enumeration keys = hashMap.keys();
        String key = null;
        while (keys.hasMoreElements()) {
            key = (String) keys.nextElement();
            LOGGER.info("Stats: " + key + "=" + statsManager.getCustomStats(key));
        }
    }

    public void stop() {
        run = false;
    }
}