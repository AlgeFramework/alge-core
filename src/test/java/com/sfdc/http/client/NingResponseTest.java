package com.sfdc.http.client;

import junit.framework.TestCase;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author psrinivasan
 *         Date: 10/15/12
 *         Time: 2:45 PM
 */
public class NingResponseTest extends TestCase {

    private NingResponse response;

    public void setUp() {
        response = new NingResponse(null);

    }

    public String baseParser(String responseBody, String token) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.findTokeninJSonArray(rootNode, token);
    }

    public ArrayList<String> baseParser_multiple_matches(String responseBody, String token) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.findTokeninJSonArrayAllMatches(rootNode, token);
    }

    public ArrayList<JsonNode> baseParser_multiple_matches_json(String responseBody, String token) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.findTokeninJSonArrayAllNodes(rootNode, token);
    }


    public void testParseGetClientId_1() throws Exception {
        String responseBody = null;
        responseBody =
                "[{\"channel\":\"/meta/connect\",\"clientId\":\"1ljvke3c5bpf3nv818c1ayqzg\",\"advice\":{\"reconnect\":\"retry\",\"interval\":0,\"timeout\":110000},\"successful\":true}]";
        String token = "clientId";
        String result = baseParser(responseBody, token);

        assertEquals("1ljvke3c5bpf3nv818c1ayqzg", result);
    }

    public void testParseGetClientId_2() throws Exception {
        String responseBody = null;
        responseBody =
                "[{\"channel\":\"/topic/accountTopic\",\"clientId\":\"amb1h71fnd0nw02x1bhl9ftu8qk2q\",\"data\":{\"event\":{\"type\":\"created\",\"createdDate\":\"2012-10-15T21:32:06.000+0000\"},\"sobject\":{\"Name\":\"oldAccountName\",\"StreamingIdentifier__c\":\"20-351-2\",\"Id\":\"00130000013IIxaAAG\",\"RoleMatch__c\":\"R2\"}}},{\"channel\":\"/meta/connect\",\"clientId\":\"amb1h71fnd0nw02x1bhl9ftu8qk2q\",\"advice\":{\"reconnect\":\"retry\",\"interval\":0,\"timeout\":110000},\"successful\":true}]";
        String token = "clientId";
        String result = baseParser(responseBody, token);

        assertEquals("amb1h71fnd0nw02x1bhl9ftu8qk2q", result);
    }

    public void testParseGetChannel_1() throws Exception {
        String responseBody = null;
        responseBody =
                "[{\"channel\":\"/meta/connect\",\"clientId\":\"1ljvke3c5bpf3nv818c1ayqzg\",\"advice\":{\"reconnect\":\"retry\",\"interval\":0,\"timeout\":110000},\"successful\":true}]";
        String token = "channel";
        ArrayList<String> results = baseParser_multiple_matches(responseBody, token);

        assertEquals("/meta/connect", results.get(0));
    }

    public void testParseGetChannel_2() throws Exception {
        String responseBody = null;
        responseBody =
                "[{\"channel\":\"/topic/accountTopic\",\"clientId\":\"amb1h71fnd0nw02x1bhl9ftu8qk2q\",\"data\":{\"event\":{\"type\":\"created\",\"createdDate\":\"2012-10-15T21:32:06.000+0000\"},\"sobject\":{\"Name\":\"oldAccountName\",\"StreamingIdentifier__c\":\"20-351-2\",\"Id\":\"00130000013IIxaAAG\",\"RoleMatch__c\":\"R2\"}}},{\"channel\":\"/meta/connect\",\"clientId\":\"amb1h71fnd0nw02x1bhl9ftu8qk2q\",\"advice\":{\"reconnect\":\"retry\",\"interval\":0,\"timeout\":110000},\"successful\":true}]";
        String token = "channel";
        ArrayList<String> results = baseParser_multiple_matches(responseBody, token);
        assertEquals(true, results.contains("/topic/accountTopic"));
        assertEquals(true, results.contains("/meta/connect"));
    }

    public void testParseGetDataNode() throws Exception {
        String responseBody = null;
        responseBody =
                "[{\"channel\":\"/topic/accountTopic\",\"clientId\":\"amb1h71fnd0nw02x1bhl9ftu8qk2q\",\"data\":{\"event\":{\"type\":\"created\",\"createdDate\":\"2012-10-15T21:32:06.000+0000\"},\"sobject\":{\"Name\":\"oldAccountName\",\"StreamingIdentifier__c\":\"20-351-2\",\"Id\":\"00130000013IIxaAAG\",\"RoleMatch__c\":\"R2\"}}},{\"channel\":\"/meta/connect\",\"clientId\":\"amb1h71fnd0nw02x1bhl9ftu8qk2q\",\"advice\":{\"reconnect\":\"retry\",\"interval\":0,\"timeout\":110000},\"successful\":true}]";
        String token = "data";
        ArrayList<JsonNode> results = baseParser_multiple_matches_json(responseBody, token);
        System.out.println(results.toString());
        //assertEquals(true, results.contains("/topic/accountTopic"));
        //assertEquals(true, results.contains("/meta/connect"));
    }

    public void testParseGetBayeuxSuccessResponseField() throws Exception {

    }

    public void testParseEventData_1() {
        String responseBody = null;
        responseBody =
                "[{\"event\":{\"type\":\"created\",\"createdDate\":\"2012-10-15T21:32:06.000+0000\"},\"sobject\":{\"Name\":\"oldAccountName\",\"StreamingIdentifier__c\":\"20-351-2\",\"Id\":\"00130000013IIxaAAG\",\"RoleMatch__c\":\"R2\"}}]";
        response.parseEventData(responseBody);
    }
}
