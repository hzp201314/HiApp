package com.hzp.hiapp.demo.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.alibaba.android.arouter.launcher.ARouter
import com.hzp.hiapp.R

class ARouterDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_router_demo)

        findViewById<Button>(R.id.profile).setOnClickListener { v->
            navigation("/profile/detail")
        }
        findViewById<Button>(R.id.vip).setOnClickListener { v->
            navigation("/profile/vip")
        }
        findViewById<Button>(R.id.authentication).setOnClickListener { v->
            navigation("/profile/authentication")
        }
        findViewById<Button>(R.id.unknow).setOnClickListener { v->
            navigation("/profile/unknow")
        }

    }

    private fun navigation(path: String) {
        ARouter.getInstance().build(path).navigation()
    }
}