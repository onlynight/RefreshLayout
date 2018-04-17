package com.github.onlynight.refreshlayout.demo;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;

public class BaseApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }

}
