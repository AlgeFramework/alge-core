package com.sfdc.http.queue;

import com.ning.http.client.Cookie;
import com.sfdc.http.client.handler.StatefulHandler;

import java.util.List;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 11:00 PM
 */
public class WorkItem {
    private String sessionId;
    private String clientId;
    private String instance;
    private List<Cookie> cookies;
    private StatefulHandler handler;
    private String subscriptionChannel;

    public enum Operation {HANDSHAKE, CONNECT, SUBSCRIBE, DISCONNECT, UNSUBSCRIBE}

    private Operation operation;

    public WorkItem() {
        sessionId = null;
        clientId = null;
        instance = null;
        cookies = null;
        operation = null;
        subscriptionChannel = null;
    }

    public void setSessionId(String s) {
        sessionId = s;
    }

    public void setClientId(String c) {
        clientId = c;
    }

    public void setInstance(String i) {
        instance = i;
    }

    public void setCookies(List<Cookie> c) {
        cookies = c;
    }

    public void setOperation(Operation o) {
        operation = o;
    }

    public void setHandler(StatefulHandler handler) {
        this.handler = handler;
    }

    public void setChannel(String channel) {
        this.subscriptionChannel = channel;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getInstance() {
        return instance;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public Operation getOperation() {
        return operation;
    }

    public StatefulHandler getHandler() {
        return handler;
    }

    public String getChannel() {
        return subscriptionChannel;
    }
}
