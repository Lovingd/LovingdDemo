package com.muzi.lovingd;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * MyApplication
 *
 * @author: 17040880
 * @time: 2017/9/18 19:59
 */
public class MyApplication extends MultiDexApplication {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
