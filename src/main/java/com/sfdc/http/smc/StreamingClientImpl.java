package com.sfdc.http.smc;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;
import com.sfdc.http.client.NingAsyncHttpClientImpl;
import com.sfdc.http.client.handler.StatefulHandler;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 9/6/12
 *         Time: 9:07 PM
 */
public class StreamingClientImpl implements StreamingClient {
    private String sessionId;
    private String instance;
    private String clientId;
    protected final StreamingClientFSMContext _fsm;
    private final NingAsyncHttpClientImpl httpClient;
    //private StatefulHandler handler;

    public Future<Response> getCurrentFuture() {
        return currentFuture;
    }

    public void setCurrentFuture(Future<Response> currentFuture) {
        this.currentFuture = currentFuture;
    }

    private Future<Response> currentFuture; //we cant rely on the contents of this object in all cases.  thats because
    // there's no way to atomically obtain the current operation we're waiting on, and the value of the currentFuture
    //pointer.  If one assumes the future to correspond to a certain operation(ie., perhaps based on what is in flight
    //, then its a good idea to double check to make sure that one indeed has that response.

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    private List<Cookie> cookies;


    public StreamingClientImpl(String sessionId, String instance) {
        this.sessionId = sessionId;
        this.instance = instance;
        this.httpClient = new NingAsyncHttpClientImpl();
        //handler = new StatefulHandler(this);
        _fsm = new StreamingClientFSMContext(this);
    }

    public void start() {
        _fsm.setDebugFlag(true);
        _fsm.setDebugStream(System.out);
        System.out.println("DEBUG FLAG IS SET TO: " + _fsm.getDebugFlag());
        _fsm.enterStartState();
        System.out.println("start: Client State: " + getState());
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getState() {
        return _fsm.getState().getName();
    }


    /* for testing */
    protected Future<Response> getFuture() {
        return currentFuture;
    }

    /*
     * FSM RELATED ACTIONS
     */

    //this is an action initiated when in the initial state.
    @Override
    public void startHandshake() {
        //System.out.println("State before starting Handshake: Client State: " + getState());

        //System.out.println("starting handshake action");
        currentFuture = httpClient.streamingHandshake(instance, sessionId, new StatefulHandler(this, null));
        //System.out.println("Started Handshake: Client State: " + getState());
        _fsm.onStartingHandshake(currentFuture);
    }

    @Override
    public void startSubscribe() {
        //System.out.println("going to start subscribe: Client State: " + getState() + " " + System.currentTimeMillis());

        //System.out.println("start subscribe action" + " " + System.currentTimeMillis());
        currentFuture = httpClient.streamingSubscribe(instance, sessionId, cookies, clientId, "/topic/accountTopic", new StatefulHandler(this, null));
        //System.out.println("Started Subscribe: Client State: " + getState() + " " + System.currentTimeMillis());
        //todo: find a way to pull topics information from a config file.  remember, we'll have to subscribe to 3 \
        // topics, not 1.
        _fsm.onStartingSubscribe(currentFuture);
    }

    @Override
    public void startConnect() {
        //System.out.println("going to start connect: Client State: " + getState());
        currentFuture = httpClient.streamingConnect(instance, sessionId, cookies, clientId, new StatefulHandler(this, null));
        //System.out.println("Started Connect: Client State: " + getState());
        _fsm.onStartingConnect(currentFuture);
    }

    @Override
    public void shouldWeReconnect() {
        System.out.println("computing if we should reconnect ... ");
        //for now, just say no.
        _fsm.onFinishedScenario();
        //other option was
        //_fsm.onReconnectRequest();
    }

    @Override
    public void clientDone() {
        System.out.println("Client DONE!");
    }

    @Override
    public void clientAborted() {
        System.out.println("CLIENT ABORTED");
    }

    @Override
    public void abortClientDueToBadCredentials(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abortClientDueTo500(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abortClientDueToUnknownClientId(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /*
     *
     * FSM RELATED STATE TRANSITIONS
     *
     */


    //this is a transition from handshaking --> handshaken
    @Override
    public void onHandshakeComplete(List<Cookie> cookies, String clientId) {
        System.out.println("On Handshake Complete - havent changed state yet: Client State: " + getState());
        System.out.println("in StreamingClient.onHandshakeComplete ... ");
        setCookies(cookies);
        setClientId(clientId);
        _fsm.onHandshakeComplete(cookies, clientId);
    }

    //this is a transition from subscribing --> subscribed
    @Override
    public void onSubscribeComplete() {
        _fsm.onSubscribeComplete();
        System.out.println("completed subscribe, should be in subscribed: " + getState());


    }

    @Override
    public void onConnectComplete() {
        _fsm.onConnectComplete();
    }

    @Override
    public void onFinishedScenario() {
        _fsm.onFinishedScenario();
    }

    @Override
    public void onReconnectRequest() {
        _fsm.onReconnectRequest();
    }

    @Override
    public void onInvalidAuthCredentials(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void on500Error(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onUnknownClientId(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
