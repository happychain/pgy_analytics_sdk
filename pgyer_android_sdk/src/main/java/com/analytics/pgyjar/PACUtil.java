package com.analytics.pgyjar;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class PACUtil {

    private char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public String formatSize(long size) {
        String suffix = "B";
        float fSize = 0.0F;

        if (size >= 1000L) {
            suffix = "KB";
            fSize = (float) (size / 1000L);
            if (fSize >= 1000.0F) {
                suffix = "MB";
                fSize /= 1000.0F;
            }
            if (fSize >= 1000.0F) {
                suffix = "GB";
                fSize /= 1000.0F;
            }
        } else {
            fSize = (float) size;
        }
        DecimalFormat df = new DecimalFormat("#0.0");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public String formatCpuSize(long size) {
        String suffix = "KHz";
        float fSize = 0.0F;

        if (size >= 1000L) {
            suffix = "MHz";
            fSize = (float) (size / 1000L);
            if (fSize >= 1000.0F) {
                suffix = "GHz";
                fSize /= 1000.0F;
            }
            if (fSize >= 1000.0F) {
                suffix = "THz";
                fSize /= 1000.0F;
            }
        } else {
            fSize = (float) size;
        }
        DecimalFormat df = new DecimalFormat("#0.0");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static boolean checkPermission(Context context, String permission) {
        if (context == null) {
            return false;
        }

        boolean result = false;
        PackageManager pm = context.getPackageManager();
        result = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, context.getPackageName()));
        return result;
    }

    public static void log(String content) {
        Log.v("PgyerAnalytics", content);
    }

    public static String md5(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 文件的md5效验
     *
     * @param filePath
     * @return
     */
    public String fileMd5Verify(String filePath) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filePath);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }


    public String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }


}
