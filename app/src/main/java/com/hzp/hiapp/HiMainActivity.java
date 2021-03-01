package com.hzp.hiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.BuildConfig;
import com.hzp.hi.library.util.HiStatusBar;
import com.hzp.hiapp.logic.MainActivityLogic;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class HiMainActivity extends AppCompatActivity implements MainActivityLogic.ActivityProvider {
    private MainActivityLogic activityLogic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_main);
        /*MainActivity辅助类，解决MainActivity代码过多问题*/
        activityLogic = new MainActivityLogic(this, savedInstanceState);

        HiStatusBar.INSTANCE.setStatusBar(this,true, Color.WHITE,false);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //音量下键点击事件
            if (BuildConfig.DEBUG) {
//                try {
//                    Class<?> aClass = Class.forName("com.hzp.hiapp.debug.DebugToolDialogFragment");
//                    DialogFragment target = (DialogFragment) aClass.getConstructor().newInstance();
//                    target.show(getSupportFragmentManager(), "debug_tool");
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        activityLogic.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }
}