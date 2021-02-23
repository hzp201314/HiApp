package com.hzp.hiapp.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.hzp.common.ui.view.EmptyView
import com.hzp.hiapp.R

/**
 * 全局统一错误页
 */
@Route(path = "/degrade/global/activity")
class DegradeGlobalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_degrade_global)

        val emptyView =findViewById<EmptyView>(R.id.empty_view)
        emptyView.setIcon(R.string.if_empty)
        emptyView.setTitle(getString(R.string.degrade_tips))
    }
}