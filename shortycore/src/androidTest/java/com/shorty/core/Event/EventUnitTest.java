package com.shorty.core.Event;

import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;

import com.shorty.core.AsyncInstrumentationTestCase;
import com.shorty.core.annotation.Subscribe;
import com.shorty.core.event.EventListener;
import com.shorty.core.event.EventManager;
import com.shorty.core.manager.ManagerFactory;

/**
 * Created by yue.huang on 2016/6/3.
 */
public class EventUnitTest extends AsyncInstrumentationTestCase {
    private EventManager eventManager;

    public void setUp(){
        eventManager = new EventManager();
        eventManager.onCreate(null);
    }

    public void testEventDefaultSubmit(){
        eventManager.addEventListener("test", new EventListener() {
            @Override
            @Subscribe(threadLevel = Subscribe.DEFAULT)
            public void onEvent(Object event) {
                System.out.println("testEventDefaultSubmit ok");
                assertTrue(event.toString(), event != null);
            }
        });

        eventManager.sendEvent("test", "xxxxx");
    }

    public synchronized void testEventMainSubmit() throws InterruptedException {
        final Handler handler = new Handler(Looper.myLooper());
        eventManager.addEventListener("test", new EventListener() {
            @Override
            @Subscribe(threadLevel = Subscribe.MAIN_THREAD)
            public void onEvent(Object event) {
                System.out.println("testEventMainSubmit ok");
                assertTrue(event.toString(), event != null);
                asyncNotify();
            }
        });

        eventManager.sendEvent("test", "xxxxx");
        asyncWait();
    }

    public synchronized void testEventBackgroudSubmit() throws InterruptedException {
        final Handler handler = new Handler(Looper.myLooper());
        eventManager.addEventListener("test", new EventListener() {
            @Override
            @Subscribe(threadLevel = Subscribe.BACKGROUND_THREAD)
            public void onEvent(Object event) {
                assertTrue(event.toString(), event != null);
            }
        });

        eventManager.sendEvent("test", "xxxxx");
    }

    public synchronized void testEventAsyncSubmit() throws InterruptedException {
        startAsync();
        final Handler handler = new Handler(Looper.myLooper());
        eventManager.addEventListener("test", new EventListener() {
            @Override
            @Subscribe(threadLevel = Subscribe.ASYNC_THREAD)
            public void onEvent(Object event) {
                assertTrue(event.toString(), event != null);
                asyncNotify();
                finishAsync();
            }
        });

        eventManager.sendEvent("test", "xxxxx");
        asyncWait();
    }
}
