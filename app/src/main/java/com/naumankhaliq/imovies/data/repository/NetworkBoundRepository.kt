/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 */

package com.naumankhaliq.imovies.data.repository

import androidx.annotation.MainThread
import com.naumankhaliq.imovies.model.response.BaseResponse
import com.naumankhaliq.imovies.model.response.ErrorResponse
import com.naumankhaliq.imovies.model.response.ErrorResponse.Companion.parseErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * A repository which provides resource from local database as well as remote end point.
 *
 * [RESULT] represents the type for network.
 */
@ExperimentalCoroutinesApi
abstract class NetworkBoundRepository<RESULT> {

    fun asFlow() = flow<Resource<RESULT>> {

        // Fetch latest posts from remote
        val apiResponse = fetchFromRemote()

        // Parse body
        val remotePosts = apiResponse.body()

        // Check for response validation
        if (apiResponse.isSuccessful) {
            remotePosts?.results?.let {
                emit(Resource.Success(it, DataFrom.REMOTE))
            }
        } else {
            // Something went wrong! Emit Error state.
            val errObj: ErrorResponse? = apiResponse.parseErrorResponse()
            emit(Resource.Failed(errObj?.message ?: "Something went wrong"))

        }
    }.catch { ex ->
        ex.printStackTrace()
        emit(Resource.Failed("Something went wrong"))
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches [Response] from the remote end point.
     */
    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<BaseResponse<RESULT>>

}
