package com.hzp.hiapp.http.api

import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.GET
import com.hzp.hi.library.restful.annotation.Path
import com.hzp.hiapp.model.Subcategory
import com.hzp.hiapp.model.TabCategory


interface CategoryApi {
    @GET("category/categories")
    fun queryCategoryList(): HiCall<List<TabCategory>>


    @GET("category/subcategories/{categoryId}")
    fun querySubcategoryList(@Path("categoryId") categoryId: String): HiCall<List<Subcategory>>
}