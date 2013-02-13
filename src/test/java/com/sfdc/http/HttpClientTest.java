package com.sfdc.http;

import com.sfdc.http.client.SfdcConstants;
import com.sfdc.http.client.handler.ResponseHandler;
import junit.framework.TestCase;

import java.util.HashMap;

/**
 * @author psrinivasan
 *         Date: 12/5/12
 *         Time: 12:25 AM
 */
public class HttpClientTest extends TestCase {
    private HttpClient httpClient;
    private static final String STATIC_URL = "http://www.gnu.org/";

    public void setUp() throws Exception {
        httpClient = new HttpClient();
    }

    public void tearDown() throws Exception {
        httpClient.getHttpLoadGenerator().stop();
    }

    public void testStartGet() throws Exception {
        httpClient.startGet(STATIC_URL);
        Thread.sleep(5000);

    }

    public void testStartGetWithHandler() throws InterruptedException {
        ResponseHandler myResponseHandler = new ResponseHandler() {
            @Override
            public void onCompleted(int statusCode, String statusText, String responseBody, String contentType, HttpClient httpClient) {
                System.out.println("Status Code " + statusCode);
                System.out.println("Status Text " + statusText);
                assertEquals(200, statusCode);
                assertEquals("OK", statusText);
            }
        };
        httpClient.startGet(STATIC_URL, myResponseHandler);
        Thread.sleep(5000);
    }

    public void testStartPost() throws Exception {
        ResponseHandler myResponseHandler = new ResponseHandler() {
            @Override
            public void onCompleted(int statusCode, String statusText, String responseBody, String contentType, HttpClient httpClient) {
                System.out.println("Status Code " + statusCode);
                System.out.println("Status Text " + statusText);
                assertEquals(200, statusCode);
                assertEquals("OK", statusText);
            }
        };
/*        String body = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:partner.soap.sforce.com\">\n" +
                "   <soapenv:Header>\n" +
                "      <urn:SessionHeader>\n" +
                "         <urn:sessionId>00D30000001Il2K!ARcAQJlBCLXWtfSyf67hru4O9pkgIkSfmp34KH.Bz9fHCzHFG9.mL47wVZ7cfnGgM8O9Ok2nP43xh1emmYnHYpFSH78Wzu4W</urn:sessionId>\n" +
                "      </urn:SessionHeader>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:impersonateUser>\n" +
                "         <urn:userIds>00530000006yrtg</urn:userIds>\n" +
                "      </urn:impersonateUser>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";*/

        String body = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:partner.soap.sforce.com\">\n" +
                "   <soapenv:Header>\n" +
                "      <urn:SessionHeader>\n" +
                "         <urn:sessionId>00D30000001Il2K!ARcAQJlBCLXWtfSyf67hru4O9pkgIkSfmp34KH.Bz9fHCzHFG9.mL47wVZ7cfnGgM8O9Ok2nP43xh1emmYnHYpFSH78Wzu4W</urn:sessionId>\n" +
                "      </urn:SessionHeader>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:impersonateUser>\n" +
                "         <urn:userIds>00530000006yrtg</urn:userIds><urn:userIds>00530000006yrth</urn:userIds><urn:userIds>00530000006yrti</urn:userIds>\n" +
                "      </urn:impersonateUser>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";


        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "text/xml");
        map.put("SOAPAction", "''");
        httpClient.startPost("https://ist8.soma.salesforce.com/" + SfdcConstants.SERVICES_SOAP_PARTNER_ENDPOINT, map, body, null, myResponseHandler);
        Thread.sleep(5000);
    }
}
