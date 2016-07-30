package com.shorty.core.event;

import com.shorty.core.annotation.Subscribe;

/**
 * 事件监听基类
 * Created by yue.huang on 2016/6/1.
 */
public abstract class EventListener<T> {
    public String contextHash;

    public EventListener(){
    }

    public EventListener(String contextHash){
        this.contextHash = contextHash;
    }

    @Subscribe(threadLevel = Subscribe.DEFAULT, oneTime = true)
    public abstract void onEvent(T event);

    public abstract void onFailed(int code, String error);

    public void setContextHash(String contextHash){
        this.contextHash = contextHash;
    }
}
