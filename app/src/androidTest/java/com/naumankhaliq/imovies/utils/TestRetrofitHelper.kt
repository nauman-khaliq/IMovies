package com.naumankhaliq.imovies.utils

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object TestRetrofitHelper {
    inline fun<reified T> getTestRetrofitServiceForMockServer(server: MockWebServer): T {
        val BASE_URL = server.url("/").toString()
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val service  = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build().create(T::class.java)

        return service
    }
}