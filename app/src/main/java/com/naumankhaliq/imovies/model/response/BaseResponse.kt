/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 */
package com.naumankhaliq.imovies.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    val resultCount: Int,
    val results: T?
)