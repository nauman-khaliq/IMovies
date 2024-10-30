/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 * 
 */
package com.naumankhaliq.imovies.model.response

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Response

@JsonClass(generateAdapter = true)
class ErrorResponse {
    var status: String? = null
    var responseCode: Int? = null
    var message: String? = null
    val isStatusSuccess: Boolean get() = responseCode == 200 || responseCode == 201
    val isUnauthorized: Boolean get() = responseCode == 401

    companion object {
        suspend fun<T> Response<T>.parseErrorResponse(): ErrorResponse? {
            this.let { apiResponse ->
                val adapter: JsonAdapter<ErrorResponse> = Moshi.Builder().build().adapter(
                    ErrorResponse::class.java)
                var errObj: ErrorResponse? = null
                val on = CoroutineScope(Dispatchers.IO).async {
                    runCatching {
                        errObj = adapter.fromJson(apiResponse.errorBody()?.string() ?: "")
                    }
                }
                on.await()
                return errObj
            }
        }
    }
}

