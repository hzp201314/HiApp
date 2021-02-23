package com.hzp.hiapp.demo.route;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hzp.hiapp.R;
import com.hzp.hiapp.route.RouteFlag;

/**
 * VIP认证DEMO
 */
@Route(path = "/profile/vip",extras = RouteFlag.FLAG_VIP)
public class VipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
    }
}