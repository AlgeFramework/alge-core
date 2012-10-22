package com.sfdc.http.queue;

import com.sfdc.stats.StatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 11:01 AM
 *         Pushes requests into the work queue for consumption by
 *         consumers.
 */
public class StreamingProducer implements ProducerInterface {
//    public class StreamingProducer implements Runnable {

    private final BlockingQueue<WorkItemInterface> queue;
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingProducer.class);
    //private int numHandshakes;
    //private SessionIdReader sessionIdReader;
    // TODO:  temporary declaration - it's not clear that we want request generator to be a instance var.
    //private RequestGeneratorPrototype requestGeneratorPrototype;
    //private String instance;
    private volatile boolean run;
    private final boolean collectQueueStats;
    private final StatsManager statsManager;


/*    public StreamingProducer(BlockingQueue<StreamingWorkItem> queue, int numHandshakes, SessionIdReader sessionIdReader, String instance) throws Exception {
        run = true;
        this.queue = queue;
        //requestGeneratorPrototype = new RequestGeneratorPrototype();
        //this.numHandshakes = numHandshakes;
        //this.sessionIdReader = sessionIdReader;
        //this.instance = instance;
    }*/

    public StreamingProducer(BlockingQueue<WorkItemInterface> queue, boolean collectQueueStats, StatsManager statsManager) {
        this.statsManager = statsManager;
        run = true;
        this.queue = queue;
        this.collectQueueStats = collectQueueStats;

        if (collectQueueStats && statsManager != null) {
            initializeQueueStatsCollection();
        }
    }

    private void initializeQueueStatsCollection() {
        statsManager.createCustomStats(ProducerConsumerQueue.QUEUE_STATS_METRIC);
    }

    /*
    @Override
    public void run() {
        //publish(numHandshakes);
        try {
            publishFromSessionIdFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("StreamingProducer is done.  exiting");
    }*/

/*    public void publish(int num_requests) {
        for (int i = 0; i < num_requests; i++) {
            boolean result = queue.add(requestGeneratorPrototype.generateHandshakeWorkItem());
            if (!result) {
                LOGGER.warn("Failed to publish request to queue");
            }
        }
    }*/

    @Override
    public void publish(WorkItemInterface w) {
        if (!run) {
            LOGGER.warn("PRODUCER IS STOPPED BUT ATTEMPT TO PUBLISH WAS MADE");
            return;
        }
        if (!queue.add(w)) {
            LOGGER.warn("Failed to publish request to queue");
        } else {
            if (collectQueueStats && statsManager != null) {
                statsManager.incrementCustomStats(ProducerConsumerQueue.QUEUE_STATS_METRIC);
            }
        }
    }

/*    public void publishFromSessionIdFile() throws IOException {
        String sessionId;
        while ((sessionId = sessionIdReader.getOneSessionId()) != null) {
            boolean result = queue.add(requestGeneratorPrototype.generateHandshakeWorkItem(sessionId, instance));
            if (!result) {
                LOGGER.warn("Failed to publish request to queue");
            }
        }
    }*/

    @Override
    public void stop() {
        run = false;
    }
}
