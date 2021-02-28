package com.hzp.hi.library.restful.annotation

/**
 * @BaseUrl("https://api.domin.org/as/")
 *fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl(val value: String)