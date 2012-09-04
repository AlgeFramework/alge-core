package com.sfdc.http.client;

import com.sfdc.http.client.handler.GenericContentExchange;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.AbstractBuffer;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

import java.io.IOException;

/**
 * @author psrinivasan
 *         Date: 8/30/12
 *         Time: 11:59 PM
 */
public class JettyAsyncHttpClientImpl extends HttpClient {
    public JettyAsyncHttpClientImpl() throws Exception {
        super();
        setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        start();
    }

    public GenericContentExchange streamingHandshake(String instance, String sessionId) throws IOException {
        GenericContentExchange exchange = new GenericContentExchange();
        //ContentExchange exchange = new ContentExchange();
        exchange.setMethod("POST");
        exchange.setURL(instance + SfdcConstants.DEFAULT_PUSH_ENDPOINT + SfdcConstants.HANDSHAKE);
        exchange.setRequestHeader("Authorization", "Bearer " + sessionId);
        exchange.setRequestHeader("Content-Type", "application/json");
        AbstractBuffer buffer = new ByteArrayBuffer(SfdcConstants.HANDSHAKE_MESSAGE.getBytes());
        exchange.setRequestContent(buffer);
        send(exchange);
        return exchange;
    }

}
