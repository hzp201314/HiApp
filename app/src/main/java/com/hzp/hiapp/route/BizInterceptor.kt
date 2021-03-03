package com.hzp.hiapp.route

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.hzp.hi.library.util.MainHandler
import com.hzp.hiapp.biz.account.AccountManager
import java.lang.RuntimeException

/**
 * 业务的拦截器，判断目标页是否具备预先定义好的属性
 * @see RouteFlag
 */
@Interceptor(name = "biz_interceptor", priority = 9)
open class BizInterceptor : IInterceptor {
    private var context: Context? = null

    override fun init(context: Context?) {
        this.context = context
    }

    /**
     * note:该方法运行在ARouter的线程池中（子线程中）
     */
    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        val flag: Int = postcard!!.extra

        if ((flag and (RouteFlag.FLAG_LOGIN)) != 0) {
            //login
            callback!!.onInterrupt(RuntimeException("need login"))
            loginIntercept(postcard, callback)
        } else if ((flag and (RouteFlag.FLAG_AUTHENTICATION) != 0)) {
            callback!!.onInterrupt(RuntimeException("need authentication"))
            showToast("请先实名认证")
            //authentication()
        } else if ((flag and (RouteFlag.FLAG_VIP) != 0)) {
            callback!!.onInterrupt(RuntimeException("need become vip"))
            showToast("请先加入会员")
        } else {
            callback!!.onContinue(postcard)
        }
    }


    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        }
    }

    private fun loginIntercept(postcard: Postcard?,callback: InterceptorCallback?) {
//        Handler(Looper.getMainLooper()).post {
//            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
////            HiRoute.startActivity(null, destination = HiRoute.Destination.ACCOUNT_LOGIN)
//            ARouter.getInstance().build("/account/login").navigation()
//        }

        MainHandler.post(runnable = Runnable {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
            if(AccountManager.isLogin()){
                callback?.onContinue(postcard)
            }else{
                AccountManager.login(context, Observer { success->
                    callback?.onContinue(postcard)
                })
            }
        })
    }


}