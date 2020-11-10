package com.analytics.pgyjar.model;

import java.util.List;

/**
 * Created by liuqiang 2020-10-23 .
 */
public class PgySdkInfoModel {

    /**
     * type : dex
     * version : 0.0.0
     * versionCode : 000000
     * url : https://static.frontjs.com/dist/sdk/PgyerAnalytics/dex/readme
     * md5 : d41d8cd98f00b204e9800998ecf8427e
     */

    private String type;
    private String version;
    private String versionCode;
    private String url;
    private String md5;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}

