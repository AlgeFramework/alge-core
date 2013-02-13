package com.sfdc.http.loadgen;

import com.sfdc.SystemInfo;
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
        checkSystemPrerequisites();
        //RequestGenerator rg = new RequestGenerator(getConfigFileLocation());
        // COMMENTED THE BELOW LINE TO REFACTOR
        //Thread requestsThread = new Thread(rg);
        Thread requestsThread = null;
        requestsThread.start();
        Properties p = loadConfigs(getConfigFileLocation());
        boolean collectStats = Boolean.parseBoolean(p.getProperty("automatically_collect_operation_counts", "true"));
        int runTime = Integer.parseInt(p.getProperty("runtime", "600000"));
        Thread statsThread = null;
        StatsReporter statsReporter = new StatsReporter();
        if (collectStats) {
            statsThread = startStatsReporter(statsReporter);
        }

        try {
            //todo: running time of the load generator - needs to be parameterized
            Thread.sleep(runTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //rg.stop();
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

    public static void checkSystemPrerequisites() {
        SystemInfo systemInfo = SystemInfo.createSystemInfo();
        int range, fds;
        try {
            range = systemInfo.getEphemeralPortCount();
            LOGGER.info("Ephemeral port range = " + range);
            System.out.println("Ephemeral port range = " + range);
            fds = systemInfo.getMaxFileDescriptors();
            LOGGER.info("File Descriptor Limit = " + fds);
            System.out.println("File Descriptor Limit = " + fds);
            if ((fds < 64000) || (range < 20000)) {
                LOGGER.error("System does not meet prerequisites of 64000 files and 20000 ephemeral ports");
            }
        } catch (Exception e) {
            LOGGER.error("COULD NOT GET EPHEMERAL PORT COUNT AND/OR FD LIMITS");
            e.printStackTrace();
        }

    }

    public static String getConfigFileLocation() {
        return System.getProperty("config.properties", "src/main/resources/config.properties");
    }
}
