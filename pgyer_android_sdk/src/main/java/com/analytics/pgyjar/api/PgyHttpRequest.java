package com.analytics.pgyjar.api;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.analytics.pgyjar.Analytics;
import com.analytics.pgyjar.PACHttpRequestCallback;
import com.analytics.pgyjar.PACUtil;
import com.analytics.pgyjar.crash.AsyncTaskUtils;
import com.analytics.pgyjar.crash.PgyerActivityManager;
import com.analytics.pgyjar.dialog.PgyerDialogBuilder;
import com.analytics.pgyjar.downhotfixfile.DownLoaderTask;
import com.analytics.pgyjar.hotfix.HotFixStopUtil;
import com.analytics.pgyjar.model.PgySdkInfoModel;
import com.analytics.pgyjar.settingData.GetPgySettingData;
import com.analytics.pgyjar.util.CommonUtil;
import com.analytics.pgyjar.util.FileUtil;
import com.analytics.pgyjar.util.HttpDataUtil;
import com.analytics.pgyjar.util.HttpURLConnectionBuilder;
import com.analytics.pgyjar.util.PgyUserApplyInfo;
import com.analytics.pgyjar.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.analytics.pgyjar.util.SteamToStringUtil.getStringFromConnection;

/**
 * Created by liuqiang 2020-10-23 .
 */
public class PgyHttpRequest implements DownLoaderTask.ExtractorProgressToSuccess {

    private static final String TAG = "PGY_PgyApi";
    private static PgyHttpRequest instance;
    private String md5;
    private  Dialog dialog = null;

    private PgyHttpRequest() {

    }

    public static PgyHttpRequest getInstance() {//方法无需同步，各线程同时访问
        if (instance == null) {
            synchronized (PgyHttpRequest.class) {//在创建对象时再进行同步锁定
                if (instance == null) {
                    instance = new PgyHttpRequest();
                }
            }
        }
        return instance;
    }

    private Object readResolve() throws ObjectStreamException {
        return instance;
    }

    public Dialog getDelog() {
        return dialog;
    }

    public void  setDelog() {
         dialog = null;
    }

    //触发获取最新的热更新包下载信息

