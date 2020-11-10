package com.analytics.pgyjar.crash;

import android.util.Log;

import com.analytics.pgyjar.util.FileUtil;
import com.analytics.pgyjar.api.PgyHttpRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by liuqiang 2020-10-27 .
 */
public class PgyCrashManager {

    private static final String TAG = "PGY_PgyCrashManager";
    private static boolean submitting = false;
    private static boolean isIgnoreDefaultHander = false;

    /**
     * 注册crash上报
     * 通过 观察模式实现 Crash 的处理
     */
    public static void register() {
        PgyerCrashObservable.get().start();
    }

    public static void caughtException(Exception e) {

        Log.e(TAG, "主动开始上报");
        //主动上报
        PgyHttpRequest.getInstance().sendExceptionRequest(null, e);
    }

    public static boolean isIsIgnoreDefaultHander() {
        return isIgnoreDefaultHander;
    }

    static void saveException(String exception, ExceptionType type) {
        try {
            File file = createCrashFile(type);
            Log.d(TAG, "create Crash File  path:" + file.getAbsolutePath());
            Log.d(TAG, "Writing unhandled exception to: " + file.getAbsolutePath());

            // Write the stacktrace to disk
            BufferedWriter write = new BufferedWriter(new FileWriter(file));

            // PgySdkApp expects the package name in the first line!
            write.write(exception);
            write.flush();
            write.close();

        } catch (Exception another) {
            Log.e(TAG, "Error saving exception stacktrace!\n",
                    another);
        }
    }

    private static File createCrashFile(ExceptionType type) {
        File path = FileUtil.getCrashStorePath();
        File file = null;
        try {
            switch (type) {
                case CRASH:
                    file = File.createTempFile("crash-", ".stacktrace", path);
                    break;
                case EXCEPTION:
                    file = File.createTempFile("exception-", ".stacktrace", path);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    enum ExceptionType {
        EXCEPTION, CRASH;
    }
}
