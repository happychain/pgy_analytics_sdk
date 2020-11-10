package com.analytics.pgyjar.util;


/**
 * Created by liuqiang 2020-11-05 .
 * <p>
 * 用户应用和蒲公英关联的信息
 */
public class PgyUserApplyInfo {

    private static int APP_ICON = android.R.mipmap.sym_def_app_icon;

    public static void setAppIcon(int icon) {
        APP_ICON = (icon == 0 ? android.R.mipmap.sym_def_app_icon : icon);
    }

    public static int getApplyInfo() {
        return APP_ICON;
    }

    //错误日志上报
    private static String TOKEN = "";

    public static void setToken(String token) {
        TOKEN = token;
    }

    public static String getToken() {
        return TOKEN;
    }

    private static String API_KEY = "";

    public static void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    //appkey
    private static String APP_KEY = "";

    public static void setAppKey(String appKey) {
        APP_KEY = appKey;
    }

    public static String getAppKey() {
        return APP_KEY;
    }

//
//
//    private PgyUserApplyInfo(Builder builder) {
//        this.appIcon = builder.appIcon;
//        this.token = builder.token;
//        this.apiKey = builder.apiKey;
//        this. appKey = builder.appKey;
//        this.forced=builder.forced;
//    }
//
//    private int appIcon;
//    private String token;
//    private String apiKey;
//    private String appKey;
//    private boolean forced;
//    public static class Builder {
//
//        private int appIcon;
//        private String token;
//        private String apiKey;
//        private String appKey;
//        private boolean forced;
//
//        public  Builder appIcon(int appIcon){
//            this.appIcon=appIcon;
//            return  this;
//        }
//
//        public  Builder setForced(boolean forced){
//            this.forced=forced;
//            return  this;
//        }
//        public  Builder token(String token){
//            this.token=token;
//            return  this;
//        }
//        public  Builder apiKey(String apiKey){
//            this.apiKey=apiKey;
//            return  this;
//        }
//        public  Builder appKey(String appKey){
//            this.appKey=appKey;
//            return  this;
//        }
//
//
//        public PgyUserApplyInfo build() {
//            return new PgyUserApplyInfo(this);
//        }
//    }

}
