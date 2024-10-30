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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

interface IMoviesRepository {
    /**
     * Fetches movies list from itunes api
     * @return Flow<Resource<List<Movie>>>
     */
    fun getIMovies(): Flow<Resource<List<Movie>>>

    /**
     * Updates i movies list from itunes api
     * @param movies pass [List] of [Movie]
     */
    suspend fun updateIMovies(movies: List<Movie>)

    /**
     * Updates inserts or updates favourite movie
     * @param favouriteMovie pass which movie wants to update in room
     */
    suspend fun insertOrUpdateFavouriteMovie(favouriteMovie: FavouriteMovie)

    /**
     * Deletes Favourite movie
     * @param favouriteMovie pass which movie wants to delete in room
     */
    suspend fun deleteFavouriteMovie(favouriteMovie: FavouriteMovie)

    /**
     * Fetches movies list from local db
     * @return Flow<Resource<List<Movie>>>
     */
    fun getFavouriteLocalIMovies(): Flow<Resource<List<FavouriteMovie>>>
}

/**
 * Singleton repository for fetching data from remote and storing it in database
 * for offline capability. This is Single source of data.
 */
@ExperimentalCoroutinesApi
open class DefaultIMoviesRepository @Inject constructor(
    private val moviesDao: MoviesDao,
    private val favouriteMoviesDao: FavouriteMoviesDao,
    val iMoviesService: IMoviesService
) : IMoviesRepository {

    override fun getIMovies(): Flow<Resource<List<Movie>>> {
        return object: NetworkAndDbBoundRepository<List<Movie>, BaseResponse<List<Movie>>>() {
            override suspend fun saveRemoteData(response: BaseResponse<List<Movie>>) {
                val favMovies = favouriteMoviesDao.getAllFavouriteMovies()
                val favMovList = ArrayList<Movie>()
                favMovies.forEach {
                    favMovList.add(it.movie)
                }
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
                return iMoviesService.getIMovies()
            }

        }.asFlow()
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
