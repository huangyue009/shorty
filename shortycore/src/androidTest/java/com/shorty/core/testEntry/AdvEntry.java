package com.shorty.core.testEntry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by levin.lin on 2015-11-11.
 */
public class AdvEntry {
    /**
     * 是否可以点击
     */
    @Expose
    @SerializedName("clickurl")
    public String clickurl = "";
    /**
     * 广告结束时间
     */
    @Expose
    @SerializedName("endtime")
    public String endtime = "";

    /**
     * 广告播放的开始时间，如果为空全天播放（例如："06:00:00"）
     */
    @Expose
    @SerializedName("startime")
    public String startime = "";

    /**
     * 是否可以点击
     */
    @Expose
    @SerializedName("isclick")
    public String isclick = "";

    /**
     * "显示时间 单位：秒",
     */
    @Expose
    @SerializedName("dispalysecond")
    public String dispalysecond = "";

    /**
     *
     * 1.0-320*480,1.5-480*800,2.0-640*960,3.0-640*1136,4.0-720*1280,5.0-1080*1920",
     */
    @Expose
    @SerializedName("resolution")
    public String resolution = "";

    /**
     * 广告播放的结束时间，如果为空全天播放（例如"13:00:00"）
     */
    @Expose
    @SerializedName("imageurl")
    public String imageurl = "";
}
