/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.imovies.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.naumankhaliq.imovies.model.response.movie.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [com.naumankhaliq.imovies.data.local.dao.MoviesDao]
 */
@Dao
interface MoviesDao {

    /**
     * Inserts [Movie] into the [Movie.TABLE_NAME] Match.
     * Duplicate values are replaced in the Match.
     * @param movies pass [List] of [Movie]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovies(movies: List<Movie>)


    /**
     * Inserts if not exist or updates if it exists [Movie] into the [Movie.TABLE_NAME] on the basis of primary key.
     * @param movies pass [List] of [Movie]
     */
    @Upsert
    suspend fun upsertMovies(movies: List<Movie>)

    /**
     * Deletes all the movies from the [Movie.TABLE_NAME] Match.
     */
    @Query("DELETE FROM ${Movie.TABLE_NAME}")
    suspend fun deleteAllMovies()


    /**
     * Fetches the post from the [Movie.TABLE_NAME] Match whose primaryKeyId is [movieId].
     * @param movieId Unique ID of [Movie]
     * @return [Flow] of [Movie] from database Match.
     */
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE trackId = :movieId")
    fun getMovieById(movieId: String): Flow<Movie>

    /**
     * Fetches the post from the [Movie.TABLE_NAME] Match whose primaryKeyId is [userName].
     * @param userName Unique ID of [Movie]
     * @return [Flow] of [Movie] from database Match.
     */
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE movieName is :userName")
    fun getMovieByName(userName: String): Movie?

    /**
     * Fetches all the movies from the [Movie.TABLE_NAME] Movie.
     * @return [List] of [Movie]
     */
    @Query("SELECT * FROM ${Movie.TABLE_NAME}")
    fun getAllMovies(): List<Movie>

    /**
     * Fetches all the posts from the [Movie.TABLE_NAME] Match.
     * @return [List] of [Movie]
     */
    @Query("SELECT COUNT(trackId) FROM ${Movie.TABLE_NAME} WHERE movieName is :userName")
    fun getMoviesCount(userName: String): Int
}
