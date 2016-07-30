package com.shorty.core.http.base;

import android.os.Handler;

import com.shorty.core.http.action.HttpActionListener;
import com.shorty.core.http.action.ShortyHttpResponse;

import org.json.JSONException;

/**
 * Created by yue.huang on 2016/5/11.
 */
public abstract class BaseParse {
    protected Handler handler;

    public abstract void parse(ShortyHttpResponse response, HttpActionListener listener) throws JSONException;

    public void setHandler(Handler handler){
        this.handler = handler;
    }
}
