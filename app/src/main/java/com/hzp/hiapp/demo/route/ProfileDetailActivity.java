package com.hzp.hiapp.demo.route;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hzp.hiapp.R;
import com.hzp.hiapp.route.RouteFlag;

/**
 * 登录认证DEMO
 * 个人中心页
 */
@Route(path = "/profile/detail",extras = RouteFlag.FLAG_LOGIN)
public class ProfileDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
    }
}