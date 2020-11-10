package com.analytics.pgyjar.crash;

/**
 * Created by liuqiang 2020-10-27 .
 */

public interface PgyerObserver {
    void receivedCrash(Thread thread, Throwable exception);
}