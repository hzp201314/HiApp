package com.hzp.hi.library.restful

/**
 * callback 回调
 */
interface HiCallback<T> {
    fun onSuccess(response: HiResponse<T>)
    fun onFailed(throwable: Throwable)
}