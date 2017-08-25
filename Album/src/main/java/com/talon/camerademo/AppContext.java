package com.talon.camerademo;

import android.app.Application;
import android.util.DisplayMetrics;

/**
 * Created by Talon on 2017/8/25.
 */

public class AppContext extends Application {

    private int screenWidth, screenHeight;

    private static AppContext instance;

    public static AppContext getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initScreenSize();
    }

    private void initScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
