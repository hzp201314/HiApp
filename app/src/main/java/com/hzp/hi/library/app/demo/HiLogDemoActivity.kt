package com.hzp.hi.library.app.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hzp.hi.library.app.R
import com.hzp.hi.library.log.HiLog
import com.hzp.hi.library.log.HiLogConfig
import com.hzp.hi.library.log.HiLogType

class HiLogDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_log_demo)
        findViewById<View>(R.id.btn_log).setOnClickListener {
            printLog()
        }
    }
    private fun printLog(){
        //自定义Log配置
        HiLog.log(object : HiLogConfig(){
            override fun includeThread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 0
            }
        },HiLogType.E,"-----","5566")
        HiLog.a("9900")

    }
}