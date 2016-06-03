package com.shorty.core;

import android.test.InstrumentationTestCase;

/**
 * Created by yue.huang on 2016/6/3.
 */
public class AsyncInstrumentationTestCase extends InstrumentationTestCase {
    private boolean waiting;
    private boolean isFinish;

    public void startAsync(){
        isFinish = false;
        waiting = false;
    }

    public void finishAsync(){
        isFinish = true;
        waiting = false;
    }

    public void asyncWait() throws InterruptedException {
        if(isFinish){
            return;
        }
        waiting = true;
        while (waiting){
            Thread.sleep(500);
        }
    }

    public void asyncNotify(){
        waiting = false;
    }

}
