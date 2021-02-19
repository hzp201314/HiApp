package com.hzp.hiapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hzp.hiapp.demo.log.HiLogDemoActivity
import com.hzp.hiapp.demo.tab.HiTabBottomDemoActivity
import com.hzp.hiapp.demo.tab.HiTabTopDemoActivity

class MainActivity : AppCompatActivity(),View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_hi_log->{
                startActivity(Intent(this,
                    HiLogDemoActivity::class.java))
            }
            R.id.tv_hi_tab_bottom->{
                startActivity(Intent(this,
                    HiTabBottomDemoActivity::class.java))
            }
            R.id.tv_hi_tab_top->{
                startActivity(Intent(this,
                    HiTabTopDemoActivity::class.java))
            }
        }
    }


}