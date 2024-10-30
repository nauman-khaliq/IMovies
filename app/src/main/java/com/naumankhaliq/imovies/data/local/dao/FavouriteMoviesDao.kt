/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.imovies.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [com.naumankhaliq.imovies.data.local.dao.FavouriteMoviesDao]
 */
@Dao
interface FavouriteMoviesDao {

    /**
     * Inserts [FavouriteMovie] into the [FavouriteMovie.TABLE_NAME] Match.
     * Duplicate values are replaced in the Match.
     * @param movies pass [List] of [FavouriteMovie]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavouriteMovies(movies: List<FavouriteMovie>)


    /**
     * Inserts if not exist or updates if it exists [FavouriteMovie] into the [FavouriteMovie.TABLE_NAME] on the basis of primary key.
     * @param movies pass [List] of [FavouriteMovie]
     */
    @Upsert
    suspend fun upsertFavouriteMovies(movies: List<FavouriteMovie>)

    /**
     * Deletes all the movies from the [FavouriteMovie.TABLE_NAME] Match.
     */
    @Query("DELETE FROM ${FavouriteMovie.TABLE_NAME}")
    suspend fun deleteAllFavouriteMovies()


    /**
     * Fetches the post from the [FavouriteMovie.TABLE_NAME] whose primaryKeyId is [primaryKeyId].
     * @param primaryKeyId Unique ID of [FavouriteMovie]
     * @return [Flow] of [FavouriteMovie] from database.
     */
    @Query("SELECT * FROM ${FavouriteMovie.TABLE_NAME} WHERE trackId = :trackId")
    fun getFavouriteMovieById(trackId: String): Flow<FavouriteMovie>


    /**
     * Fetches all the movies from the [FavouriteMovie.TABLE_NAME] FavouriteMovie.
     * @return [List] of [FavouriteMovie]
     */
    @Query("SELECT * FROM ${FavouriteMovie.TABLE_NAME}")
    fun getAllFavouriteMovies(): List<FavouriteMovie>

    /**
     * Fetches all the posts from the [FavouriteMovie.TABLE_NAME] Match.
     * @return [List] of [FavouriteMovie]
     */
    @Query("SELECT COUNT(trackId) FROM ${FavouriteMovie.TABLE_NAME}")
    fun getFavouriteMoviesCount(): Int

    /**
     * Delete [FavouriteMovie] into the [FavouriteMovie.TABLE_NAME] .
     * @param movies pass [List] of [FavouriteMovie]
     */
    @Delete
    suspend fun deleteFavouriteMovies(movies: List<FavouriteMovie>)
}
