package poc;

import com.ning.http.client.*;
import com.sfdc.http.util.SoapLoginUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpClient {

    private static final int NUM_REQUESTS = 1;
    private static final boolean BLOCKING = false;
    private static final boolean THREADED = false;
    private static final String URL = "http://www.gnu.org/";
    private static final String BASE_URI = "https://ist6.soma.salesforce.com";
    //private static final String BASE_URI = "http://ist6-app1-1-sfm.ops.sfdc.net:8085";

    private static final String QUERY_URI = "https://ist6.soma.salesforce.com/services/data/v25.0/query/";
    private static final String HANDSHAKE_MESSAGE = "[{\"version\":\"1.0\",\"minimumVersion\":\"0.9\",\"channel\":\"/meta/handshake\",\"supportedConnectionTypes\": [\"long-polling\"]}]";
    private static final String CONNECT_PREFIX_MESSAGE = "[{\"channel\":\"/meta/connect\",\"clientId\":\"";
    private static final String CONNECT_POST_MESSAGE = "\",\"connectionType\":\"long-polling\"}]";
    private static final String DEFAULT_PUSH_ENDPOINT = "/cometd/25.0";
    private static final String STREAMING_URI = BASE_URI + DEFAULT_PUSH_ENDPOINT;
    private static final ProxyServer p = new ProxyServer("127.0.0.1", 8080);
    private static final String USER_NAME = "user_40_r2@180.private.streaming.100.org36";


    private static String SESSION_ID = "00DD0000001H4nX!ARoAQP00Mop0zArOpJpcTNCS5d58HZQqn0TONpjCXK_p3bzMWNvHYtQUZcIgbiPoXR.JIiV1f4NI39cKMpSkyt1vyt1My1gU";

    public static void main(String[] args) throws Exception,
            ExecutionException,
            InterruptedException, SAXException, ParserConfigurationException {
        //String [] results = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com");
        String[] results = SoapLoginUtil.login(USER_NAME, "m3FUXujlX", "https://ist6.soma.salesforce.com");

        System.out.println("Session id " + results[0] + " url " + results[1]);
        SESSION_ID = results[0];
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setIOThreadMultiplier(3).build();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(config);
        ArrayList<ListenableFuture<Response>> futures = new ArrayList<ListenableFuture<Response>>();
        Cookie c1 = new Cookie(null, "com.salesforce.LocaleInfo", "us", "/", 24 * 60 * 60 * 1000, false);
        Cookie c2 = new Cookie(null, "login", USER_NAME, "/", 24 * 60 * 60 * 1000, false);
        Cookie c3 = new Cookie(null, "sid", SESSION_ID, "/", 24 * 60 * 60 * 1000, false);
        Cookie c4 = new Cookie(null, "language", "en_US", "/", 24 * 60 * 60 * 1000, false);

        long time1 = System.currentTimeMillis();
        ListenableFuture<Response> f = null;
        for (int i = 0; i < NUM_REQUESTS; i++) {
            if (THREADED) {
                f = (asyncHttpClient.prepareGet(URL).execute(new MyRealAsyncCompletionHandler()));
                asyncHttpClient.preparePost("").addHeader(null, null).addCookie(null).execute(null);
            } else {
                f = (asyncHttpClient.preparePost(STREAMING_URI)
                        .addHeader("Authorization", "Bearer " + SESSION_ID)
                        .addHeader("Content-Type", "application/json")
                        .setBody(HANDSHAKE_MESSAGE)
                        .setProxyServer(p)
                        .addCookie(c1).addCookie(c2).addCookie(c3).addCookie(c4)
                        .execute(new FullAsyncHandler()));
            }
            futures.add(f);
            if (BLOCKING) {
                f.get();
            }
        }

        for (ListenableFuture<Response> future : futures) {
            //future.get();
            connect(future, asyncHttpClient, c1, c2, c3, c4);
        }
        System.out.println("Time taken for " + NUM_REQUESTS + " calls = " + (System.currentTimeMillis() - time1));
        System.out.println("number of threads = " + FullAsyncHandler.list.size());
    }

    public static void connect(ListenableFuture<Response> f, AsyncHttpClient asyncHttpClient, Cookie c1, Cookie c2, Cookie c3, Cookie c4) throws ExecutionException, InterruptedException, IOException {
        Response response = f.get();
        String responseBody = response.getResponseBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        //System.out.println("size of array = " + rootNode.size());
        //System.out.println("clientId: " + rootNode.get(0).path("clientId").asText());
        String clientId = rootNode.get(0).path("clientId").asText();
        List<Cookie> cookies = response.getCookies();
        System.out.println("number of handshake cookies = " + cookies.size());
        for (int i = 0; i < cookies.size(); i++) {
            System.out.println("cookie " + i + " = " + cookies.get(i));
        }
        //assume that only one cookie is sent.  bug.


        System.out.println("sending the following connect string \"" + CONNECT_PREFIX_MESSAGE + clientId + CONNECT_POST_MESSAGE + "\"");
        System.out.println("STREAMING_URI is " + STREAMING_URI);
/*
        f = (asyncHttpClient.preparePost(STREAMING_URI)
                .addHeader("Authorization", "Bearer " + SESSION_ID)
                .addHeader("Content-Type", "application/json")
                .setBody(CONNECT_PREFIX_MESSAGE + clientId + CONNECT_POST_MESSAGE)
                .addCookie(cookies.get(0))
                .execute(new FullAsyncHandler()));
*/
        f = (asyncHttpClient.preparePost(STREAMING_URI)
                .addHeader("Authorization", "Bearer " + SESSION_ID)
                .addHeader("Content-Type", "application/json")
                .setBody(CONNECT_PREFIX_MESSAGE + clientId + CONNECT_POST_MESSAGE)
                .addCookie(cookies.get(0))
                .setProxyServer(p)
                .addCookie(c1).addCookie(c2).addCookie(c3).addCookie(c4)
                .execute(new FullAsyncHandler()));
        Response r = f.get();
        System.out.println("connect response is " + r.getResponseBody());


    }


}
