package poc;

import com.ning.http.client.Response;
import com.sfdc.http.client.JettyAsyncHttpClientImpl;
import com.sfdc.http.client.handler.BasicAsyncHandler;
import com.sfdc.http.client.handler.GenericContentExchange;
import com.sfdc.http.util.SoapLoginUtil;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 4:10 PM
 */
public class JettyHandshakeHammer {

    private JettyAsyncHttpClientImpl asyncHttpClient;
    private int num_handshakes;
    private List<GenericContentExchange> exchanges;
    private String instance;
    private String sessionId;
    private StopWatch stopWatch;

    public JettyHandshakeHammer(int num_handshakes) throws Exception {
        stopWatch = new StopWatch();
        asyncHttpClient = new JettyAsyncHttpClientImpl();
        this.num_handshakes = num_handshakes;
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8",
                "123456", "https://ist6.soma.salesforce.com/");
//        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8",
//                "123456", "http://ist6-app1-1-sfm.ops.sfdc.net:8085");
        sessionId = credentials[0];
        instance = credentials[1];
        exchanges = new ArrayList<GenericContentExchange>();

    }


    public void queueHandshakes() throws IOException {
        stopWatch.start();

        for (int i = 0; i < num_handshakes; i++) {
            exchanges.add(asyncHttpClient.streamingHandshake(instance, sessionId));
        }

    }

    public void reapHandshakeResponses() throws ExecutionException, InterruptedException {
        for (GenericContentExchange exchange : exchanges) {
            exchange.waitForDone();
        }
        stopWatch.stop();
        System.out.println("Total time taken to service " + num_handshakes + " requests = " + stopWatch.getTime() + "ms");
    }

    public static void main(String[] args) throws Exception {
        JettyHandshakeHammer h = new JettyHandshakeHammer(10000);
        h.queueHandshakes();
        h.reapHandshakeResponses();

    }


}
