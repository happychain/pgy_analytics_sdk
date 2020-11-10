package com.analytics.pgyjar.util;

import android.util.Log;

import com.analytics.pgyjar.Analytics;

import java.io.File;

/**
 * Created by liuqiang 2020-10-23 .
 */
public class FileUtil {
    private static final String TAG = "PGY_FileUtil";

    //删除文件夹和文件夹里面的文件
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            dir.renameTo(file);
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
    }

    /**
     * 根据路径删除文件夹里的文件，如果没有文件夹，创建新文件夹
     */

    public static void creatEmptyFile(File dexFile) {
        if (dexFile == null || !dexFile.exists()) {
            dexFile.mkdirs();
            Log.d(TAG, "热更新补丁目录不存在");
            return;
        } else {
            deleteDirWihtFile(dexFile);
        }
    }

    //设置crash日志存放目录
    public static File getCrashStorePath() {
        File check = Analytics.mContext.getExternalFilesDir(CommonUtil.CRASH);
        if (!check.exists()) {
            check.mkdirs();
        }
        return check;
    }


    public static void deleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }


    /**
     * 安全删除文件.
     *
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

}
