package com.analytics.pgyjar.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.pgyjar.util.ConvertUtil;

/**
 * Created by liuqiang 2020-10-30 .
 */

public class PgyerDialogBuilder extends AlertDialog.Builder {
    private TextView titleView;

    private TextView etDesc;
    private TextView version;


    private LinearLayout viewContent;
    private boolean isSimpleDialog;
    private int COLOR_INPUT_BG = Color.rgb(255, 255, 255);

    private static String mDescription = "";

    private static String COLOR_TITLE_BG = "#2E2D2D";
    private static String mCurrentVersion;


    private static String COLOR_HINT = "#cccccc";

    private Context context;

    public PgyerDialogBuilder(Context context) {

        this(context, 3);
    }

    public PgyerDialogBuilder(Context context, int themeResId) {
        super(context, themeResId);
        this.context = new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light);
    }

    public PgyerDialogBuilder setDescription(String description) {
        mDescription = description;
        return this;
    }

    public static void setCurrentVersion(String currentVersion) {
        mCurrentVersion = currentVersion;
    }

    public PgyerDialogBuilder setSimple(boolean isSimple) {
        this.isSimpleDialog = isSimple;
        return this;
    }


    public PgyerDialogBuilder setPgyerDialogTheme(int styleID) {
        this.context = new ContextThemeWrapper(context, styleID);
        return this;
    }

    @Override
    public PgyerDialogBuilder setTitle(CharSequence title) {
        return this;
    }

    @Override
    public PgyerDialogBuilder setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        return this;
    }

    @Override
    public PgyerDialogBuilder setCustomTitle(View customTitleView) {
        return this;
    }

    @Override
    public AlertDialog create() {
        setView(addContentView());
        AlertDialog alertDialog = super.create();
        return alertDialog;
    }

    @SuppressLint("ClickableViewAccessibility")
    private View addContentView() {
        viewContent = new LinearLayout(context);
        LinearLayout.LayoutParams lpDefault = getDefaultLayoutParams();
        viewContent.setOrientation(LinearLayout.VERTICAL);
        viewContent.setBackgroundColor(Color.WHITE);
        if (!isSimpleDialog) {
            titleView = (TextView) createTitltView("版本更新");
            titleView.setTextColor(Color.WHITE);
            viewContent.addView(titleView, lpDefault);
        }

        setPgyerContentView(viewContent);
        return viewContent;
    }

    private void setPgyerContentView(LinearLayout viewContent) {
        LinearLayout.LayoutParams lpDefault = getDefaultLayoutParams();
        viewContent.addView(createEmailView(), lpDefault);
        viewContent.addView(createLineView(), lpDefault);
        viewContent.addView(createDesView(), lpDefault);
    }


    //描述
    private View createDesView() {
        etDesc = new TextView(context);
        etDesc.setText(mDescription);
        etDesc.setPadding(ConvertUtil.dip2px(context, 20),
                ConvertUtil.dip2px(context, 10),
                ConvertUtil.dip2px(context, 20), 0);
        etDesc.setHintTextColor(Color.parseColor(COLOR_HINT));
        Configuration config = context.getResources().getConfiguration();
        if (config.orientation == 1)
            etDesc.setMinLines(8);
        else {
            etDesc.setMinLines(2);
        }
        etDesc.setTextSize(14);
        etDesc.setGravity(Gravity.LEFT | Gravity.TOP);
        etDesc.setBackgroundColor(COLOR_INPUT_BG);
        return etDesc;
    }


    private TextView createLineView() {
        TextView tvLine = new TextView(context);
        tvLine.setBackgroundColor(Color.parseColor("#f0f0f0"));
        tvLine.setHeight(ConvertUtil.dip2px(context, 1));
        return tvLine;
    }

    //版本号
    private View createEmailView() {
        version = new TextView(context);
        version.setText("最新版本号:" + mCurrentVersion);
        version.setSingleLine(true);
        version.setPadding(ConvertUtil.dip2px(context, 20),
                ConvertUtil.dip2px(context, 10),
                ConvertUtil.dip2px(context, 20),
                ConvertUtil.dip2px(context, 10));
        version.setHintTextColor(Color.parseColor(COLOR_HINT));
        version.setMinLines(1);
        version.setTextSize(14);
        version.setGravity(Gravity.LEFT | Gravity.CENTER);
        version.setBackgroundColor(COLOR_INPUT_BG);
        version.setFocusable(true);
        version.setFocusableInTouchMode(true);
        version.requestFocus();

        return version;
    }

    private LinearLayout.LayoutParams getDefaultLayoutParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private View createTitltView(CharSequence title) {
        titleView = new TextView(context);
        titleView.setText(title.toString());
        titleView.setTextSize(22);
//        titleView.setTextColor(Color.parseColor(COLOR_DIALOG_TITLE));
        titleView.setPadding(30, 20, 0, 20);
        titleView.setBackgroundColor(Color.parseColor(COLOR_TITLE_BG));
        titleView.setGravity(Gravity.CENTER);
        titleView.setSingleLine(true);
        return titleView;
    }
}
