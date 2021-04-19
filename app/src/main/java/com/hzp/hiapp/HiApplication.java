package com.hzp.hiapp;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hzp.common.ui.component.HiBaseApplication;
import com.hzp.hi.library.util.ActivityManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HiApplication extends HiBaseApplication {
//    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
//    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));


    @Override
    public void onCreate() {
        super.onCreate();

//        ExecutorService service = Executors.newFixedThreadPool(CORE_POOL_SIZE);
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                //Bugly
//                initBugly();
//            }
//        });
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                //初始化U盟统计
//                initUmeng();
//            }
//        });
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                //高德地图
//                initAMap();
//            }
//        });
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                //JPush
//                initJPush();
//            }
//        });
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                //Weex
//                initWeex();
//            }
//        });
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                //ARouter
//                initARouter();
//            }
//        });
        /*监听Activity生命周期*/
        ActivityManager.getInstance().init(this);

        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }

        ARouter.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
