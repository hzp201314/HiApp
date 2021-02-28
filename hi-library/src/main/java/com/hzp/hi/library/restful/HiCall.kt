package com.hzp.hi.library.restful

import java.io.IOException

interface HiCall<T> {

    @Throws(IOException::class)
    fun execute():HiResponse<T>

    fun enqueue(callback: HiCallback<T>)

    interface Factory{
        //生产HiCall对象
        fun newCall(request:HiRequest):HiCall<*>
    }
}