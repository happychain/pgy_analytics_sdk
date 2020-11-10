package com.analytics.pgyjar;

public interface PACHttpRequestCallback {
    void onFinish(String response);

    void onError(Exception e);
}