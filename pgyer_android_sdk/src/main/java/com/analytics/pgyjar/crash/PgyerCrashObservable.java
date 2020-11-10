package com.analytics.pgyjar.crash;


import android.util.Log;

import com.analytics.pgyjar.util.FileUtil;
import com.analytics.pgyjar.api.PgyHttpRequest;
import com.analytics.pgyjar.util.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuqiang 2020-10-27 .
 */

public class PgyerCrashObservable extends PgyerObservable {
    PgyerObserver pgyerObserver;
    private static String TAG = "PGY_PgyerCrashObservable";

    private PgyerCrashObservable() {
        pgyerObserver = new PgyerCrashObserver();
        this.attach(pgyerObserver);
    }

    protected void start() {

        checkFileToSend();
        Log.e(TAG, "开始上报2");
        registerHandler();
    }

    /**
     * 遍历错误日志文件后上传到服务器
     */
    private void checkFileToSend() {
        File file = FileUtil.getCrashStorePath();
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) {
                    return;
                }
                for (int i = 0; i < files.length; i++) {
                    StringBuffer sendLog = new StringBuffer();
                    try {
                        //读出数据；
                        Utils.getInstance().readToBuffer(sendLog, files[i].getPath());
                        //发送数据
                        PgyHttpRequest.getInstance().sendExceptionRequest(sendLog.toString(), null);
                        //删除文件
                        FileUtil.deleteFile(files[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class PgyerCrashObservableHolder {
        private static final PgyerCrashObservable INSTANCE = new PgyerCrashObservable();
    }

    public static PgyerCrashObservable get() {
        return PgyerCrashObservableHolder.INSTANCE;
    }

    @Override
    public void detach(PgyerObserver observer) {
        if (!observer.equals(pgyerObserver)) {
            super.detach(observer);
        } else {
            Log.d(TAG, "Can't detach pgyer default observer.");
        }
    }

    private void registerHandler() {

        Thread.UncaughtExceptionHandler currentHandler = Thread
                .getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            Log.d(TAG, "Current handler class = " + currentHandler.getClass().getName());
        }

        // already registered, otherwise set new handler
        if (currentHandler instanceof ExceptionHandler) {
            Log.d(TAG, "ExceptionHandler is already reset");
        } else {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(
                    currentHandler, this));
        }

    }

}
