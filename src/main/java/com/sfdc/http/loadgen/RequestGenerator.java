package com.sfdc.http.loadgen;

import com.sfdc.http.queue.WorkItem;
import com.sfdc.http.util.SoapLoginUtil;

/**
 * @author psrinivasan
 *         Date: 9/4/12
 *         Time: 11:16 AM
 */
public class RequestGenerator {
    //temporary!  we want RequestGenerator not to have instance, session id state since that
    //will be read from a different source!
    private String sessionId;
    private String instance;

    public RequestGenerator() throws Exception {
        //login here
        String[] credentials = SoapLoginUtil.login("dpham@180.private.streaming.20.org8", "123456", "https://ist6.soma.salesforce.com/");
        sessionId = credentials[0];
        instance = credentials[1];


    }

    public WorkItem generateHandshakeWorkItem() {
        WorkItem w = new WorkItem();

        w.setInstance(instance);
        w.setSessionId(sessionId);
        w.setOperation(WorkItem.Operation.HANDSHAKE);
        return w;
    }
}
