package com.analytics.pgyjar.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuqiang 2020-10-27 .
 */
public class Utils {
    private static String TAG = "PGY_Utils";

    private static Utils utils = null;

    public static Utils getInstance() {
        if (utils == null) {
            utils = new Utils();
        }
        return utils;
    }

    private Utils() {
    }

    public StringBuffer ExceptionToStringBuffer(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.getBuffer();
    }

    public void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    //验证key是否正确
    public boolean checkInt(String strInt) {

        String strPattern = "^[0-9a-f]{32}$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strInt);
        return m.matches();

    }

}