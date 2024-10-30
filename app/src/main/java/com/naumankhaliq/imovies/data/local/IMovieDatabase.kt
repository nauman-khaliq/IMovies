/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.imovies.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.naumankhaliq.imovies.data.local.dao.FavouriteMoviesDao
import com.naumankhaliq.imovies.data.local.dao.MoviesDao
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.model.response.movie.MovieJsonConverter

/**
 * Abstract I Movies database.
 * It provides DAO [MoviesDao] by using method [getMoviesDao].
 */
@Database(
    entities = [Movie::class, FavouriteMovie::class],
    version = DatabaseMigrations.DB_VERSION
)
@TypeConverters(MovieJsonConverter::class)
abstract class IMovieDatabase : RoomDatabase() {

    /**
     * @return [MoviesDao] User Data Access Object.
     */
    abstract fun getMoviesDao(): MoviesDao

    /**
     * @return [FavouriteMoviesDao] User Data Access Object.
     */
    abstract fun getFavouriteMoviesDao(): FavouriteMoviesDao

    companion object {
        const val DB_NAME = "imovies_database"

        @Volatile
        private var INSTANCE: IMovieDatabase? = null

        /**
         * @return database[IMovieDatabase] instance.
         */
        fun getInstance(context: Context): IMovieDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IMovieDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
