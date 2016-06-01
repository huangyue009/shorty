package com.shorty.core.http.base;

import com.shorty.core.http.action.HttpActionListener;
import com.shorty.core.http.action.ShortyHttpResponse;

import org.json.JSONException;

/**
 * Created by yue.huang on 2016/5/11.
 */
public abstract class BaseParse {
    public abstract void parse(ShortyHttpResponse response, HttpActionListener listener) throws JSONException;
}
