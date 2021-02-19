package com.hzp.common.ui.component;

import android.app.Application;

import com.google.gson.Gson;
import com.hzp.hi.library.log.HiConsolePrinter;
import com.hzp.hi.library.log.HiFilePrinter;
import com.hzp.hi.library.log.HiLogConfig;
import com.hzp.hi.library.log.HiLogManager;

public class HiBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLog();
    }

    private void initLog() {
        HiLogManager.init(new HiLogConfig() {
            @Override
            public JsonParser injectJsonParser() {
                return (src)->new Gson().toJson(src);
            }

            @Override
            public boolean includeThread() {
                return true;
            }
        },new HiConsolePrinter(), HiFilePrinter.getInstance(getCacheDir().getAbsolutePath(),0));
    }
}
