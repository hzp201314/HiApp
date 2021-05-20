package com.hzp.hiapp.http

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.hzp.hi.library.restful.HiInterceptor
import com.hzp.hi.library.restful.HiResponse
import com.hzp.hiapp.route.HiRoute

/**
 * 根据response 的 code 自动路由到相关页面
 */
class HttpStatusInterceptor:HiInterceptor {
    override fun intercept(chain: HiInterceptor.Chain): Boolean {
        val response = chain.response()
        //不是request并且response不为空
        if(!chain.isRequestPeriod && response != null){
            val code = response.code
            when (code) {
                HiResponse.RC_NEED_LOGIN->{
                    HiRoute.startActivity(null,destination = HiRoute.Destination.ACCOUNT_LOGIN)
//                     ARouter.getInstance().build("/account/login").navigation()
                }
                //token过期
                //a | b
                HiResponse.RC_AUTH_TOKEN_EXPIRED, HiResponse.RC_AUTH_TOKEN_INVALID, HiResponse.RC_USER_FORBID -> {
                    var helpUrl: String? = null
                    if (response.errorData != null) {
                        helpUrl = response.errorData!!["helpUrl"]
                    }

                    val bundle = Bundle()
                    bundle.putString("degrade_title","非法访问")
                    bundle.putString("degrade_desc",response.msg)
                    bundle.putString("degrade_action",helpUrl)
                    HiRoute.startActivity(null,bundle,HiRoute.Destination.DEGRADE_GLOBAL)
//                    ARouter.getInstance().build("/degrade/global/activity")
//                        .withString("degrade_title", "非法访问")
//                        .withString("degrade_desc", response.msg)
//                        .withString("degrade_action", helpUrl)
//                        .navigation()
                }
            }
        }
        return false
    }
}