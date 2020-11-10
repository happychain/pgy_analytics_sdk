package com.analytics.pgyjar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PACEnv {
    private static final String TAG = "PGY_PACEnv";
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_CLASS_2_G = 2;
    public static final int NETWORK_CLASS_3_G = 3;
    public static final int NETWORK_CLASS_4_G = 4;
    public static final int NETWORK_CLASS_5_G = 5;

    public String uuid(Context context) {
        String androidID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return PACUtil.md5(androidID);
    }

    public List<String> pkgs(Context context) {
        List<String> packages = new ArrayList<String>();
        try {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES |
                    PackageManager.GET_SERVICES);
            for (PackageInfo info : packageInfos) {
                if (!isSystemApp(info)) {
                    String pkg = info.packageName;
                    packages.add(pkg);
                }
            }
        } catch (Throwable t) {
        }
        return packages;
    }

    private boolean isSystemApp(PackageInfo pi) {
        boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp || isSysUpd;
    }

    public int nt(Context context) {
        return getNetWorkStatus(context);
    }

    public String db() {
        return android.os.Build.BRAND;
    }

    public String dm() {
        return android.os.Build.MODEL;
    }

    public String av() {
        return android.os.Build.VERSION.RELEASE;
    }

    public String ua() {
        return "PgyerAnalyticsSDK";
    }

    public String uav() {
        return "1.0";
    }

    /**
     * 获取当前移动信号网络类型
     *
     * @param context
     * @return
     */
    public static int getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;

            case TelephonyManager.NETWORK_TYPE_NR:
                return NETWORK_CLASS_5_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static int getNetWorkStatus(Context context) {
        int netWorkType = NETWORK_CLASS_UNKNOWN;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                int type = networkInfo.getType();

                if (type == ConnectivityManager.TYPE_WIFI) {
                    netWorkType = NETWORK_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    netWorkType = getNetWorkClass(context);
                }
            }
        } catch (Exception e) {
        }

        return netWorkType;
    }

    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public boolean isPortrait(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration();
        int orientation = mConfiguration.orientation;
        return orientation == mConfiguration.ORIENTATION_PORTRAIT;
    }

    public int externalStorageFreespace() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            int blockSize = sf.getBlockSize();
            int availCount = sf.getAvailableBlocks();

            return availCount * blockSize;
        }

        return 0;
    }

    public long systemFreespace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public long freeMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.availMem;
    }

    public DisplayMetrics getMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取为止信息
     *
     * @param context
     * @return
     */
    public Location getLocation(Context context) {
        try {
            if (!PACUtil.checkPermission(context, "android.permission.ACCESS_FINE_LOCATION")
                    && !PACUtil.checkPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
                return null;
            }

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 获取经度
     *
     * @param context
     * @return
     */
    public double getLatitude(Context context) {
        Location location = getLocation(context);
        if (location != null) {
            return location.getLatitude();
        }
        return 0;
    }

    /**
     * 获取纬度
     *
     * @param context
     * @return
     */
    public double getLongitude(Context context) {
        Location location = getLocation(context);
        if (location != null) {
            return location.getLongitude();
        }
        return 0;
    }

    /**
     * 判断当前APP是否有加入电池优化的白名单
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            }
        } catch (Exception e) {

        }
        return isIgnoring;
    }

    /**
     * 获取应用程序名称
     */
    public synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断当前网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable() {

        ConnectivityManager connectivity = (ConnectivityManager) Analytics.mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        Log.d(TAG, "Please check your device net can work.");
        return false;
    }

}