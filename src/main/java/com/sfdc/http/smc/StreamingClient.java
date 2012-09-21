package com.sfdc.http.smc;

import com.ning.http.client.Cookie;

import java.util.List;

/**
 * @author psrinivasan
 *         Date: 9/12/12
 *         Time: 1:46 PM
 */
public interface StreamingClient {

    String getState();

    void setCookies(List<Cookie> cookies);

    /*
    *  ACTIONS THAT THE FSM INITIATES FOR US.
    */
    //this is an action initiated when in the initial state.
    void startHandshake();

    void startSubscribe();

    void startConnect();

    void shouldWeReconnect();

    void clientDone();

    void clientAborted();


    /*
    * TRANSITIONS THAT WE INVOKE ON THE FSM
    */

    //this is a transition from handshaking --> handshaken
    void onHandshakeComplete(List<Cookie> cookies, String clientId);

    //this is a transition from subscribing --> subscribed
    void onSubscribeComplete();

    void onConnectComplete();

    void onFinishedScenario();

    void onReconnectRequest();
}
