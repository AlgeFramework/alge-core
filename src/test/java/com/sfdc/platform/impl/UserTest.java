package com.sfdc.platform.impl;

import com.sfdc.http.queue.HttpWorkItem;
import com.sfdc.state.ShutdownBarrier;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * @author psrinivasan
 *         Date: 2/26/13
 *         Time: 10:57 AM
 */
public class UserTest extends TestCase {
//    public void testExecuteNextRequest() throws Exception {
//        ArrayList<HttpWorkItem> list = new ArrayList<HttpWorkItem>(1);
//
//        for (int i = 0; i < 10; i ++) {
//            HttpWorkItem w = new HttpWorkItem();
//            w.setInstance("http://localhost:8080/");
//            w.setOperation("GET");
//            list.add(w);
//        }
//        User u = new User(list, new Group(1));
//        int status = 99;
//        while (!(status == DONE)) {
//            status = u.executeNextRequest();
//        }
//    }

    public void testExecute() throws Exception {
        ShutdownBarrier s = ShutdownBarrier.getInstance();
        s.setNumUsers(1);
        Group group = new Group(1);
        HttpWorkItem w = new HttpWorkItem();
        w.setInstance("http://localhost:8080/");
        w.setOperation("GET");
        ArrayList<HttpWorkItem> list = new ArrayList<HttpWorkItem>(1);
        list.add(w);
        User user = new User(list, group, s);
        user.executeNextRequest();
        s.await();
    }

    public void testExecute_2() throws Exception {
        ShutdownBarrier s = ShutdownBarrier.getInstance();
        s.setNumUsers(1);
        ArrayList<HttpWorkItem> list = new ArrayList<HttpWorkItem>(10);
        Group group = new Group(1);
        for (int i = 0; i < 10; i++) {
            HttpWorkItem w = new HttpWorkItem();
            w.setInstance("http://localhost:8080/");
            w.setOperation("GET");
            list.add(w);
        }
        User user = new User(list, group, ShutdownBarrier.getInstance());
        user.executeNextRequest();
        s.await();
    }
}
