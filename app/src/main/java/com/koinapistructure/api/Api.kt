package com.koinapistructure.api

import com.example.theweekin.modelClass.homepage.news_model
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.response.newsfeed.newsfeed
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST

interface Api {

    @GET("homepage")
    suspend fun getProduct():Response<news_model>

    @POST("news_listing")
    suspend fun newsfeed(
        @Body request: Request
    ): Response<newsfeed>

}