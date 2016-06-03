package com.shorty.core.event;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.MoreExecutors;
import com.shorty.core.manager.BaseManager;
import com.shorty.core.utils.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

/**
 * event bus for listener
 * Created by yue.huang on 2016/5/31.
 */
public class EventManager extends BaseManager {
    private Executor executor;
    private SubscriberRegistry subscriberRegistry;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onCreate(Context context) {
        executor = MoreExecutors.directExecutor();
        subscriberRegistry = new SubscriberRegistry();
    }

    @Override
    public void onDestroy() {
        executor = null;
        mainHandler = null;
        subscriberRegistry.clean();
        subscriberRegistry = null;
    }

    /**
     * 增加事件监听
     * @param event
     * @param listener
     */
    public void addEventListener(String event, EventListener listener){
        try {
            subscriberRegistry.addEventListener(event, listener);
        } catch (NoSuchMethodException e) {
            Logger.e(e);
        }
    }

    public void removeEvent(String event){
        subscriberRegistry.removeEvent(event);
    }

    public void removeListener(EventListener listener){
        subscriberRegistry.removeListener(listener);
    }

    /**
     * 发送时间
     * @param event
     * @param eventObj
     */
    public void sendEvent(String event, Object eventObj){
        Iterator<Subscriber> itr = subscriberRegistry.getSubscribers(event);
        while (itr.hasNext()){
            itr.next().dispatchEvent(this, eventObj);
        }
    }

    public Executor getExecutor(){
        return executor;
    }

    public Handler getMainHandler(){
        return mainHandler;
    }
}
