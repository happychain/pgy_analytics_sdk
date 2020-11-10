package com.analytics.pgyjar.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.analytics.pgyjar.PACEnv;
import com.analytics.pgyjar.PACUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuqiang 2020-10-28 .
 */
public class HttpDataUtil {

    private static String sessionID = null;

    private static String TAG = "PGY_HttpDataUtil";

    /**
     * @param context
     * @param message
     * @param token
     * @return
     */
    public static String getParams(Context context, int message, String token, JSONObject params) {

        return commonParams(context, params, message, token);
    }


    private static int getNetwork(Context context) {
        int type = 0;

        switch (PACEnv.getNetWorkStatus(context)) {
            case PACEnv.NETWORK_CLASS_UNKNOWN:
                type = 0;
                break;
            case PACEnv.NETWORK_WIFI:
                type = 3;
                break;
            case PACEnv.NETWORK_CLASS_2_G:
                type = 4;
                break;
            case PACEnv.NETWORK_CLASS_3_G:
                type = 5;
                break;
            case PACEnv.NETWORK_CLASS_4_G:
            case PACEnv.NETWORK_CLASS_5_G:
                type = 6;
                break;
            default:
                type = 2;
        }

        return type;
    }


    public static String sessionID() {
        if (sessionID == null) {
            int rand = (int) (1 + Math.random() * (99999999 - 1 + 1));
            String subfix = PACUtil.md5(String.valueOf(rand)).substring(0, 16);

            Long ts = new Long(System.currentTimeMillis());
            String tsHex = Long.toHexString(ts);

            String sign = "0" + tsHex + subfix;
            sessionID = String.format("%32s", sign).replace(' ', 'f');
        }
        Log.d(TAG, "sessionID =" + sessionID);
        return sessionID;
    }

    //生成messageID
    public static String messageID() {
        int rand = (int) (1 + Math.random() * (99999999 - 1 + 1));
        String subfix = PACUtil.md5(String.valueOf(rand)).substring(0, 16);

        Long ts = new Long(System.currentTimeMillis());
        String tsHex = Long.toHexString(ts);

        String sign = "0" + tsHex + subfix;
        String messageID = String.format("%32s", sign).replace(' ', 'f');
        Log.d(TAG, "messageID =" + messageID);
        return messageID;
    }

    public static String clientID(Context context) {
        PACEnv env = new PACEnv();
        String subfix = PACUtil.md5(env.uuid(context)).substring(0, 16);

        String tsHex = "16817022c88";
        String sign = "0" + tsHex + subfix;
        return String.format("%32s", sign).replace(' ', 'f');
    }


    public static String getPullParams(Context context, String API_KEY) {
        JSONObject params = new JSONObject();

        try {
            PACEnv env = new PACEnv();
            params.put("u", env.uuid(context));
            params.put("ak", API_KEY);
            params.put("al", env.pkgs(context));
            params.put("nt", env.nt(context));
            params.put("db", env.db());
            params.put("dm", env.dm());
            params.put("av", env.av());
            params.put("ua", env.ua());
            params.put("uav", env.uav());
            params.put("lat", env.getLatitude(context));
            params.put("lng", env.getLongitude(context));

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return String.valueOf(params);
    }

    public static String getPushParams(String id) {
        JSONObject params = new JSONObject();

        try {
            params.put("id", id);
        } catch (Exception e) {

        }

        return String.valueOf(params);
    }


    public static String getErrorParams(Context context, int message, JSONObject params, String token) {
        return commonParams(context, params, message, token);
    }

    /**
     * 公共的参数
     */
    private static String commonParams(Context context, JSONObject params, int type, String token) {
        try {
            PACEnv env = new PACEnv();
            // viewPort
            JSONObject viewPort = new JSONObject();
            DisplayMetrics dm = new DisplayMetrics();
            viewPort.put("w", env.getMetrics(context).widthPixels);
            viewPort.put("h", env.getMetrics(context).heightPixels);
            viewPort.put("r", env.getMetrics(context).density);
            params.put("viewPort", viewPort);

            // deviceData
            JSONObject deviceData = new JSONObject();
            deviceData.put("network", getNetwork(context));
            deviceData.put("deviceID", env.uuid(context));
            deviceData.put("name", env.db());
            deviceData.put("brand", env.db());
            deviceData.put("model", env.dm());
            deviceData.put("isRoot", env.isDeviceRooted());
            deviceData.put("isPortrait", env.isPortrait(context));
            deviceData.put("freeDiskSpace", env.systemFreespace());
            deviceData.put("freeRam", env.freeMemory(context));
            deviceData.put("ostype", "Android");
            deviceData.put("osversion", android.os.Build.VERSION.RELEASE);
            deviceData.put("appname", env.getPackageName(context));
            deviceData.put("appversion", env.getVersionName(context));
            deviceData.put("lat", env.getLatitude(context));
            deviceData.put("lng", env.getLongitude(context));

            params.put("deviceData", deviceData);
            // other
            params.put("userAgent", env.ua());
            params.put("clientID", clientID(context));
            params.put("sessionID", sessionID());
            //如果是网页浏览则messageId 和sessionId相同
            if (type == CommonUtil.JUMP_PAGE) {
                params.put("messageID", sessionID());
            } else {
                params.put("messageID", messageID());
            }
            params.put("token", token);
            params.put("version", env.uav());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf(params);
    }
}
