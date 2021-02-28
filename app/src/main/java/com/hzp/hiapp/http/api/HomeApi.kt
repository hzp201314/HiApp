package com.hzp.hiapp.http.api

import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.GET
import com.hzp.hiapp.model.TabCategory


interface HomeApi{
    @GET("category/categories")
    fun queryTabList():HiCall<List<TabCategory>>
}