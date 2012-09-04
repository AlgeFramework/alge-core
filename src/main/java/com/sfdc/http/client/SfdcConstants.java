package com.sfdc.http.client;

/**
 * @author psrinivasan
 *         Date: 8/31/12
 *         Time: 11:15 AM
 */
public interface SfdcConstants {
    String API_VERSION = "25.0";
    String REST_URI_PREFIX = "/services/data/v";
    String QUERY = "/query/";
    String REST_URI = REST_URI_PREFIX + API_VERSION;
    String REST_QUERY_URI = REST_URI + QUERY;
    String DEFAULT_PUSH_ENDPOINT = "/cometd/25.0";
    String HANDSHAKE_MESSAGE =
            "[{\"version\":\"1.0\",\"minimumVersion\":\"0.9\",\"channel\":\"/meta/handshake\",\"supportedConnectionTypes\": [\"long-polling\"]}]";
    String CONNECT_PREFIX_MESSAGE = "[{\"channel\":\"/meta/connect\",\"clientId\":\"";
    String CONNECT_POST_MESSAGE = "\",\"connectionType\":\"long-polling\"}]";
    String SUBSCRIBE_PREFIX_MESSAGE = "[{\"channel\":\"/meta/subscribe\",\"subscription\":\"";
    String SUBSCRIBE_IN_1_MESSAGE = "\",\"clientId\":\"";
    String SUBSCRIBE_POST_MESSAGE = "\"}]";
    String DISCONNECT_PRE_MESSAGE = "[{\"channel\":\"/meta/disconnect\",\"clientId\":\"";
    String DISCONNECT_POST_MESSAGE = "\"}]";
    /* These URI suffixes are there only to make debugging server logs easier */
    String HANDSHAKE = "/handshake";
    String CONNECT = "/connect";
    String SUBSCRIBE = "/connect";
    String DISCONNECT = "/disconnect";
}
