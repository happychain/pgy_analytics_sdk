package com.analytics.pgyjar.api;

import android.util.Log;

import com.analytics.pgyjar.PACFrontjs;
import com.analytics.pgyjar.crash.PgyCrashManager;

/**
 * Created by liuqiang 2020-11-10 .
 */
public class AnalyticsSdkApi {

    private static final String TAG = "PGY_AnalyticsSdkApi";
    /**
     * 手动上报异常
     * @param e 异常信息
     */
    public static void caughtException(Exception e){
        PgyCrashManager.caughtException(e);
    }

    /**
     * 在用户结束app时调用
     * 目前主要是结束frontjs的网络请求
     */
    public static void unRegister() {
        Log.e(TAG, "解除注册");
        PACFrontjs.stopTimer();
    }
}
