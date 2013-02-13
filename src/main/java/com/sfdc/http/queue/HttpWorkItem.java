package com.sfdc.http.queue;

import com.ning.http.client.Cookie;
import com.sfdc.http.client.handler.ThrottlingGenericAsyncHandler;

import java.util.HashMap;
import java.util.List;

/**
 * @author psrinivasan
 *         Date: 11/20/12
 *         Time: 4:17 PM
 */
public class HttpWorkItem {

    private String instance;
    private List<Cookie> cookies;
    private String operation;
    private ThrottlingGenericAsyncHandler handler;
    private HashMap<String, String> headers;
    private HashMap<String, String> parameters;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String HEAD = "HEAD";
    public static final String DELETE = "DELETE";
    private String postBody;


    public void setInstance(String i) {
        this.instance = i;
    }

    public void setCookies(List<Cookie> c) {
        this.cookies = c;
    }

    public void setOperation(String o) {
        this.operation = o;
    }

    public void setHandler(ThrottlingGenericAsyncHandler handler) {
        this.handler = handler;
    }

    public String getInstance() {
        return this.instance;
    }

    public List<Cookie> getCookies() {
        return this.cookies;
    }

    public String getOperation() {
        return this.operation;
    }

    public ThrottlingGenericAsyncHandler getHandler() {
        return this.handler;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getPostBody() {
        return postBody;
    }
}
