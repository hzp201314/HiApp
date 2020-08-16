package com.hzp.hi.app.demo.refresh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.hzp.hi.app.R
import com.hzp.hi.ui.refresh.HiRefresh
import com.hzp.hi.ui.refresh.HiRefreshLayout
import com.hzp.hi.ui.refresh.HiTextOverView

class HiRefreshDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_refresh_demo)
        val refreshLayout = findViewById<HiRefreshLayout>(R.id.refresh_layout)
        val xOverView= HiTextOverView(this)
        refreshLayout.setRefreshOverView(xOverView)
        refreshLayout.setRefreshListener(object : HiRefresh.HiRefreshListener {
            override fun enableRefresh(): Boolean {
                return true
            }

            override fun onRefresh() {
                //模拟刷新时间1s
                Handler().postDelayed({refreshLayout.refreshFinished()},1000)
            }
        })
    }
}