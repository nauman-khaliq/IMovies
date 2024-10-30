/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.imovies.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.naumankhaliq.imovies.data.local.IMovieDatabase
import javax.inject.Singleton

/**
 * [IMoviesDatabaseModule] which will provide database related dependencies
 */
@InstallIn(SingletonComponent::class)
@Module
class IMoviesDatabaseModule {

    /**
     * Provides [IMovieDatabase] dependency
     * @param application of type [Application]
     * @return [IMovieDatabase]
     */
    @Singleton
    @Provides
    fun provideDatabase(application: Application) = IMovieDatabase.getInstance(application)

    /**
     * Provides [MoviesDao] dependency
     * @param database of type [IMovieDatabase]
     * @return [MoviesDao]
     */
    @Singleton
    @Provides
    fun provideMoviesDao(database: IMovieDatabase) = database.getMoviesDao()

    /**
     * Provides [FavouriteMoviesDao] dependency
     * @param database of type [IMovieDatabase]
     * @return [FavouriteMoviesDao]
     */
    @Singleton
    @Provides
    fun provideFavouriteMoviesDao(database: IMovieDatabase) = database.getFavouriteMoviesDao()

}
