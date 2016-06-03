package com.shorty.core.event;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.shorty.core.annotation.Subscribe;

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

    void addEventListener(String event, EventListener listener) throws NoSuchMethodException {
        Subscriber subscriber = getSubscriber(listener);

        CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(event);

        if (eventSubscribers == null) {
            CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<Subscriber>();
            eventSubscribers = MoreObjects.firstNonNull(
                    subscribers.putIfAbsent(event, newSet), newSet);
        }

        eventSubscribers.add(subscriber);
    }

    void removeEvent(String event){
        if(subscribers.containsKey(event)){
            subscribers.remove(event);
        }
    }

    void removeListener(EventListener listener){
        for (Map.Entry<String, CopyOnWriteArraySet<Subscriber>> entry : subscribers.entrySet()) {
            entry.getValue().remove(listener);
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
        if(annotation instanceof Subscribe){
            int threadType = ((Subscribe)annotation).threadLevel();
            return new Subscriber(listener, threadType);
        }

        return null;
    }

    void clean(){
        for (Map.Entry<String, CopyOnWriteArraySet<Subscriber>> entry : subscribers.entrySet()) {
            entry.getValue().clear();
        }
        subscribers.clear();
    }
}
