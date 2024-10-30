/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 */
package com.naumankhaliq.imovies.data.repository

sealed class Resource<T> {
    data class Success<T>(val data: T, val dataFrom: DataFrom) : Resource<T>()
    data class Failed<T>(val message: String) : Resource<T>()
}

enum class DataFrom {
    CACHED,
    REMOTE
}