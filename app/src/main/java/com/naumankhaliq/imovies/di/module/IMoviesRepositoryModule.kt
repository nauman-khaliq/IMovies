/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.imovies.di.module

import com.naumankhaliq.imovies.data.repository.IMoviesRepository
import com.naumankhaliq.imovies.data.repository.DefaultIMoviesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Currently HomeRepository is only used in ViewModels.
 * MainViewModel is not injected using @HiltViewModel so can't install in ViewModelComponent.
 */
@ExperimentalCoroutinesApi
@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class IMoviesRepositoryModule {

    /**
     * Binds DefaultIMoviesRepository returns [IMoviesRepository] which is an interface and parent of [DefaultIMoviesRepository]
     * @param repository of type [DefaultIMoviesRepository]
     * @return IMoviesRepository
     */
    @ActivityRetainedScoped
    @Binds
    abstract fun bindIMoviesRepository(repository: DefaultIMoviesRepository): IMoviesRepository
}
