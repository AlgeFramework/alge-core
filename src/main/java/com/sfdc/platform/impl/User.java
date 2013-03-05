package com.sfdc.platform.impl;

import com.sfdc.globals.SystemWideConcurrencyLimit;
import com.sfdc.http.HttpClient;
import com.sfdc.http.client.handler.ThrottlingGenericAsyncHandler;
import com.sfdc.http.queue.HttpWorkItem;
import com.sfdc.platform.Group;
import com.sfdc.state.ShutdownBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * @author psrinivasan
 *         Date: 2/13/13
 *         Time: 9:45 PM
 *         This implements a basic user who fires requests, waits for a response,
 *         and fires another request.
 *         This user does not care about the server response, and just fires the next request after
 *         the first request is completed.
 *         <p/>
 *         This class needs to get instantiated with a knowledge of the concurrency that it's group is permitted.
 */

public class User extends HttpClient implements com.sfdc.platform.User {
    public static final int DONE = 0;  //DONE means that the user is done.  ie., no more work for this user in this scenario.
    public static final int NOT_READY = 1;  // means that I've maxxed out my concurrency(or something else) so come back to me later
    public static final int READY = 2; //means I queued request for execution - ie., a success return code.

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
    private final ArrayList<HttpWorkItem> list;
    private final int numRequests;
    private int index;
    private Future future;
    private final Group parentGroup;
    private final ShutdownBarrier shutdownBarrier;
    public boolean firstRequest;

    public User(final ArrayList<HttpWorkItem> list, com.sfdc.platform.impl.Group parentGroup, ShutdownBarrier shutdownBarrier) {
        firstRequest = true;
        this.shutdownBarrier = shutdownBarrier;
        this.list = list;
        this.parentGroup = parentGroup;
        this.numRequests = list.size();
        this.index = 0;
        /* since this is a basic User, we assign a no op handler ourselves */
        for (HttpWorkItem request : list) {
            request.setHandler(getHandlerInstance());
        }
    }


    /*
     * this is called internally by the response handler.  If this were explicitly invoked,
     * we'd need to make sure that the current request for this user was completed before
     * a new request could be made.
     *
     * but if we call this method form the response handler of the current request, we'll know
     *  for sure that this method got called after the current request is done(or errored out).
     *
     *  only time that this method is called explicitly is when
     *
     */
    public void executeNextRequest() {
        if (index >= numRequests) {
            System.out.println("=================================> ENDED USER " + hashCode());

            /*
             * Release the group semaphore so that another user in the same group as this user,
             * if waiting, can issue his requests.
             */
            System.out.println(" =======###########========= USER " + hashCode() + " releasing group permit.  num left before releasing --> " + parentGroup.getConcurrencySemaphore().availablePermits());
            parentGroup.getConcurrencySemaphore().release();
            System.out.println(" =======###########========= USER " + hashCode() + " released group permit.  num left --> " + parentGroup.getConcurrencySemaphore().availablePermits());

            /*
             * counting down on the shut down barrier should be the last thing to do before returning
             * as "done".  This is because there is likely to be another thread waiting to shutdown
             * the VM as soon as this barrier is reached, and so any code in this method after
             * shutdownBarrier.countdown() is not guaranteed to run.
             */
            shutdownBarrier.countdown();
            return;
        } else {
            if (firstRequest) {
                firstRequest = false;
                System.out.println("=================================> STARTED USER " + hashCode());
                try {
                    /*
                     * This enforces group level concurrency.  ie.,
                     * only X number of users per group can be sending requests
                     * at any given time.
                     *
                     * The group concurrency semaphore is released then the user
                     * reaches its done state.
                     */
                    System.out.println(" =======###########========= USER " + hashCode() + " TRYING to acquire.  number of group permits left --> " + parentGroup.getConcurrencySemaphore().availablePermits());
                    parentGroup.getConcurrencySemaphore().acquire();
                    System.out.println(" =======###########========= USER " + hashCode() + " ACQUIRED. number of group permits left --> " + parentGroup.getConcurrencySemaphore().availablePermits());

                    System.out.println("=================================> USER " + hashCode() + " GOT SEMAPHORE ");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HttpWorkItem w = list.get(index);
            index++;
            execute(w);
        }
    }

    /*
     *  TODO: This(createWorkItem) is a generic method to create a work item
     *  and should probably be moved out of here to some
     *  utility class.
     */
//    public static HttpWorkItem createWorkItem(String url,
//                                              List<Cookie> cookies,
//                                              String operation,
//                                              ThrottlingGenericAsyncHandler handler,
//                                              HashMap<String, String> headers,
//                                              HashMap<String, String> parameters,
//                                              String postBody) {
//        HttpWorkItem h = new HttpWorkItem();
//        h.setInstance(url);
//        h.setHandler(handler);
//        h.setOperation(operation);
//        h.setCookies(cookies);
//        h.setHeaders(headers);
//        h.setParameters(parameters);
//        h.setPostBody(postBody);
//        return h;
//    }

    @Override
    public void execute(HttpWorkItem w) {
        super.execute(w);
    }

    public ThrottlingGenericAsyncHandler getHandlerInstance() {
        ThrottlingGenericAsyncHandler handler = new ThrottlingGenericAsyncHandler(SystemWideConcurrencyLimit.getInstance().getSystemConcurrencyLimitSemaphore(),
                null,
                new GenericCustomResponseHandler(),
                this);
        return handler;
    }
}


