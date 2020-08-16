package com.hzp.hi.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hzp.hi.app.logic.MainActivityLogic;

public class MainActivity2 extends AppCompatActivity implements MainActivityLogic.ActivityProvider {
    private MainActivityLogic activityLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        activityLogic=new MainActivityLogic(this);
    }
}