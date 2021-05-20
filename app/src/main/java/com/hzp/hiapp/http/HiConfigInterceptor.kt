package com.hzp.hiapp.http

import com.google.gson.Gson
import com.hzp.hi.library.restful.HiInterceptor


class HiConfigInterceptor : HiInterceptor {
    override fun intercept(chain: HiInterceptor.Chain): Boolean {
        val request = chain.request()
        val response = chain.response()
//        if (chain.isRequestPeriod) {
//            val params = mapOf(
//                "version" to (HiConfig.instance.getVersion() ?: "")
//                , "namespace" to "haowu"
//            )
//            request.addHeader("hi-config", Gson().toJson(params))
//        } else if (response != null) {
//            response.rawData?.let {
//                HiConfig.instance.feed(it)
//            }
//        }
        return false
    }
}