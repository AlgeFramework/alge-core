package poc;

import com.ning.http.client.Response;
import com.sfdc.http.client.NingAsyncHttpClientImpl;
import com.sfdc.http.client.handler.BasicAsyncHandler;
import com.sfdc.http.util.SoapLoginUtil;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 8/29/12
 *         Time: 11:05 PM
 */
public class NingHandshakeHammer {
    private NingAsyncHttpClientImpl asyncHttpClient;
    private int num_handshakes;
    private List<Future<Response>> futures;
    private String instance;
    private String sessionId;
    private StopWatch stopWatch;


    public NingHandshakeHammer(int num_handshakes) throws Exception {
        stopWatch = new StopWatch();
        asyncHttpClient = new NingAsyncHttpClientImpl();
        this.num_handshakes = num_handshakes;
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8",
                "123456", "https://ist6.soma.salesforce.com/");
//        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8",
//                "123456", "http://ist6-app1-1-sfm.ops.sfdc.net:8085");
        sessionId = credentials[0];
        instance = credentials[1];
        futures = new ArrayList<Future<Response>>();

    }

    public void queueHandshakes() {
        stopWatch.start();

        for (int i = 0; i < num_handshakes; i++) {
            futures.add(asyncHttpClient.streamingHandshake(instance, sessionId, new BasicAsyncHandler()));
        }

    }

    public void reapHandshakeResponses() throws ExecutionException, InterruptedException {
        for (Future<Response> future : futures) {
            future.get();
        }
        stopWatch.stop();
        System.out.println("Total time taken to service " + num_handshakes + " requests = " + stopWatch.getTime() + "ms");
    }

    public static void main(String[] args) throws Exception {
        NingHandshakeHammer h = new NingHandshakeHammer(1000);
        h.queueHandshakes();
        h.reapHandshakeResponses();

    }
}
