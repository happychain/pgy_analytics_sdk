package com.analytics.pgyjar.util;

/**
 * Created by liuqiang 2020-10-23 .
 */
public class CommonUtil {

    /**
     * 当前sdk版本号
     */
    public static String CURRENT_SDK_VERSION = "000008";

    /**
     *
     */
    public static final String BASE_URL = "https://collecter.frontjs.com/";

    /**
     * 新app版本检测
     */
    public static final String CHECK_SOFTWARE = "https://www.pgyer.com/apiv2/app/check";

    /**
     * 获取SDK版本信息
     */
    public static String ACQUIRE_PGY_SDK_INFO = "https://www.frontjs.com/dist/sdk/stable.json";

    public static String FILES_PATH = "";

    public static final String SDK_NAME = "pgy_pgyeranalytics_sdk";

    public static final String DEX_DIR = ".patch";
    public static final String CRASH = "crash";

    //页面跳转或访问事件
    public static int JUMP_PAGE = 0x0400;

    //错误日志上报
    public static int ERROR_REPORT = 0x0100;

    public static boolean IS_SHOW_UPDATE_DELOG = true;
}
