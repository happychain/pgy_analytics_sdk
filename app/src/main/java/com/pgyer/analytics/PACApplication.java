package com.pgyer.analytics;

import android.app.Application;
import com.analytics.pgyjar.functionenum.FunctionEnum;
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

        new Analytics.InitSdk()
                .setContext(application) //设置上下问对象
                .apiKey(API_KEY) //添加apikey
                .appKey(appKey)   //添加 appkey
                .token(TOKEN)    //添加 token
                .addFunction(FunctionEnum.ANALYTICE_FUNCTION_SHAKE)  //添加需要集成的功能名称
                .build();
    }
}