package com.analytics.pgyjar;

import android.content.Context;
import android.util.Log;

import com.analytics.pgyjar.util.CommonUtil;
import com.analytics.pgyjar.util.HttpDataUtil;
import com.analytics.pgyjar.api.PgyHttpRequest;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class PACFrontjs {

    private static final String TAG = "PGY_PACFrontjs";
    static String token = null;
    static int heartInterval = 10;
    static int view = 0;

    public static Timer timer;

    public static void run(final Context context, final String token) {
        PACFrontjs.token = token;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                send(context);
                view += heartInterval;
            }
        }, 0, heartInterval * 1000);

    }

    public static void stopTimer() {
        if (timer != null) {
            Log.e(TAG, "结束请求PACFrontjs");
            timer.cancel();
        }
    }

    public static void send(final Context context) {
        // https://front.pgyer.yunhuiju.com/collecter/
        // https://collecter.frontjs.com/
        Log.e(TAG, "开始请求PACFrontjs");
        JSONObject params = new JSONObject();
        PACEnv env = new PACEnv();
        try {
            params.put("type", CommonUtil.JUMP_PAGE);
            // data
            JSONObject data = new JSONObject();
            data.put("message", CommonUtil.JUMP_PAGE);

            JSONObject detail = new JSONObject();
            detail.put("view", view == 0 ? 1 : view);
            data.put("detail", detail);
            params.put("data", data);
            PgyHttpRequest.getInstance().sendPACHttpRequest(CommonUtil.BASE_URL,
                    HttpDataUtil.getParams(context, CommonUtil.JUMP_PAGE, token, params), null);
        } catch (Exception e) {

        }
    }
}
