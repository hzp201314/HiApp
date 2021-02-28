package com.hzp.hiapp.http.api

import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.Filed
import com.hzp.hi.library.restful.annotation.GET
import com.hzp.hi.library.restful.annotation.Path
import com.hzp.hiapp.model.GoodsList


interface GoodsApi {
    @GET("goods/goods/{categoryId}")
    fun queryCategoryGoodsList(
        @Path("categoryId") categoryId: String,
        @Filed("subcategoryId") subcategoryId: String,
        @Filed("pageSize") pageSize: Int,
        @Filed("pageIndex") pageIndex: Int
    ): HiCall<GoodsList>
}