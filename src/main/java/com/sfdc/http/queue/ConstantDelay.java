package com.sfdc.http.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author psrinivasan
 *         Date: 10/2/12
 *         Time: 9:30 PM
 */
public class ConstantDelay implements DelayLogic {
    private boolean firstInvocation;
    private long lastFiringTime;
    private final long delayPeriod;  //milliseconds
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstantDelay.class);


    public ConstantDelay(long delayPeriod) {
        this.delayPeriod = delayPeriod;
        this.firstInvocation = true;
    }

    @Override
    public void delay() {
        if (firstInvocation) {
            sleep(delayPeriod);
            LOGGER.debug("Slept for " + delayPeriod + "ms");
            firstInvocation = false;
            lastFiringTime = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - lastFiringTime >= delayPeriod) {
                return; //we've waited long enough.  Perhaps even too long.
            } else {
                long period = System.currentTimeMillis() - lastFiringTime;
                sleep(period);
                LOGGER.debug("Slept for " + period + "ms");

            }
        }
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
