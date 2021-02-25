package com.hzp.hiapp.demo.executor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.hzp.hi.library.executor.HiExecutor
import com.hzp.hi.library.executor.HiExecutor.Callable
import com.hzp.hiapp.R

class HiExecutorDemoActivity : AppCompatActivity() {
    private val TAG: String = "HiExecutorDemoActivity"
    var paused = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_executor_demo)

        //按任务优先级执行
        findViewById<Button>(R.id.btn_priority).setOnClickListener { v ->
            for (priority in 0 until 10) {
                var finalPriority = priority
                HiExecutor.execute(priority, Runnable {
                    try {
                        Thread.sleep(1000 - (finalPriority * 100).toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                })
            }
        }
        //暂停/恢复线程池所有任务
        findViewById<Button>(R.id.btn_paused_resume).setOnClickListener { v ->
            if (paused) {
                HiExecutor.resume()
            } else {
                HiExecutor.pause()
            }

            paused = !paused
        }
        //异步结果回调主线程
        findViewById<Button>(R.id.btn_async_back).setOnClickListener { v ->
            HiExecutor.execute(0,object : Callable<String>() {
                override fun onBackground(): String {
                    Log.e(
                        TAG,
                        "onBackground-当前线程是：" + Thread.currentThread().name
                    )
                    return "我是异步任务结果"
                }

                override fun onCompleted(result: String) {
                    Log.e(
                        TAG,
                        "onCompleted-当前线程是：" + Thread.currentThread().name
                    )
                    Log.e(TAG, "onCompleted-当前任务结果是：$result")
                }

            })
        }
    }
}