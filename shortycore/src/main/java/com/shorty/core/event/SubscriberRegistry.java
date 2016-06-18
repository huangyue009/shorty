package com.shorty.core.event;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.shorty.core.annotation.Subscribe;
import com.shorty.core.ui.BaseActivity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 事件容器，管理整个事件的队列和映射
 * Created by yue.huang on 2016/6/1.
 */
public class SubscriberRegistry {
    private final ConcurrentMap<String, CopyOnWriteArraySet<Subscriber>> subscribers =
            Maps.newConcurrentMap();

    private final ConcurrentMap<String, CopyOnWriteArraySet<Subscriber>> contextSubscribers =
            Maps.newConcurrentMap();

    void addEventListener(String event, EventListener listener) throws NoSuchMethodException {
        Subscriber subscriber = getSubscriber(listener);

        CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(event);

        if (eventSubscribers == null) {
            CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<Subscriber>();
            eventSubscribers = MoreObjects.firstNonNull(
                    subscribers.putIfAbsent(event, newSet), newSet);
        }

        eventSubscribers.add(subscriber);
        //context map
        eventSubscribers = contextSubscribers.get(event);

        if (eventSubscribers == null) {
            CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<Subscriber>();
            eventSubscribers = MoreObjects.firstNonNull(
                    contextSubscribers.putIfAbsent(event, newSet), newSet);
        }

        eventSubscribers.add(subscriber);
    }

    void removeEvent(String event){
        if(subscribers.containsKey(event)){
            subscribers.remove(event);
        }
    }

    void removeContext(String hash){
        if(contextSubscribers.containsKey(hash)){
            contextSubscribers.remove(hash);
        }
    }

    void removeListener(EventListener listener){
        for (Map.Entry<String, CopyOnWriteArraySet<Subscriber>> entry : subscribers.entrySet()) {
            for(Subscriber subscriber : entry.getValue()){
                if(listener.equals(subscriber.getListener())){
                    entry.getValue().remove(subscriber);
                }
            }
        }
    }

    Iterator<Subscriber> getSubscribers(String event){
        if(subscribers.containsKey(event)){
            return subscribers.get(event).iterator();
        }

        return null;
    }

    private Subscriber getSubscriber(EventListener listener) throws NoSuchMethodException {
        Method method = listener.getClass().getMethod("onEvent", Object.class);
        Annotation annotation = method.getAnnotation(Subscribe.class);
        Subscriber subscriber = null;
        if(annotation instanceof Subscribe){
            subscriber = new Subscriber(listener, ((Subscribe)annotation).threadLevel(), ((Subscribe)annotation).oneTime());
            if(TextUtils.isEmpty(((Subscribe)annotation).destroyWhenFinish())){
                subscriber.setContextHash(((Subscribe)annotation).destroyWhenFinish());
            }
        } else {
            subscriber = new Subscriber(listener, Subscribe.DEFAULT, true);
        }

        return subscriber;
    }

    void clean(){
        for (Map.Entry<String, CopyOnWriteArraySet<Subscriber>> entry : subscribers.entrySet()) {
            entry.getValue().clear();
        }
        subscribers.clear();

        for (Map.Entry<String, CopyOnWriteArraySet<Subscriber>> entry : contextSubscribers.entrySet()) {
            entry.getValue().clear();
        }
        contextSubscribers.clear();
    }
}
