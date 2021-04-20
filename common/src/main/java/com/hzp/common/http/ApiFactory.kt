package com.hzp.common.http

import com.hzp.common.utils.SPUtil
import com.hzp.hi.library.restful.HiRestful

object ApiFactory {
    val KEY_DEGRADE_HTTP = "degrade_http"
    val HTTPS_BASE_URL = "https://api.devio.org/as/"
    val HTTP_BASE_URL = "http://api.devio.org/as/"
    val degrade2Http = SPUtil.getBoolean(KEY_DEGRADE_HTTP)
    val baseUrl = if (degrade2Http) HTTP_BASE_URL else HTTPS_BASE_URL
    private val hiRestful: HiRestful = HiRestful(baseUrl, RetrofitCallFactory(baseUrl))

    init {
        //TODO 拦截器 未启用
//        hiRestful.addInterceptor(BizInterceptor())
//        hiRestful.addInterceptor(HttpCodeInterceptor())
//        hiRestful.addInterceptor(HiConfigInterceptor())

        SPUtil.putBoolean(KEY_DEGRADE_HTTP, false)
    }

    fun <T> create(service: Class<T>): T {
        return hiRestful.create(service)
    }
}