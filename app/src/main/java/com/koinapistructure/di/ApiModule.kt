package com.koinapistructure.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.koinapistructure.api.Api
import com.koinapistructure.utils.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

const val baseURL=Constant.baseURL

fun provideGson():Gson {

    return GsonBuilder().setLenient().create()
}
fun provideOkHTTPClient():OkHttpClient{
    val  loggingInterceptorp=HttpLoggingInterceptor()
    loggingInterceptorp.setLevel(HttpLoggingInterceptor.Level.HEADERS)
    loggingInterceptorp.setLevel(HttpLoggingInterceptor.Level.BODY)

    val requestInterceptor=Interceptor{
        val url=it.request()
            .url
            .newBuilder()
            .build()

        val request=it.request()
            .newBuilder()
            .url(url)
            .build()

        return@Interceptor it.proceed(request)
    }

    return OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptorp)
        .addNetworkInterceptor(requestInterceptor)
        .build()
}


fun provideRetrofit( baseURL :String,gson: Gson,client: OkHttpClient):Api{
    return Retrofit.Builder()
        .baseUrl(baseURL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(Api::class.java)
}

val ApiModule= module {
    single { baseURL }
    single { provideGson() }
    single { provideOkHTTPClient() }
    single { provideRetrofit(get(),get(),get()) }
}