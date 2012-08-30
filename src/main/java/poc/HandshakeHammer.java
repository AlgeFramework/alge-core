package poc;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.sfdc.http.client.AsyncHttpClient;
import com.sfdc.http.client.handler.BasicAsyncHandler;
import com.sfdc.http.util.SoapLoginUtil;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author psrinivasan
 *         Date: 8/29/12
 *         Time: 11:05 PM
 */
public class HandshakeHammer {
    private AsyncHttpClient asyncHttpClient;
    private int num_handshakes;
    private List<ListenableFuture<Response>> futures;
    private String instance;
    private String sessionId;
    private StopWatch stopWatch;


    public HandshakeHammer(int num_handshakes) throws Exception {
        stopWatch = new StopWatch();
        asyncHttpClient = new AsyncHttpClient();
        this.num_handshakes = num_handshakes;
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8",
                "123456", "https://ist6.soma.salesforce.com/");
        sessionId = credentials[0];
        instance = credentials[1];
        futures = new ArrayList<ListenableFuture<Response>>();

    }

    public void queueHandshakes() {
        stopWatch.start();

        for (int i = 0; i < num_handshakes; i++) {
            futures.add(asyncHttpClient.streamingHandshake(instance, sessionId, new BasicAsyncHandler()));
        }

    }

    public void reapHandshakeResponses() throws ExecutionException, InterruptedException {
        for (ListenableFuture<Response> future : futures) {
            future.get();
        }
        stopWatch.stop();
        System.out.println("Total time taken to service " + num_handshakes + " requests = " + stopWatch.getTime() + "ms");
    }

    public static void main(String[] args) throws Exception {
        HandshakeHammer h = new HandshakeHammer(1);
        h.queueHandshakes();
        h.reapHandshakeResponses();

    }
}
