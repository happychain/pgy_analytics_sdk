package com.analytics.pgyjar.crash;


import com.analytics.pgyjar.Analytics;
import com.analytics.pgyjar.util.CommonUtil;
import com.analytics.pgyjar.util.HttpDataUtil;
import com.analytics.pgyjar.util.PgyUserApplyInfo;
import com.analytics.pgyjar.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuqiang 2020-10-27 .
 * <p>
 * 保存错误信息到本地文件夹
 */

class PgyerCrashObserver implements PgyerObserver {

    PgyerCrashObserver() {
    }

    @Override
    public void receivedCrash(Thread thread, Throwable exception) {
        JSONObject params = new JSONObject();
        try {
            params.put("type", CommonUtil.ERROR_REPORT);
            // data
            JSONObject data = new JSONObject();
            data.put("message", CommonUtil.ERROR_REPORT);

            JSONObject detail = new JSONObject();
            detail.put("err", exception.getLocalizedMessage());
            detail.put("file", exception.getStackTrace()[0].getClassName());
            detail.put("line", exception.getStackTrace()[0].getLineNumber());
            detail.put("column", 0);
            detail.put("trace", Utils.getInstance().ExceptionToStringBuffer(exception).toString());//错误内容
            data.put("detail", detail);
            params.put("data", data);
            String info = HttpDataUtil.getErrorParams(Analytics.mContext, CommonUtil.ERROR_REPORT, params, PgyUserApplyInfo.getToken());
            PgyCrashManager.saveException(info, PgyCrashManager.ExceptionType.CRASH);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
