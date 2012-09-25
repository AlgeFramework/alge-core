package com.sfdc.http.smc;

import com.ning.http.client.Cookie;
import com.ning.http.client.Response;

import java.util.List;

/**
 * @author psrinivasan
 *         Date: 9/12/12
 *         Time: 1:51 PM
 */
public class MockStreamingClientImpl implements StreamingClient {
    private StreamingClientFSMContext _fsm;
    private boolean reconnect;

    public MockStreamingClientImpl(boolean reconnect) {
        this.reconnect = reconnect;
        _fsm = new StreamingClientFSMContext(this);
        _fsm.enterStartState();
    }

    @Override
    public String getState() {
        return _fsm.getState().getName();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCookies(List<Cookie> cookies) {
    }

    @Override
    public void startHandshake() {
        System.out.println("in startHandshake: EXPECTED STATE IS INITIAL. TRUE STATE: " + getState());
        new Thread() {
            public void run() {
                onHandshakeComplete(null, null);
            }
        }.start();
        System.out.println("STARTED HANDSHAKE");
        _fsm.onStartingHandshake(null);
        System.out.println("in startHandshake: EXPECTED STATE IS HANDSHAKING. TRUE STATE: " + getState());
    }

    @Override
    public void startSubscribe() {
        System.out.println("in startSubscribe: EXPECTED STATE IS HANDSHAKEN. TRUE STATE: " + getState());
        new Thread() {
            public void run() {
                onSubscribeComplete();
            }
        }.start();
        System.out.println("STARTED SUBSCRIBE");
        _fsm.onStartingSubscribe(null);
        System.out.println("in startSubscribe: EXPECTED STATE IS SUBSCRIBING.  TRUE STATE: " + getState());
    }

    @Override
    public void startConnect() {
        System.out.println("in startConnect: EXPECTED STATE IS SUBSCRIBED OR RECONNECTING. TRUE STATE: " + getState());
        new Thread() {
            public void run() {
                onConnectComplete();
            }
        }.start();
        System.out.println("STARTED CONNECTED");
        _fsm.onStartingConnect(null);
        System.out.println("in startConnect: EXPECTED STATE IS CONNECTED. TRUE STATE: " + getState());
    }

    @Override
    public void shouldWeReconnect() {
        onFinishedScenario();
    }

    @Override
    public void clientDone() {
        System.out.println("in clientDone: EXPECTED STATE IS DONE. TRUE STATE: " + getState());

    }

    @Override
    public void clientAborted() {
        //To change body of implemented methods use File | Settings | File Templates.
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
    public void onHandshakeComplete(List<Cookie> cookies, String clientId) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("in onHandshakeComplete: EXPECTED STATE IS HANDSHAKEN.  TRUE STATE: " + getState());
        _fsm.onHandshakeComplete(null, null);
    }


    @Override
    public void onSubscribeComplete() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("in onSubscribeComplete: EXPECTED STATE IS SUBSCRIBING.  TRUE STATE: " + getState());
        _fsm.onSubscribeComplete();
    }

    @Override
    public void onConnectComplete() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("in onConnectComplete: EXPECTED STATE IS CONNECTED.  TRUE STATE: " + getState());
        _fsm.onConnectComplete();
    }

    @Override
    public void onFinishedScenario() {
        _fsm.onFinishedScenario();
        System.out.println("in onFinishedScenario: EXPECTED STATE IS DONE.  TRUE STATE: " + getState());
    }


    @Override
    public void onReconnectRequest() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onInvalidAuthCredentials(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void on500Error(Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
