package com.shorty.noun.event;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * event bus for listener
 * Created by yue.huang on 2016/5/31.
 */
public class EventManager {
    private Executor executor;
    private SubscriberRegistry subscriberRegistry;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    public void onCreate(Context context) {
        executor = Executors.newSingleThreadExecutor();
        subscriberRegistry = new SubscriberRegistry();
    }

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
            e.printStackTrace();
//            Logger.e(e);
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
        if(itr != null) {
            while (itr.hasNext()) {
                itr.next().dispatchEvent(this, eventObj);
            }
        }
    }

    /**
     * 发送时间
     * @param event
     * @param errorCode
     * @param error
     */
    public void sendEventError(String event, int errorCode, String error){
        Iterator<Subscriber> itr = subscriberRegistry.getSubscribers(event);
        if(itr != null) {
            while (itr.hasNext()) {
                itr.next().dispatchError(this, errorCode, error);
            }
        }
    }

    public Executor getExecutor(){
        return executor;
    }

    public Handler getMainHandler(){
        return mainHandler;
    }
}
