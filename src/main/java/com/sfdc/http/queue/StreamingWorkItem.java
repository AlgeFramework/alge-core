package com.sfdc.http.queue;

import com.ning.http.client.Cookie;
import com.sfdc.http.client.handler.StatefulHandler;

import java.util.List;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 11:00 PM
 */
public class StreamingWorkItem implements StreamingWorkItemInterface {
    private String sessionId;
    private String clientId;
    private String instance;
    private List<Cookie> cookies;
    private StatefulHandler handler;
    private String subscriptionChannel;

    private Operation operation;

    public StreamingWorkItem() {
        sessionId = null;
        clientId = null;
        instance = null;
        cookies = null;
        operation = null;
        subscriptionChannel = null;
    }

    @Override
    public void setSessionId(String s) {
        sessionId = s;
    }

    @Override
    public void setClientId(String c) {
        clientId = c;
    }

    @Override
    public void setInstance(String i) {
        instance = i;
    }

    @Override
    public void setCookies(List<Cookie> c) {
        cookies = c;
    }

    @Override
    public void setOperation(Operation o) {
        operation = o;
    }

    @Override
    public void setHandler(StatefulHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setChannel(String channel) {
        this.subscriptionChannel = channel;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getInstance() {
        return instance;
    }

    @Override
    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

    @Override
    public StatefulHandler getHandler() {
        return handler;
    }

    @Override
    public String getChannel() {
        return subscriptionChannel;
    }
}
