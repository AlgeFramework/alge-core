package com.sfdc.http;

import junit.framework.TestCase;

/**
 * @author psrinivasan
 *         Date: 12/5/12
 *         Time: 1:14 AM
 */
public class HttpLoadGeneratorTest extends TestCase {
    public void setUp() throws Exception {

    }

    public void tearDown() throws Exception {

    }

    public void testGracefulShutdownUnderZeroLoad() throws InterruptedException {
        HttpLoadGenerator httpLoadGenerator = HttpLoadGenerator.getInstance();
        httpLoadGenerator.start();

        Thread.sleep(3000);
        httpLoadGenerator.stop();
    }
}
