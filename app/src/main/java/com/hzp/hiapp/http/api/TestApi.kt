package com.hzp.hiapp.http.api

import com.google.gson.JsonObject
import com.hzp.hi.library.restful.HiCall
import com.hzp.hi.library.restful.annotation.Filed
import com.hzp.hi.library.restful.annotation.GET


interface TestApi {
    @GET("cities")
    fun listCities(@Filed("name") name: String): HiCall<JsonObject>
}