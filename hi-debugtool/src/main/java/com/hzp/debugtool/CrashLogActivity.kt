package com.hzp.debugtool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hzp.common.ui.component.HiBaseActivity

class CrashLogActivity : HiBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_log)
    }
}