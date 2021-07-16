package com.geniecustomer.di

import com.geniecustomer.api.ApiCallInterface
import com.geniecustomer.api.Repository
import com.geniecustomer.api.Urls
import com.geniecustomer.utils.UnsafeOkHttpClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val retrofitModule: Module = module {
    single {
        okHttp()
    }
    single {
        provideGson()
    }
    single {
        retrofit(get(), get())
    }
    single {
        get<Retrofit>().create(ApiCallInterface::class.java)
    }
    single {
        Repository(get())
    }


}

private fun okHttp(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .build()
        chain.proceed(request)
    }.addInterceptor(loggingInterceptor).connectTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)

    return httpClient.build()
}

fun provideGson() = GsonBuilder().setLenient().create()

private fun retrofit(gson: Gson, okHttpClient: OkHttpClient) = Retrofit.Builder()
    .baseUrl(Urls.BASE_URL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    //.client(okHttpClient)
    .client(getUnsafeOkHttpClient())
    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()



fun getUnsafeOkHttpClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val builder = OkHttpClient.Builder().addInterceptor(interceptor)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .build()
            chain.proceed(newRequest)
        }.build()

    return builder
}
