package com.analytics.pgyjar;

import android.content.Context;

import com.analytics.pgyjar.hotfix.HotFixUtils;

/**
 * Created by liuqiang 2020-10-22 .
 */
public class Analytics{
    private static final String TAG = "PGY_Analytics";
    public static Context mContext;

    public static void initSdk(final android.app.Application context, String apiKey, String token, String appKey) {
        initSdk(context, apiKey, token, appKey, 0);
    }

    public static void initSdk(final android.app.Application context, String apiKey, String token, String appKey, int icon) {
        mContext = context;
        new HotFixUtils().initHotFix(apiKey, token, appKey, icon);
    }
}