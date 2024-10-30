/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 * Copyright (c) 2020 Shreyas Patil
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.naumankhaliq.imovies.data.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * A repository which provides resource from local database as well as remote end point.
 *
 * [RESULT] represents the type for database.
 * [REQUEST] represents the type for network.
 */
@ExperimentalCoroutinesApi
abstract class NetworkAndDbBoundRepository<RESULT, REQUEST> {

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
