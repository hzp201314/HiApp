package com.hzp.hiapp.demo.concurrent_demo.coroutine

import android.content.res.AssetManager
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 演示以异步的方式 读取 assets目录下的文件,并且适配协程的写法，让他真正的挂起函数
 *
 * 方便调用方 直接以同步的形式拿到返回值
 */
object CoroutineScene3 {
    suspend fun parseAssetsFile(assetManager: AssetManager, fileName: String): String {
       // suspendCoroutine<> {  }
        return suspendCancellableCoroutine { continuation ->
            Thread(Runnable {
                val inputStream = assetManager.open(fileName)
                val br = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                var stringBuilder = StringBuilder()

//               while ((line=br.readLine())!=null){
//
//               }

                do {
                    line = br.readLine()
                    if (line != null) stringBuilder.append(line) else break
                } while (true)

                inputStream.close()
                br.close()

                Thread.sleep(2000)

                Log.e("coroutine", "parseassetsfile completed")
                continuation.resumeWith(Result.success(stringBuilder.toString()))
            }).start()
        }
    }
}

//fun main(){
//    GlobalScope.launch {
//        val content = CoroutineScene3.parseAssetsFile(assets,"config.json")
//        Log.e("Coroutine",content)
//    }
//}