package com.hzp.hiapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hzp.hi.library.log.HiLog
import com.hzp.hi.library.util.ActivityManager
import com.hzp.hiapp.demo.banner.HiBannerDemoActivity
import com.hzp.hiapp.demo.coroutine.CoroutineSceneDemoActivity
import com.hzp.hiapp.demo.executor.HiExecutorDemoActivity
import com.hzp.hiapp.demo.item.HiItemDataDemoActivity
import com.hzp.hiapp.demo.log.HiLogDemoActivity
import com.hzp.hiapp.demo.navigation.NavigationDemoActivity
import com.hzp.hiapp.demo.refresh.HiRefreshDemoActivity
import com.hzp.hiapp.demo.route.ARouterDemoActivity
import com.hzp.hiapp.demo.tab.HiTabBottomDemoActivity
import com.hzp.hiapp.demo.tab.HiTabTopDemoActivity
import com.hzp.hiapp.demo.test.TestActivityManagerActivity
class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityManager.instance.addFrontBackCallback(object : ActivityManager.FrontBackCallback {
            override fun onChange(front: Boolean) {
                Toast.makeText(applicationContext, "当前处于:$front", Toast.LENGTH_SHORT).show()
                HiLog.d("当前处于:$front")
            }
        })


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_hi_log -> {
                startActivity(
                    Intent(
                        this,
                        HiLogDemoActivity::class.java
                    )
                )
            }
            R.id.tv_hi_tab_bottom -> {
                startActivity(
                    Intent(
                        this,
                        HiTabBottomDemoActivity::class.java
                    )
                )
            }
            R.id.tv_hi_tab_top -> {
                startActivity(
                    Intent(
                        this,
                        HiTabTopDemoActivity::class.java
                    )
                )
            }
            R.id.tv_hi_refresh -> {
                startActivity(
                    Intent(
                        this,
                        HiRefreshDemoActivity::class.java
                    )
                )
            }
            R.id.tv_hi_banner -> {
                startActivity(
                    Intent(
                        this,
                        HiBannerDemoActivity::class.java
                    )
                )
            }
            R.id.tv_activity_manager -> {
                startActivity(
                    Intent(
                        this,
                        TestActivityManagerActivity::class.java
                    )
                )
            }
            R.id.tv_hi_data_item -> {
                startActivity(
                    Intent(
                        this,
                        HiItemDataDemoActivity::class.java
                    )
                )
            }
            R.id.tv_navigation -> {
                startActivity(
                    Intent(
                        this,
                        NavigationDemoActivity::class.java
                    )
                )
            }
            R.id.tv_arouter -> {
                startActivity(
                    Intent(
                        this,
                        ARouterDemoActivity::class.java
                    )
                )
            }
            R.id.tv_hi_executor -> {
                startActivity(
                    Intent(
                        this,
                        HiExecutorDemoActivity::class.java
                    )
                )
            }
            R.id.tv_coroutine -> {
                startActivity(
                    Intent(
                        this,
                        CoroutineSceneDemoActivity::class.java
                    )
                )
            }
        }
    }


}