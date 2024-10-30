/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.utils

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HeadersInterceptor for headers which will be go with every api call
 */
@Singleton
class HeadersInterceptor @Inject constructor(private val preferenceHelper: PreferenceHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = preferenceHelper.getAccessToken()
        val originalRequest = chain.request()
        // set auth token and other headers on the request
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Connection", "keep-alive")
            .header("Authorization", "Bearer $accessToken")
        return chain.proceed(requestBuilder.build())
    }
}