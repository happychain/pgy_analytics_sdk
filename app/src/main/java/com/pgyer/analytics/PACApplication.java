package com.pgyer.analytics;

import android.app.Application;

import com.analytics.pgyjar.Analytics;

public class PACApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init(this);
    }

    private static void init(PACApplication application){
        final String API_KEY = "aaff81059ac6a9b646472c64ed83a54b";
        final String TOKEN = "0106b0ddd419ff415616c6b33920f2bb";
        final String appKey = "1272bac90fb7946cd546f8fff0515e60";
        //如果不想要自定义提醒logo则调用改方法,默认安卓原生头像
        // Analytics.helloFather(application,API_KEY,TOKEN,appKey);
        //   可以自定义logo
        Analytics.initSdk(application,API_KEY,TOKEN,appKey,R.mipmap.ic_launcher);
    }
}