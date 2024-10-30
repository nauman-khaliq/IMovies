/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.imovies.data.repository

import androidx.annotation.WorkerThread
import com.naumankhaliq.imovies.utils.MockResponseFileReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.mockwebserver.MockResponse
import retrofit2.Response

/**
 * A repository which provides resource from local database as well as remote end point.
 *
 * [RESULT] represents the type for database.
 * [REQUEST] represents the type for network.
 */
@ExperimentalCoroutinesApi
abstract class FakeNetworkAndDbBoundRepository<RESULT, REQUEST> {

    fun asFlow() = flow<Resource<RESULT>> {

        // Emit Database content first
        emit(Resource.Success(fetchFromLocal(), DataFrom.CACHED))

        if (shouldFetchFromRemote()) {

            // Fetch latest posts from remote
            val apiResponse = fetchFromRemote()

            // Parse body
            val remotePosts = apiResponse?.body()

            // Check for response validation
            if (apiResponse?.isSuccessful == true && remotePosts != null) {
                // Save posts into the persistence storage
                remotePosts.let {
                    saveRemoteData(it)
                }
            } else {
                // Something went wrong! Emit Error state.
                emit(Resource.Failed(apiResponse?.message() ?: ""))
            }

            // Retrieve posts from persistence storage and emit
            emit(
                Resource.Success<RESULT>(fetchFromLocal(), DataFrom.REMOTE)
            )
        }
    }.catch { e ->
        e.printStackTrace()
        emit(Resource.Failed("Something went wrong!"))
    }.flowOn(Dispatchers.IO)

    /**
     * Saves retrieved from remote into the persistence storage.
     */
    @WorkerThread
    protected abstract suspend fun saveRemoteData(response: REQUEST)

    /**
     * Saves retrieved from remote into the persistence storage.
     */
    @WorkerThread
    protected abstract suspend fun shouldFetchFromRemote(): Boolean

    /**
     * Retrieves all data from persistence storage.
     */
    @WorkerThread
    protected abstract fun fetchFromLocal(): RESULT

    /**
     * Fetches [Response] from the remote end point.
     */
    @WorkerThread
    protected abstract suspend fun fetchFromRemote(): Response<REQUEST>?
}
