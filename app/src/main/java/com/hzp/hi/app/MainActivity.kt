package com.hzp.hi.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hzp.hi.app.demo.log.HiLogDemoActivity
import com.hzp.hi.app.demo.refresh.HiRefreshDemoActivity
import com.hzp.hi.app.demo.tab.HiTabBottomDemoActivity
import com.hzp.hi.app.demo.tab.HiTabTopDemoActivity
import com.hzp.hi.common.ui.component.HiBaseActivity

class MainActivity : HiBaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_hi_log ->{
                startActivity(Intent(this,
                    HiLogDemoActivity::class.java))
            }
            R.id.tv_tab_bottom ->{
                startActivity(Intent(this,
                    HiTabBottomDemoActivity::class.java))
            }
            R.id.tv_tab_top ->{
                startActivity(Intent(this,
                    HiTabTopDemoActivity::class.java))
            }
            R.id.tv_hi_refresh ->{
                startActivity(Intent(this,
                    HiRefreshDemoActivity::class.java))
            }
        }
    }
}