    public void checkPgySdk() {
        RequestUpDatePgySdkInfoAsyncTask asyncTask = new RequestUpDatePgySdkInfoAsyncTask(CommonUtil.ACQUIRE_PGY_SDK_INFO, new HttpRequestCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "checkPgySdk onSuccess");
                List<PgySdkInfoModel> pgySdkInfoModels = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("PgyerAnalyticsSDK");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        PgySdkInfoModel pgySdkInfoModel = new PgySdkInfoModel();
                        pgySdkInfoModel.setType(object.getString("type"));
                        pgySdkInfoModel.setVersion(object.getString("version"));
                        pgySdkInfoModel.setVersionCode(object.getString("versionCode"));
                        pgySdkInfoModel.setUrl(object.getString("url"));
                        pgySdkInfoModel.setMd5(object.getString("md5"));
                        pgySdkInfoModels.add(pgySdkInfoModel);
                    }
                    int dexIndex = 1;
                    if (pgySdkInfoModels.get(0).getType().equals("dex")) {
                        dexIndex = 0;
                    }
                    downPgySdk(pgySdkInfoModels.get(dexIndex));

                } catch (Exception e) {
                    Log.e(TAG, "RequestUpDatePgySdkInfoAsyncTask analysis is fail e =" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                HotFixStopUtil.startFather();
            }
        });

        AsyncTaskUtils.getInstance().execute(asyncTask);
    }

    @Override
    public void setProgressMaxLength(int maxLength) {
        // TODO dex包总大小
        Log.d(TAG, "maxLength = " + maxLength);
    }

    @Override
    public void setCurrentProgress(int current) {
        // TODO 下载当前进度
        Log.d(TAG, "current = " + current);
    }

    @Override
    public void setDownLoadSuccess(String path) {
        // TODO 成功
        Log.d(TAG, "setDownLoadSuccess");
        String dexMd5 = new PACUtil().fileMd5Verify(path);

        if (!md5.equals(dexMd5)) {
            Log.d(TAG, "setDownLoadSuccess Verify Fail");
            //下载有问题，需要删除当前dex文件包
            FileUtil.deleteDirWihtFile(new File(path));
        }
        HotFixStopUtil.startFather();
    }

    @Override
    public void onDownFail() {
        // TODO 失败
        Log.d(TAG, "onDownFail");
        HotFixStopUtil.startFather();
    }


    /**
     * 获取更新sdk信息和下载路径
     */
    public class RequestUpDatePgySdkInfoAsyncTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        private String url;
        private HttpRequestCallBack callBack;

        public RequestUpDatePgySdkInfoAsyncTask(String url, HttpRequestCallBack callBack) {
            this.url = url;
            this.callBack = callBack;
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String, String> result = new HashMap<>();
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = new HttpURLConnectionBuilder(url)
                        .setRequestMethod("GET")
                        .build();
                urlConnection.connect();
                result.put("status", String.valueOf(urlConnection.getResponseCode()));
                result.put("response", getStringFromConnection(urlConnection));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            Log.d(TAG, "RequestUpDatePgySdkInfoAsyncTask onPostExecute");
            String status = result.get("status");
            if (status == null) {
                Log.d(TAG, "current status is null");
                return;
            }
            if ("200".equals(status)) {

                if (callBack != null) {
                    callBack.onSuccess(result.get("response"));

                }
            } else {
                if (callBack != null) {
                    callBack.onFail();
                }
                Log.e(TAG, "RequestUpDatePgySdkInfoAsyncTask request is fail status=" + status);
            }
        }
    }


    /**
     * 去下载最新版本的dex文件包
     *
     * @return
     */

    private void downPgySdk(PgySdkInfoModel pgySdkInfoModel) {


        Log.d("PGY_HotFixStopUtil","SDK初始化成功");

        int versionCode = Integer.parseInt(pgySdkInfoModel.getVersionCode());
        md5 = pgySdkInfoModel.getMd5();
        File dexFile = Analytics.mContext.getExternalFilesDir(CommonUtil.DEX_DIR);
        Log.d(TAG, "The current version is new versionCode =" + CommonUtil.CURRENT_SDK_VERSION);
        if (versionCode <= Integer.parseInt(CommonUtil.CURRENT_SDK_VERSION)) {
            HotFixStopUtil.startFather();
            return;
        }

        // 先判文件存不存在，再去下载
        FileUtil.creatEmptyFile(dexFile);
        DownLoaderTask downLoaderTask = new DownLoaderTask(pgySdkInfoModel.getUrl(), dexFile.getAbsolutePath());
        downLoaderTask.setProgressCallBack(this);
        AsyncTaskUtils.getInstance().execute(downLoaderTask);
    }

    /**
     * 上报
     */
    public static class PACHttpRequestAsyncTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        private String address;
        private String params;
        private PACHttpRequestCallback listener;

        public PACHttpRequestAsyncTask(String url, String params, PACHttpRequestCallback listener) {
            this.address = url;
            this.params = params;
            this.listener = listener;
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String, String> result = new HashMap<>();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(1500);
                connection.setReadTimeout(1500);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStream os = connection.getOutputStream();
                os.write(params.getBytes());
                os.close();
                int responseCode = connection.getResponseCode();
                result.put("status", String.valueOf(responseCode));
                result.put("response", getStringFromConnection(connection));

            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    Log.e(TAG, "Exception e =" + e.getMessage());
                    listener.onError(e);
                }
            }finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            Log.d(TAG, "请求结束");
            String status = result.get("status");
            if (status == null) {
                Log.d(TAG, "current status is null");
                return;
            }
            if ("200".equals(status)) {
                if (listener != null) {
                    listener.onFinish(result.get("response"));
                }

            } else {
                if (listener != null) {
                    listener.onError(new Exception());
                }
            }
        }
    }

    public void sendPACHttpRequest(String url, String param, PACHttpRequestCallback callback) {
        PACHttpRequestAsyncTask asyncTask = new PACHttpRequestAsyncTask(url, param, callback);
        AsyncTaskUtils.getInstance().execute(asyncTask);
    }

    /**
     * 上报错误数据
     */
    public void sendExceptionRequest(String content, Throwable exception) {
        String info;
        try {
            Log.e(TAG, "发送错误信息");
            if (content == null) {
                JSONObject params = new JSONObject();
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
                info = HttpDataUtil.getErrorParams(Analytics.mContext, CommonUtil.ERROR_REPORT, params, PgyUserApplyInfo.getToken());
            } else {
                info = content;
            }
            PgyHttpRequest.getInstance().sendPACHttpRequest(CommonUtil.BASE_URL, info, null);
        } catch (Exception e) {

        }
    }

    /**
     * 检查新版本
     */
    public void checkSoftwareUpdate() {
        StringBuilder builder = new StringBuilder("?_api_key=");
        builder.append(PgyUserApplyInfo.getApiKey());
        builder.append("&");
        builder.append("appKey=");
        builder.append(PgyUserApplyInfo.getAppKey());
        builder.append("&buildVersion=");
        builder.append(GetPgySettingData.getVersionCode());
        Log.e(TAG,"开始新版本检查网络请求");
        RequestUpDatePgySdkInfoAsyncTask asyncTask = new RequestUpDatePgySdkInfoAsyncTask(CommonUtil.CHECK_SOFTWARE + builder.toString(), new HttpRequestCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG,"检查新版本结束");
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") != 0) {
                        Log.e(TAG, "checkSoftwareUpdate 请求失败 message= " + jsonObject.getString("message"));
                        return;
                    }
                    String data = jsonObject.getString("data");
                    JSONObject jsonData = new JSONObject(data);
                    boolean isNeedForceUpdate = jsonData.getBoolean("needForceUpdate");
                    boolean buildHaveNewVersion = jsonData.getBoolean("buildHaveNewVersion");
                    //判断是否强制更新
                    if (isNeedForceUpdate) {
                        if (PgyerActivityManager.getInstance().getCurrentActivity() == null) {
                            Log.e(TAG, "current activity is null");
                            return;
                        }
                        String buildUpdateDescription = jsonData.getString("buildUpdateDescription");
                        String buildVersionNo = jsonData.getString("buildVersionNo");
                        final String buildShortcutUrl = jsonData.getString("buildShortcutUrl");
                        Log.d(TAG, "开始调用delog");
                        PgyerDialogBuilder builder = new PgyerDialogBuilder(PgyerActivityManager.getInstance().getCurrentActivity())
                                .setSimple(false).setCancelable(false);
                        builder.setDescription(buildUpdateDescription);
                        builder.setCurrentVersion(buildVersionNo);
                        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int which) {
                                try { // 这一块是点击按钮后不消失
                                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialogInterface, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "Exception e=" + e.getMessage());
                                }
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(buildShortcutUrl);
                                intent.setData(content_url);
                                PgyerActivityManager.getInstance().getCurrentActivity().startActivity(intent);
                            }
                        });

                        dialog = builder.create();
                        dialog.show();
                        Log.e(TAG,"检查新版本弹框结束");
                    } else {
                        //判断是否要更新 如果不相等则需要提示更新
                        if (buildHaveNewVersion) {
                            String buildUpdateDescription = jsonData.getString("buildUpdateDescription");
                            String buildVersionNo = jsonData.getString("buildVersionNo");
                            String buildShortcutUrl = jsonData.getString("buildShortcutUrl");
                            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(buildShortcutUrl));
                            PendingIntent pendingIntent = PendingIntent.getActivity(PgyerActivityManager.getInstance().getCurrentActivity(), 0, in, 0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel("1", "channel_name", NotificationManager.IMPORTANCE_HIGH);
                                NotificationManager manager = (NotificationManager) PgyerActivityManager.getInstance().getCurrentActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.createNotificationChannel(channel);
                                Notification.Builder builder = new Notification.Builder(PgyerActivityManager.getInstance().getCurrentActivity());
                                builder.setTicker("Pgy Ticker")
                                        .setContentTitle("版本更新")
                                        .setContentText(buildUpdateDescription)
                                        .setSmallIcon(PgyUserApplyInfo.getApplyInfo())
                                        .setAutoCancel(true)
                                        .setLargeIcon(GetPgySettingData.getBitmap())
                                        .setContentIntent(pendingIntent)
                                        .setChannelId("1");
                                manager.notify(1, builder.build());
                            } else {

                                Notification.Builder builder = new Notification.Builder(PgyerActivityManager.getInstance().getCurrentActivity());
                                builder.setTicker("Pgy Ticker")
                                        .setContentTitle("版本更新")
                                        .setContentText(buildUpdateDescription)
                                        .setSmallIcon(PgyUserApplyInfo.getApplyInfo())
                                        .setContentIntent(pendingIntent)
                                        .setLargeIcon(GetPgySettingData.getBitmap());
                                getNotificationManager().notify(1, builder.getNotification());

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSONException e=" + e.getMessage());
                }
            }

            @Override
            public void onFail() {

            }
        });
        AsyncTaskUtils.getInstance().execute(asyncTask);
    }

    public interface HttpRequestCallBack {
        void onSuccess(String result);

        void onFail();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) PgyerActivityManager.getInstance().getCurrentActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
