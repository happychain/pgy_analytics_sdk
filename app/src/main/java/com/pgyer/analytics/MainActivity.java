package com.pgyer.analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    private Button textView;

    private  Handler handler = new Handler();
    private  Runnable run = new Runnable() {
        @Override
        public void run() {
            /**
             * 版本检查
             */
            Log.e("liuqiang-->","开始跳转");
            startActivity(new Intent(MainActivity.this, Main2Activity.class));
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("liuqiang-->","开始准备跳转");
        handler.postDelayed(run,5000);
//
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startVerification();
////                try {
////                    List<String> list = null;
////                    list.add("发送错误信息");
////
////                } catch (Exception e) {
////                    PgyCrashManager.reportCaughtException(e);
////                }
//
//            }
//        });
    }


//    private AlertDialog alertDialog;
//    private void startVerification(){
//        FingerprintManagerUtil.startFingerprinterVerification(this,
//                new FingerprintManagerUtil.FingerprintListenerAdapter() {
//
//                    @Override
//                    public void onAuthenticationStart() {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("指纹验证")
//                                .setMessage("指纹验证测试")
//                                .setCancelable(false)
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        FingerprintManagerUtil.cancel();
//                                    }
//                                });
//                        alertDialog = builder.create();
//                        alertDialog.show();
//                    }
//
//                    @Override
//                    public void onNonsupport() {
//                        Log.i("MainActivity", "onNonsupport");
//                        Toast.makeText(MainActivity.this, "不支持指纹验证", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onEnrollFailed() {
//                        Log.i("MainActivity", "onEnrollFailed");
//                        Toast.makeText(MainActivity.this, "没有录入指纹", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
//                        alertDialog.dismiss();
//                        Log.i("MainActivity", "onAuthenticationSucceeded result = [" + result + "]");
//                        Toast.makeText(MainActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAuthenticationFailed() {
//                        Log.i("MainActivity", "onAuthenticationFailed");
//                        Toast.makeText(MainActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
//                        Log.i("MainActivity", "onAuthenticationError errMsgId = [" + errMsgId + "], errString = [" + errString + "]");
//                        Toast.makeText(MainActivity.this, "提示:1 " + errString, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
//                        Log.i("MainActivity", "onAuthenticationHelp helpMsgId = [" + helpMsgId + "], helpString = [" + helpString + "]");
//                        Toast.makeText(MainActivity.this, "提示:2 " + helpString, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }


}