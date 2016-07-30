package com.shorty.core.http.base;

/**
 * 下载进度监听
 * Created by yue.huang on 16/6/20.
 */
public abstract class OnFileProgressListener {
    public int lastProgress;

    public abstract void onProgressChanged(int progress);
    public abstract void onSuccess(String localPath);
    public abstract void onError(int errorCode, String error);
}
