package com.hzp.hiapp.http.api

import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.Filed
import com.hzp.hi.library.restful.annotation.GET
import com.hzp.hi.library.restful.annotation.Path
import com.hzp.hiapp.model.HomeModel
import com.hzp.hiapp.model.TabCategory


interface HomeApi{
    @GET("category/categories")
    fun queryTabList():HiCall<List<TabCategory>>

    @GET("home/{categoryId}")
    fun queryTabCategoryList(
        @Path("categoryId") categoryId: String,
        @Filed("pageIndex") pageIndex: Int,
        @Filed("pageSize") pageSize: Int
    ): HiCall<HomeModel>
}