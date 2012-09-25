package com.sfdc.http.loadgen;

import com.sfdc.stats.StatsReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author psrinivasan
 *         Date: 9/20/12
 *         Time: 8:46 PM
 *         <p/>
 *         Cliched entry point.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        RequestGenerator rg = new RequestGenerator();
        Thread requestsThread = new Thread(rg);
        requestsThread.start();
        Properties p = loadConfigs("src/main/resources/config.properties");
        boolean collectStats = Boolean.parseBoolean(p.getProperty("automatically_collect_operation_counts", "true"));
        Thread statsThread = null;
        StatsReporter statsReporter = new StatsReporter();
        if (collectStats) {
            statsThread = startStatsReporter(statsReporter);
        }

        try {
            Thread.sleep(600 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rg.stop();
        try {
            requestsThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for request generator thread to finish");
            e.printStackTrace();
            LOGGER.error("Bye.");
            System.exit(1);
        }
        statsReporter.stop();
        try {
            statsThread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static Properties loadConfigs(String file_name) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(file_name));
        } catch (IOException e) {
            LOGGER.warn("couldn't load config properties file.");
            e.printStackTrace();
        }
        return p;
    }

    public static Thread startStatsReporter(StatsReporter s) {
        Thread t = new Thread(s);
        t.setName("StatsReporter");
        t.start();
        return t;
    }
}
