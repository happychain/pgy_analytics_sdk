package com.analytics.pgyjar;

import android.content.Context;

import com.analytics.pgyjar.functionenum.FunctionEnum;
import com.analytics.pgyjar.hotfix.HotFixUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang 2020-10-22 .
 */
public class Analytics{

    public static List<FunctionEnum> functions = new ArrayList<>();

    /**
     *摇一摇功能
     */

    private static String APP_KEY = "";
    private static String API_KEY = "";

    public static Context mContext;
    //错误日志上报
    private static String TOKEN = "";


    public static String getToken() {
        return TOKEN;
    }
    public static String getApiKey() {
        return API_KEY;
    }

    //appkey
    public static String getAppKey() {
        return APP_KEY;
    }

    //
//
    private Analytics(InitSdk builder) {
        TOKEN = builder.token;
        API_KEY = builder.apiKey;
        APP_KEY = builder.appKey;
        functions = builder.functions;
        mContext = builder.context;
    }

    public static class InitSdk {

        private String token;
        private String apiKey;
        private String appKey;
        private List<FunctionEnum> functions = new ArrayList<>();
        private Context context;


        public InitSdk setContext(Context context){
            this.context = context;
            return this;
        }

        public InitSdk addFunction(FunctionEnum function){
            functions.add(function);
            return this;
        }
        public InitSdk token(String token) {
            this.token = token;
            return this;
        }

        public InitSdk apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public InitSdk appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }


        public void build() {
            new Analytics(this);
            new HotFixUtils().initHotFix();
        }
    }




}