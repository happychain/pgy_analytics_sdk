package com.analytics.pgyjar.crash;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang 2020-10-27 .
 */
public class PgyerObservable {

    private List<PgyerObserver> list = new ArrayList<>();

    private static final String TAG = "PGY_PgyerObservable";

    public void attach(PgyerObserver observer) {
        if (!list.contains(observer)) {
            list.add(observer);
        } else {
            Log.d(TAG, "This observer is already attached.");
        }
    }

    public void detach(PgyerObserver observer) {
        if (list.contains(observer)) {
            list.remove(observer);
        }
    }

    public void notifyObservers(Thread thread, Throwable exception) {
        for (PgyerObserver observer : list) {
            Log.d(TAG, "抛出异常");
            observer.receivedCrash(thread, exception);
        }
    }
}
