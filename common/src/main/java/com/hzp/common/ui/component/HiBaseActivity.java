package com.hzp.common.ui.component;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 基础Activity
 */
public class HiBaseActivity extends AppCompatActivity implements HiBaseActionInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showToast(String message) {
        if (TextUtils.isEmpty(message)) return;
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}