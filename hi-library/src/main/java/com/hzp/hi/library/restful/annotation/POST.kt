package com.hzp.hi.library.restful.annotation


/**
 * @POST("/cities/{province}")
 *fun test(@Path("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class POST(val value: String, val formPost: Boolean = true)