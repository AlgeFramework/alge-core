package com.sfdc.platform.runner;

import com.sfdc.globals.SystemWideConcurrencyLimit;
import com.sfdc.http.queue.HttpWorkItem;
import com.sfdc.platform.impl.Group;
import com.sfdc.platform.impl.User;
import com.sfdc.state.ShutdownBarrier;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * @author psrinivasan
 *         Date: 3/5/13
 *         Time: 11:36 AM
 */
public class FirstRequestExecutorTest extends TestCase {
    public void setUp() throws Exception {

    }

    public void tearDown() throws Exception {

    }

    public void testExecute() throws Exception {

        /*
         * SETUP
         */
        SystemWideConcurrencyLimit systemWideConcurrencyLimit = SystemWideConcurrencyLimit.getInstance();
        systemWideConcurrencyLimit.setLimit(50000);
        FirstRequestExecutor f = new FirstRequestExecutor();
        ShutdownBarrier s = ShutdownBarrier.getInstance();
        s.setNumUsers(3);
        /*
         * END Of SETUP
         */

        ArrayList<HttpWorkItem> requestList = new ArrayList<HttpWorkItem>(10);
        ArrayList<HttpWorkItem> requestList2 = new ArrayList<HttpWorkItem>(10);
        ArrayList<HttpWorkItem> requestList3 = new ArrayList<HttpWorkItem>(10);


        Group group = new Group(2);
        for (int i = 0; i < 10; i++) {
            HttpWorkItem w = new HttpWorkItem();
            w.setInstance("http://localhost:8080/" + i);
            w.setOperation("GET");
            requestList.add(w);

            HttpWorkItem w2 = new HttpWorkItem();
            w2.setInstance("http://localhost:8080/" + (i + 10));
            w2.setOperation("GET");
            requestList2.add(w2);

            HttpWorkItem w3 = new HttpWorkItem();
            w3.setInstance("http://localhost:8080/" + (i + 20));
            w3.setOperation("GET");
            requestList3.add(w3);

        }
        //System.out.println("request list size " + requestList.size());
        User user = new User(requestList, group, ShutdownBarrier.getInstance());
        User user2 = new User(requestList2, group, ShutdownBarrier.getInstance());
        User user3 = new User(requestList3, group, ShutdownBarrier.getInstance());


        f.execute(user);
        f.execute(user2);
        f.execute(user3);


        s.await();
        System.out.println("DONE");
    }
}
