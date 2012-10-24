package com.sfdc.http.queue;

import com.ning.http.client.Cookie;
import com.sfdc.http.client.handler.StatefulHandler;

import java.util.List;

/**
 * @author psrinivasan
 *         Date: 10/21/12
 *         Time: 9:07 PM
 */
public interface WorkItemInterface {
    void setSessionId(String s);

    void setInstance(String i);

    void setCookies(List<Cookie> c);

    void setOperation(String o);

    void setHandler(StatefulHandler handler);

    String getSessionId();

    String getInstance();

    List<Cookie> getCookies();

    String getOperation();

    StatefulHandler getHandler();

    void setClientId(String c);

    void setChannel(String channel);

    String getClientId();

    String getChannel();

}
