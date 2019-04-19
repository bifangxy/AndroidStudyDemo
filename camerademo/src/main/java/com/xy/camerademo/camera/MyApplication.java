package com.xy.camerademo.camera;

import android.app.Application;
import android.content.Context;

import com.xy.common.utils.LogUtils;

/**
 * Created by xieying on 2019/3/28.
 * Description：
 */
public class MyApplication extends Application {

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        LogUtils.debugMode();

    }



    public static Context getContext() {
        return context;
    }
}
