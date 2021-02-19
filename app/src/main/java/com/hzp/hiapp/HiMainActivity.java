package com.hzp.hiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hzp.hiapp.logic.MainActivityLogic;

public class HiMainActivity extends AppCompatActivity implements MainActivityLogic.ActivityProvider {
    private MainActivityLogic activityLogic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_main);
        activityLogic = new MainActivityLogic(this, savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        activityLogic.onSaveInstanceState(outState);
    }
}