package com.sfdc.http.client;

import com.ning.http.client.Cookie;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

/**
 * @author psrinivasan
 *         Date: 8/28/12
 *         Time: 6:29 PM
 */
public class NingResponse implements com.ning.http.client.Response {

    private com.ning.http.client.Response response;

    public NingResponse(com.ning.http.client.Response response) {
        this.response = response;
    }

    @Override
    public int getStatusCode() {
        return response.getStatusCode();
    }

    @Override
    public String getStatusText() {
        return response.getStatusText();
    }

    @Override
    public byte[] getResponseBodyAsBytes() throws IOException {
        return response.getResponseBodyAsBytes();
    }

    @Override
    public InputStream getResponseBodyAsStream() throws IOException {
        return response.getResponseBodyAsStream();
    }

    @Override
    public String getResponseBodyExcerpt(int i, String s) throws IOException {
        return response.getResponseBodyExcerpt(i, s);
    }

    @Override
    public String getResponseBody(String s) throws IOException {
        return response.getResponseBody(s);
    }

    @Override
    public String getResponseBodyExcerpt(int i) throws IOException {
        return response.getResponseBodyExcerpt(i);
    }

    @Override
    public String getResponseBody() throws IOException {
        return response.getResponseBody();
    }

    @Override
    public URI getUri() throws MalformedURLException {
        return response.getUri();
    }

    @Override
    public String getContentType() {
        return response.getContentType();
    }

    @Override
    public String getHeader(String s) {
        return response.getHeader(s);
    }

    @Override
    public List<String> getHeaders(String s) {
        return response.getHeaders(s);
    }

    @Override
    public FluentCaseInsensitiveStringsMap getHeaders() {
        return response.getHeaders();
    }

    @Override
    public boolean isRedirected() {
        return response.isRedirected();
    }

    @Override
    public List<Cookie> getCookies() {
        return response.getCookies();
    }

    @Override
    public boolean hasResponseStatus() {
        return response.hasResponseStatus();
    }

    @Override
    public boolean hasResponseHeaders() {
        return response.hasResponseHeaders();
    }

    @Override
    public boolean hasResponseBody() {
        return response.hasResponseBody();
    }

    public String getClientId() throws Exception {
        String responseBody = null;
        try {
            responseBody = response.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return findTokeninJSonArray(rootNode, "clientId");
    }

    public String getChannel() throws Exception {
        String responseBody = null;
        try {
            responseBody = response.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return findTokeninJSonArray(rootNode, "channel");
    }

    /**
     * This will return a value only if the response is a valid subscribe call, else it will throw an exception.
     *
     * @return
     * @throws Exception
     */
    public String getSubscription() throws Exception {
        String responseBody = null;
        try {
            responseBody = response.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return findTokeninJSonArray(rootNode, "subscription");
    }

    public boolean getBayeuxSuccessResponseField() throws Exception {
        String responseBody = null;
        try {
            responseBody = response.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.parseBoolean(findTokeninJSonArray(rootNode, "successful"));

    }

    /**
     * Looks for strings like clientId in JSON arrays like
     * [{"channel":"/meta/connect","clientId":"1ljvke3c5bpf3nv818c1ayqzg","advice":{"reconnect":"retry","interval":0,"timeout":110000},"successful":true}]
     *
     * @param rootNode
     * @param searchString
     * @return
     */
    public String findTokeninJSonArray(JsonNode rootNode, String searchString) throws Exception {
        for (int i = 0; i < rootNode.size(); i++) {
            Iterator<String> itr = rootNode.get(i).getFieldNames();
            while (itr.hasNext()) {
                String field = itr.next();
                //System.out.println("field: " + field);
                if (field.equalsIgnoreCase(searchString)) {
                    return (rootNode.get(i).path(searchString).asText());
                }
            }
        }
        throw new Exception("could not find(or parse) " + searchString + " field in Bayeux response.  Response is " + rootNode.asText());
    }
}
