package com.analytics.pgyjar.crash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.analytics.pgyjar.util.CommonUtil;
import com.analytics.pgyjar.api.PgyHttpRequest;

import java.lang.reflect.Method;

/**
 * Created by liuqiang 2020-10-27 .
 */

public class PgyerActivityManager {
    @SuppressLint("StaticFieldLeak")
    private volatile static PgyerActivityManager INSTANCE;
    private Activity currentActivity;
    private static String TAG = "PGY_PgyerActivityManager";
    private PgyerActivityLifecycleCallbacks callbacks;

    private PgyerActivityManager(Application application) {
        callbacks = new PgyerActivityLifecycleCallbacks();
        application.registerActivityLifecycleCallbacks(callbacks);
    }

    public static void init(Application application) {
        if (INSTANCE == null) {
            synchronized (PgyerActivityManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PgyerActivityManager(application);
                }
            }
        }
    }

    public static boolean isSuccessSetInstance() {
        return INSTANCE != null;
    }

    public static PgyerActivityManager getInstance() {
        if (INSTANCE == null) {
            throw new Error("PGYER Analytic SDK init PgyerActivityManager is error.");
        }
        return INSTANCE;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    private class PgyerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

            // 前台可见
            Log.d(TAG, "当前activity=" + activity.getLocalClassName());
            currentActivity = activity;
            if (CommonUtil.IS_SHOW_UPDATE_DELOG) {
                PgyHttpRequest.getInstance().checkSoftwareUpdate();
                CommonUtil.IS_SHOW_UPDATE_DELOG = false;
                /**
                 * frontjs 和stack 请求网络
                 */
                try {
                    Class<?> hotFixStopUtil = Class.forName("com.analytics.pgyjar.hotfix.HotFixStopUtil");//完整类名
                    Object util = hotFixStopUtil.newInstance();//获得实例
                    Method getAuthor = hotFixStopUtil.getDeclaredMethod("registerFrontjsAndStack");//获得私有方法
                    getAuthor.setAccessible(true);//调用方法前，设置访问标志
                    getAuthor.invoke(util);//使用方法
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentActivity = activity;
        }


        @Override
        public void onActivityPaused(Activity activity) {
            // 前台不可见
            Log.d(TAG, "当前activity暂停=" + activity.getLocalClassName());
            currentActivity = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "当前activity 停止=" + activity.getLocalClassName());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "当前activity 销毁=" + activity.getLocalClassName());

            if (activity == currentActivity && PgyHttpRequest.getInstance().getDelog() != null) {
                PgyHttpRequest.getInstance().getDelog().dismiss();
                PgyHttpRequest.getInstance().setDelog();
            }
        }
    }
}
