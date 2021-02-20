package com.hzp.hiapp;

import com.hzp.common.ui.component.HiBaseApplication;
import com.hzp.hi.library.util.ActivityManager;

public class HiApplication extends HiBaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        /*监听Activity生命周期*/
        ActivityManager.getInstance().init(this);
    }
}
