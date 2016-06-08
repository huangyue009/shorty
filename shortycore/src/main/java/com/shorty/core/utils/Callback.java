package com.shorty.core.utils;

/**
 * 异步线程回调
 * 包括成功和失败两个事件
 *
 * @param <T>
 * @author huangyue  2013-10-21
 */
public abstract class Callback<T> {

    private String tag;

    public Callback() {
    }

    public Callback(String tag) {
        this.tag = tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    /**
     * 成功调用
     */
    protected abstract void onSucceed(T object);

    /**
     * 出错调用
     */
    protected abstract void onError(Object object);

    @SuppressWarnings("unchecked")
    public void notify(Object object, boolean isSuceecd) {
        if (isSuceecd) {
            onSucceed((T) object);
        } else {
            onError(object);
        }
    }
}