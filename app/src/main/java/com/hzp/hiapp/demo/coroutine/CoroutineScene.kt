package com.hzp.hiapp.demo.coroutine

import android.util.Log
import kotlinx.coroutines.*

/**
 * 协程demo
 */
object CoroutineScene {
    private const val TAG:String= "CoroutineScene"

    /**
     * 依次启动三个子线程。并且以同步方式拿到他们的返回值，进而更新UI
     */
    fun startScene1(){
        GlobalScope.launch (Dispatchers.Main){
            Log.e(TAG,"coroutine is running")
            val result1 = request1()
            val result2 = request2(result1)
            val result3 = request3(result2)

            updateUI(result3)
        }
        Log.e(TAG,"coroutine has launched")
    }

    /**
     * 启动一个线程。先执行request1，完了之后同时执行request2和request3，这两并发结束进而更新UI
     */
    fun startScene2(){
        GlobalScope.launch (Dispatchers.Main){
            Log.e(TAG,"coroutine is running")
            val result1 = request1()
            val deferred2 = GlobalScope.async {
                request2(result1)
            }
            val deferred3 = GlobalScope.async {
                request3(result1)
            }


            updateUI(deferred2.await(),deferred3.await())
        }
        Log.e(TAG,"coroutine has launched")
    }

    private fun updateUI(result2: String, result3: String) {
        Log.e(TAG,"update work on ${Thread.currentThread().name}")
        Log.e(TAG,"param:${result2}----$result3")
    }


    private fun updateUI(result3: String) {
        Log.e(TAG,"update work on ${Thread.currentThread().name}")
        Log.e(TAG,"param:${result3}")
    }

    //suspend关键字作用：挂起函数
    suspend fun request1():String{
        //异步任务 不会暂停线程，但是会暂停当前协程
        delay(2*1000)
        //Thread.sleep(2000)//让线程休眠
        Log.e(TAG,"request1 work on ${Thread.currentThread().name}")
        return "result from request1"
    }
    suspend fun request2(result1: String):String{
        delay(2*1000)
        Log.e(TAG,"request2 work on ${Thread.currentThread().name}")
        return "result from request2"
    }
    suspend fun request3(result2: String):String{
        delay(2*1000)
        Log.e(TAG,"request3 work on ${Thread.currentThread().name}")
        return "result from request3"
    }
}