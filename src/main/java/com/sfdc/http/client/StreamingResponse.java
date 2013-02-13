package com.sfdc.http.client;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author psrinivasan
 *         Date: 8/28/12
 *         Time: 6:29 PM
 */
public class StreamingResponse extends NingResponse {

    public StreamingResponse(com.ning.http.client.Response response) {
        super(response);
    }

    public String getClientId() throws Exception {
        return getTokenValue("clientId");
    }

    public ArrayList<String> getChannels() throws Exception {
        return getTokenValueAllMatches("channel");
    }

    /**
     * This will return a value only if the response is a valid subscribe call, else it will throw an exception.
     *
     * @return
     * @throws Exception
     */
    public String getSubscription() throws Exception {
        return getTokenValue("subscription");
    }

    public boolean getBayeuxSuccessResponseField() throws Exception {
        return Boolean.parseBoolean(getTokenValue("successful"));

    }

    public String getBayeuxError() throws Exception {
        return getTokenValue("error");
    }

    public HashMap<String, String> parseEventData(String data) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        System.out.println(data);
        try {
            node = mapper.readTree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (int i = 0; i < node.size(); i++) {
//            Iterator<String> itr = node.get(i).getFieldNames();
//            while (itr.hasNext()) {
//                System.out.println("array[" + i + "] field: " + node.get(i).path(itr.next()).toString());
//            }
//
//
//        }
        return null;
    }
}
