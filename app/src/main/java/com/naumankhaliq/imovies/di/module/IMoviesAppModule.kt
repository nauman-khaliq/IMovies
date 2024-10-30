/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 */

package com.naumankhaliq.imovies.di.module

import android.content.Context
import android.content.SharedPreferences
import com.naumankhaliq.imovies.BuildConfig
import com.naumankhaliq.imovies.utils.HeadersInterceptor
import com.naumankhaliq.imovies.utils.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * [IMoviesAppModule] which will provide app related dependencies
 */
@InstallIn(SingletonComponent::class)
@Module
class IMoviesAppModule {

    /**
     * Provides shared preferences dependency
     * @param context of type [Context]
     * @return [SharedPreferences]
     */
    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            "SharedPreferences",
            Context.MODE_PRIVATE
        )

    /**
     * Provides [PreferenceHelper] dependency
     * @param prefs of type [SharedPreferences]
     * @return [PreferenceHelper]
     */
    @Singleton
    @Provides
    fun providePreferencesHelper(prefs: SharedPreferences): PreferenceHelper =
        PreferenceHelper(prefs)

    /**
     * Provides [HttpLoggingInterceptor] dependency
     * @return [HttpLoggingInterceptor]
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }
        return interceptor
    }

    /**
     * Provides [HeadersInterceptor] dependency
     * @param preferenceHelper of type [PreferenceHelper]
     * @return [HeadersInterceptor]
     */
    @Provides
    @Singleton
    fun provideHeadersInterceptor(preferenceHelper: PreferenceHelper): HeadersInterceptor {
        return HeadersInterceptor(preferenceHelper)
    }

    /**
     * Provides [OkHttpClient] dependency
     * @param context of type [Context]
     * @param httpLoggingInterceptor of type [HttpLoggingInterceptor]
     * @param hostSelectionInterceptor of type [HostSelectionInterceptor]
     * @param headersInterceptor of type [HeadersInterceptor]
     * @return [OkHttpClient]
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: HeadersInterceptor
    ): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val mCache = Cache(context.cacheDir, cacheSize)
        return if (BuildConfig.DEBUG) {
            OkHttpClient().newBuilder()
                .cache(mCache)
                .retryOnConnectionFailure(true)
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .addInterceptor(headersInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
        } else {
            OkHttpClient().newBuilder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(headersInterceptor)
                .build()
        }
    }
}
