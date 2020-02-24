package com.shorty.demo;


import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }

    }
}
