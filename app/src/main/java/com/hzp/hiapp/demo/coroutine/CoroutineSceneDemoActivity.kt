package com.hzp.hiapp.demo.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hzp.hi.library.executor.HiExecutor
import com.hzp.hiapp.R

class CoroutineSceneDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_scene_demo)

        //启动协程
        findViewById<Button>(R.id.btn_scene_1).setOnClickListener { v ->
                CoroutineScene.startScene1()
        }
        findViewById<Button>(R.id.btn_scene_2).setOnClickListener { v ->
                CoroutineScene.startScene2()
        }
    }
}