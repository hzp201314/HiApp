package com.hzp.hiapp.demo.log

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hzp.hi.library.log.*
import com.hzp.hiapp.R

class HiLogDemoActivity : AppCompatActivity() {
    var viewPrinter: HiViewPrinter? = null
    var filePrinter: HiFilePrinter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_log_demo)
        viewPrinter = HiViewPrinter(this)
        filePrinter = HiFilePrinter.getInstance(null,0)
        findViewById<View>(R.id.btn_log).setOnClickListener { printLog() }
        viewPrinter!!.viewProvider.showFloatingView()
    }

    private fun printLog() {
        HiLogManager.getInstance().addPrinter(viewPrinter)
        HiLogManager.getInstance().addPrinter(filePrinter)
        //自定义Log配置
        HiLog.log(object : HiLogConfig() {
            override fun includeThread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 0;
            }
        }, HiLogType.E, "-----", "5566")
        HiLog.a("9900")
    }
}