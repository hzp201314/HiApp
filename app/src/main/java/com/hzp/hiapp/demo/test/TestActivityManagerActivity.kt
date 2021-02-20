package com.hzp.hiapp.demo.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hzp.hi.library.util.ActivityManager
import com.hzp.hiapp.R
import kotlinx.android.synthetic.main.activity_test_manager.*

class TestActivityManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_manager)
        val topActivity=ActivityManager.instance.getTopActivity(true)
        if(topActivity!=null){
            tv_context.text = topActivity.localClassName
        }
    }
}