package com.hzp.hiapp.http.api

import com.hzp.hi.library.restful.CacheStrategy
import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.Filed
import com.hzp.hi.library.restful.annotation.GET
import com.hzp.hi.library.restful.annotation.Path
import com.hzp.hiapp.model.HomeModel
import com.hzp.hiapp.model.TabCategory


interface HomeApi{
    @CacheStrategy(CacheStrategy.CACHE_FIRST)
    @GET("category/categories")
    fun queryTabList():HiCall<List<TabCategory>>

    @GET("home/{categoryId}")
    fun queryTabCategoryList(
        @CacheStrategy cacheStrategy: Int,
        @Path("categoryId") categoryId: String,
        @Filed("pageIndex") pageIndex: Int,
        @Filed("pageSize") pageSize: Int
    ): HiCall<HomeModel>
}