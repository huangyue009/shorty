package com.shorty.core.http.action;

/**
 * Created by hy on 2014/10/24.
 */
public abstract class HttpActionListener<T> {
    public abstract void onSuccess(T result);

    public abstract void onFailure(int resultCode, String error);
}
