package com.maxi.chatdemo.app;

import android.app.Application;

import com.maxi.chatdemo.db.base.BaseManager;


public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BaseManager.initOpenHelper(this);
    }

}
