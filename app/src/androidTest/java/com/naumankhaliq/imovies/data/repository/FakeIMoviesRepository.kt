/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 *
 */

package com.naumankhaliq.imovies.data.repository

import com.naumankhaliq.imovies.data.local.dao.FavouriteMoviesDao
import com.naumankhaliq.imovies.data.local.dao.MoviesDao
import com.naumankhaliq.imovies.data.remote.api.IMoviesService
import com.naumankhaliq.imovies.model.response.BaseResponse
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.utils.MockResponseFileReader
import com.naumankhaliq.imovies.utils.TestRetrofitHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Response
import javax.inject.Inject

/**
 * Singleton repository for fetching data from remote and storing it in database
 * for offline capability. This is Single source of data.
 */
@ExperimentalCoroutinesApi
open class FakeIMoviesRepository @Inject constructor(
    private val moviesDao: MoviesDao,
    private val favouriteMoviesDao: FavouriteMoviesDao,
) : IMoviesRepository {

    var testApiService: IMoviesService
    private var mockWebServer: MockWebServer = MockWebServer()

    init {
        mockWebServer.start()
        testApiService = TestRetrofitHelper.getTestRetrofitServiceForMockServer<IMoviesService>(mockWebServer)
    }

    override fun getIMovies(): Flow<Resource<List<Movie>>> {
        return object: NetworkAndDbBoundRepository<List<Movie>, BaseResponse<List<Movie>>>() {
            override suspend fun saveRemoteData(response: BaseResponse<List<Movie>>) {
                val favMovies = favouriteMoviesDao.getAllFavouriteMovies()
                val favMovList = ArrayList<Movie>()
                favMovies.forEach {
                    favMovList.add(it.movie)
                }
                //moviesDao.upsertMovies(listOf(Movie(trackId = 1, movieName = "test1", shortDescription = "test1ShortDesc", longDescription = "test1LongDesc"), Movie(trackId = 2, movieName = "test2", shortDescription = "test2ShortDesc", longDescription = "test2LongDesc")))
                moviesDao.upsertMovies(response.results ?: listOf())
                moviesDao.upsertMovies(favMovList)
            }

            override suspend fun shouldFetchFromRemote(): Boolean {
                return true
            }

            override fun fetchFromLocal(): List<Movie> {
                return moviesDao.getAllMovies()
            }

            override suspend fun fetchFromRemote(): Response<BaseResponse<List<Movie>>> {
                val mockedResponse = MockResponseFileReader("imovie_response.json").content
                mockWebServer.enqueue(
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(mockedResponse)
                )
                return testApiService.getIMovies()
            }

        }.asFlow().onCompletion {
            mockWebServer.shutdown()
        }
    }

    override suspend fun updateIMovies(movies: List<Movie>) {
        moviesDao.upsertMovies(movies)
    }

    override suspend fun insertOrUpdateFavouriteMovie(favouriteMovie: FavouriteMovie) {
        favouriteMoviesDao.upsertFavouriteMovies(listOf(favouriteMovie))
    }

    override suspend fun deleteFavouriteMovie(favouriteMovie: FavouriteMovie) {
        favouriteMoviesDao.deleteFavouriteMovies(listOf(favouriteMovie))
    }

    override fun getFavouriteLocalIMovies(): Flow<Resource<List<FavouriteMovie>>> {
        return object : NetworkAndDbBoundRepository<List<FavouriteMovie>, List<FavouriteMovie>>() {
            override suspend fun saveRemoteData(response: List<FavouriteMovie>) {
            }

            override suspend fun shouldFetchFromRemote(): Boolean {
                return false
            }

            override fun fetchFromLocal(): List<FavouriteMovie> {
                return favouriteMoviesDao.getAllFavouriteMovies()
            }

            override suspend fun fetchFromRemote(): Response<List<FavouriteMovie>>? {
                return null
            }

        }.asFlow()
    }
}
