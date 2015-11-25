package com.broadway.downfile.downfiledemo.activity;

import android.app.Application;

import org.xutils.x;

/**
 * Created by fengyanjun on 15/11/20.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
