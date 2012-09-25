package com.sfdc.http.smc;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;

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

    void abortClientDueToBadCredentials(Response response);

    void abortClientDueTo500(Response response);


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

    void onInvalidAuthCredentials(Response response);

    void on500Error(Response response);
}
