package com.analytics.pgyjar.downhotfixfile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by liuqiang on 2020/10/23.
 * 下载zip
 */

public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
    private final String TAG = "PGY_DownLoaderTask";
    private URL mUrl;
    private File mFile;
    private int mProgress = 0;
    private ProgressReportingOutputStream mOutputStream;
    private String out;
    //保存文件名
    private String fileName;
    private int length;

    public DownLoaderTask(String url, String out) {
        super();
        this.out = out;
        try {
            mUrl = new URL(url);
            fileName = "classes.dex";//目前写写死
            mFile = new File(this.out, fileName);
            if (mFile.exists()) {//如果文件存在需要删除掉
                mFile.delete();
                mFile.mkdirs();
            }
        } catch (IOException e) {
            Log.e(TAG, "DownLoaderTask-->IOException=" + e.getMessage());
            e.printStackTrace();
        }

    }

    //    这个方法会在后台任务开始执行之间调用，用于进行一些界面上的初始化操作，比如显示一个进度条对话框等。
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected Long doInBackground(Void... params) {
        // TODO Auto-generated method stub
        return download();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        Log.d(TAG, " onProgressUpdate values.length= " + values.length);
        if (values.length > 1) {
            int contentLength = values[1];
            if (contentLength == -1) {
            } else {
                if (extractorProgressToSuccess != null)
                    extractorProgressToSuccess.setProgressMaxLength(contentLength);
            }
        } else {
            if (extractorProgressToSuccess != null)
                extractorProgressToSuccess.setCurrentProgress(values[0].intValue());
        }

        Log.e(TAG, " onProgressUpdate values[0].intValue= " + (float) values[0].intValue());
        int process = (int) ((float) values[0].intValue() / Float.valueOf(String.valueOf(this.length)).floatValue() * 100.0F);
        Log.e(TAG, " onProgressUpdate processh= " + process);
    }

    @Override
    protected void onPostExecute(Long result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (isCancelled())
            return;
        try {

            Log.e(TAG, "下载文件为FIle");
            extractorProgressToSuccess.setDownLoadSuccess(out + "/" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long download() {
        URLConnection connection = null;
        int bytesCopied = 0;
        try {
            connection = mUrl.openConnection();
            connection.setReadTimeout(30 * 1000);
            length = connection.getContentLength();
            mOutputStream = new ProgressReportingOutputStream(mFile);
            publishProgress(0, length);
            bytesCopied = copy(connection.getInputStream(), mOutputStream);
            if (bytesCopied != length && length != -1) {
            }
            mOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "download=" + e.getMessage());
            e.printStackTrace();
        }
        return bytesCopied;
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "copy1=" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "copy2=" + e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "copy3=" + e.getMessage());
                e.printStackTrace();
            }
        }
        return count;
    }

    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            publishProgress(mProgress);
        }

    }

    /**
     * 下载过程中的回调
     */
    public interface ExtractorProgressToSuccess {
        void setProgressMaxLength(int maxLength);

        void setCurrentProgress(int current);

        void setDownLoadSuccess(String path);

        void onDownFail();

    }

    private ExtractorProgressToSuccess extractorProgressToSuccess;

    public void setProgressCallBack(ExtractorProgressToSuccess extractorProgressToSuccess) {

        this.extractorProgressToSuccess = extractorProgressToSuccess;

    }

    //取消下载
    public void cancelDownLoad() {
        extractorProgressToSuccess.onDownFail();
        cancel(true);
    }
}
