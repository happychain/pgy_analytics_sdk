package com.analytics.pgyjar.crash;

import android.os.AsyncTask;

import com.analytics.pgyjar.api.PgyHttpRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuqiang 2020-10-28 .
 */
public class AsyncTaskUtils {

    private static AsyncTaskUtils instance;
    private static ExecutorService newFixedThreadPool;

    private AsyncTaskUtils() {
        newFixedThreadPool = Executors.newFixedThreadPool(5);
    }

    public static AsyncTaskUtils getInstance() {//方法无需同步，各线程同时访问
        if (instance == null) {
            synchronized (PgyHttpRequest.class) {//在创建对象时再进行同步锁定
                if (instance == null) {
                    instance = new AsyncTaskUtils();
                }
            }
        }
        return instance;
    }


    public void execute(AsyncTask<Void, ?, ?> asyncTask) {
        asyncTask.executeOnExecutor(newFixedThreadPool);
    }


}
