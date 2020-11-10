package com.analytics.pgyjar.hotfix;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.analytics.pgyjar.Analytics;
import com.analytics.pgyjar.crash.PgyCrashManager;
import com.analytics.pgyjar.crash.PgyerActivityManager;
import com.analytics.pgyjar.util.PgyUserApplyInfo;
import com.analytics.pgyjar.PACFrontjs;
import com.analytics.pgyjar.PACStack;
import com.analytics.pgyjar.api.PgyHttpRequest;
import com.analytics.pgyjar.util.Utils;


/**
 * Created by liuqiang 2020-10-30 .
 * <p>
 * 热更新结束加载数据，
 */
public class HotFixStopUtil {

    private static final String TAG = "PGY_HotFixStopUtil";
    private boolean initData() {

        /**
         * 检查更新dex 热更新文件
         */
        PgyHttpRequest.getInstance().checkPgySdk();

        if (!Utils.getInstance().checkInt(PgyUserApplyInfo.getApiKey())) {
            Toast.makeText(Analytics.mContext, "Apikey错误，错误码100001", Toast.LENGTH_LONG).show();
            Log.e(TAG,"当前用户的ApiKey错误，错误码100001，请查看 https://seed.pgyer.com/vJOlUDPI");
            return false;
        } else if (!Utils.getInstance().checkInt(PgyUserApplyInfo.getAppKey())) {
            Toast.makeText(Analytics.mContext, "Appkey错误，错误码100002", Toast.LENGTH_LONG).show();
            Log.e(TAG,"当前应用的Appkey错，错误码100002，请查看 https://seed.pgyer.com/vJOlUDPI");
            return false;

        } else if (!Utils.getInstance().checkInt(PgyUserApplyInfo.getToken())) {
            Toast.makeText(Analytics.mContext, "token错误，错误码100003", Toast.LENGTH_LONG).show();
            Log.e(TAG,"当前用户的token错误，错误码100003，请查看 https://seed.pgyer.com/vJOlUDPI");
            return false;
        }
        return true;

    }

    //初始化
    private static void initPgyerActivityManger() {
        if (Analytics.mContext instanceof Application) {
            PgyerActivityManager.init((Application) Analytics.mContext);
        } else {
            throw new Error("PGYER SDK init activity manager throw a Error");
        }
    }


    /**
     * 初始化要加载的东西
     */
    public static void startFather(){
        /**
         * 监听异常
         */
        PgyCrashManager.register();

        /**
         * activity生命周期的监听
         */
        initPgyerActivityManger();
    }


    /**
     * 在检查版本结束后通过反射调用
     *
     * 开始请求注册
     */
    private  void registerFrontjsAndStack() {
        try {
            Log.d(TAG, "开始注册");
            PACStack.run(Analytics.mContext, PgyUserApplyInfo.getApiKey());
            PACFrontjs.run(Analytics.mContext, PgyUserApplyInfo.getToken());
        } catch (Exception e) {
            Log.e(TAG, "getAndroidManifestKey e =" + e.getMessage());
            e.printStackTrace();
        }
    }
}
