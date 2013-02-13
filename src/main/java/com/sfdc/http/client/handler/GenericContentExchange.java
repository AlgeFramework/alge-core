package com.sfdc.http.client.handler;

import org.apache.commons.lang3.time.StopWatch;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.http.HttpFields;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 1:36 PM
 */
public class GenericContentExchange extends ContentExchange {
    private StopWatch stopWatch;

    public GenericContentExchange() {
        super();
        stopWatch = new StopWatch();
    }

    @Override
    protected void onRequestCommitted() throws IOException {
        stopWatch.start();
        super.onRequestCommitted();
    }

    @Override
    protected void onResponseComplete() throws IOException {
        stopWatch.stop();
        HttpFields httpFields = getResponseFields();
        try {
            System.out.println("time elapsed = " + stopWatch.getTime() + "; http status = " + getResponseStatus() + "; clientId = " + getClientId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResponseComplete();
    }

    public String getClientId() throws Exception {
        String responseBody = null;
        try {
            responseBody = getResponseContent();
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
            responseBody = getResponseContent();
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
            responseBody = getResponseContent();
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
            responseBody = getResponseContent();
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
        throw new Exception("could not find(or parse) " + searchString + " field in Bayeux response");
    }


}
