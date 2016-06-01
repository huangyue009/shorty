package com.shorty.core.http.constant;

import com.shorty.core.http.action.HttpAction;

/**
 * enum include url ,request type, parse type
 * Created by yue.huang on 2016/5/11.
 */
public enum  HttpMethodType {
    SAMPLE("box/osad.do", HttpAction.POST); //获取设备列表
    /**
     * url link
     */
    public String URL;

    /**
     * set request request type
     * example HttpAction.POST HttpAction.GET
     */
    public int type;

    private HttpMethodType(String url, int type) {
        this.URL = "https://rgbox-api.roistar.net/" + url;
        this.type = type;
    }
}
