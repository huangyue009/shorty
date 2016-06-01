package com.shorty.core.event;

import android.content.Context;

import com.google.common.eventbus.EventBus;
import com.shorty.core.manager.BaseManager;

/**
 * event bus for listener
 * Created by yue.huang on 2016/5/31.
 */
public class EventManager extends BaseManager {
    private EventBus eventBus;
    @Override
    public void onCreate(Context context) {
        eventBus = new EventBus("EventManager");
    }

    @Override
    public void onDestroy() {
        eventBus = null;
    }

    public void addEventListener(String event, EventListener listener){}

}
