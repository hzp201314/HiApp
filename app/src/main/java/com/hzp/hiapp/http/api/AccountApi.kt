package com.hzp.hiapp.http.api

import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.Filed
import com.hzp.hi.library.restful.annotation.GET
import com.hzp.hi.library.restful.annotation.POST
import com.hzp.hiapp.model.CourseNotice
import com.hzp.hiapp.model.UserProfile

interface AccountApi {

    @POST("user/login")
    fun login(
        @Filed("userName") userName: String,
        @Filed("password") password: String
    ): HiCall<String>


    @POST("user/registration")
    fun register(
        @Filed("userName") userName: String,
        @Filed("password") password: String,
        @Filed("imoocId") imoocId:
        String, @Filed("orderId") orderId: String
    ): HiCall<String>


    @GET("user/profile")
    fun profile(): HiCall<UserProfile>


    @GET("notice")
    fun notice(): HiCall<CourseNotice>
}