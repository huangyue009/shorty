package com.shorty.core.event;

import com.shorty.core.annotation.Subscribe;

/**
 * 事件监听基类
 * Created by yue.huang on 2016/6/1.
 */
public abstract class EventListener<T> {
    @Subscribe(threadLevel = Subscribe.DEFAULT)
    public abstract void onEvent(T event);
}
