package com.analytics.pgyjar.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.analytics.pgyjar.Analytics;
import com.analytics.pgyjar.util.CommonUtil;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by liuqiang 2020-10-27 .
 * <p>
 * 获取全局异常
 */

public class ExceptionHandler implements UncaughtExceptionHandler {
    //不同机型的默认处理崩溃方式不同，可以通过设置为 True 忽略默认的处理方式
    private static String TAG = "PGY_ExceptionHandler";
    private boolean ignoreDefaultHandler = false;
    private UncaughtExceptionHandler defaultExceptionHandler;
    PgyerObservable pgyerObservable;

    public ExceptionHandler(UncaughtExceptionHandler defaultExceptionHandler,
                            PgyerObservable pgyerObservable) {
        this.defaultExceptionHandler = defaultExceptionHandler;
        this.pgyerObservable = pgyerObservable;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        ignoreDefaultHandler = PgyCrashManager.isIsIgnoreDefaultHander();
        if (CommonUtil.FILES_PATH == null) {
            Log.d(TAG, "文件路径不对");
            // If the files path is null, the exception can't be stored
            // Always call the default handler instead
            defaultExceptionHandler.uncaughtException(thread, exception);

        } else {
            //发送错误日志 ，接收点在HotFixUtils;
            // TODo 需要看一下能不能通过网络请求直接发出去
            pgyerObservable.notifyObservers(thread, exception);

            if (!ignoreDefaultHandler) {
                defaultExceptionHandler.uncaughtException(thread, exception);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            } else {
                Intent intent = new Intent(Analytics.mContext, PgyerActivityManager.getInstance().getCurrentActivity().getClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("crash", true);
                PendingIntent restartIntent = PendingIntent.getActivity(Analytics.mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager mgr = (AlarmManager) Analytics.mContext.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        }
    }
}
