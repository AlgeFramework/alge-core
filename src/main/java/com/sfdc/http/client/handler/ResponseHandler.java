package com.sfdc.http.client.handler;

import com.sfdc.http.HttpClient;

/**
 * @author psrinivasan
 *         Date: 12/5/12
 *         Time: 11:07 PM
 *         Utility class to make the process of writing a workload easy.
 */
public interface ResponseHandler {
    public void onCompleted(int statusCode,
                            String statusText,
                            String responseBody,
                            String contentType, HttpClient httpClient);
}
