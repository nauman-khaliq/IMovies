/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */

package com.naumankhaliq.imovies.di.module

import com.naumankhaliq.imovies.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.naumankhaliq.imovies.data.remote.api.IMoviesService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * [IMoviesApiModule] that will handle api related dependencies.
 * [IMoviesAppModule] is included in this module
 */
@InstallIn(SingletonComponent::class)
@Module(includes = [IMoviesAppModule::class])
class IMoviesApiModule {

    /**
     * Provides retrofit service with Moshi converter and okhttp client
     * @param okHttpClient of type [OkHttpClient]
     * @return [IMoviesService]
     */
    @Singleton
    @Provides
    fun provideRetrofitService(okHttpClient: OkHttpClient): IMoviesService = Retrofit.Builder()
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()
        .create(IMoviesService::class.java)
}
