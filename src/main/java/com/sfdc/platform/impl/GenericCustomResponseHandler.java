package com.sfdc.platform.impl;

import com.sfdc.http.HttpClient;
import com.sfdc.http.client.handler.ResponseHandler;

/**
 * @author psrinivasan
 *         Date: 2/17/13
 *         Time: 8:50 PM
 */
public class GenericCustomResponseHandler implements ResponseHandler {

    @Override
    public void onCompleted(int statusCode, String statusText, String responseBody, String contentType, HttpClient httpClient) {
        /*  ignore response completely.
         *  just call the next request once we have the response to the current requestt.
         *
         */
        com.sfdc.platform.impl.User u = (com.sfdc.platform.impl.User) httpClient;
        u.executeNextRequest();
    }
}
