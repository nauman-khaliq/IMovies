/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */
package com.naumankhaliq.imovies.model.response.movie

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie.Companion.TABLE_NAME
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Entity(tableName = TABLE_NAME)
@JsonClass(generateAdapter = true)
@Parcelize
data class FavouriteMovie(
    @PrimaryKey(autoGenerate = false)
    var trackId: Long,
    val movie: Movie
): Parcelable {
    companion object {
        const val TABLE_NAME = "fav_movies"
    }
}




/**
 * [MovieJsonConverter] will be to convert movie to json and back for room database storage
 */
class MovieJsonConverter {
    /**
     * Converts movie of type [Movie] to json [String]
     * @param movie of type [Movie]
     * @return [String]
     */
    @TypeConverter
    fun fromMovie(movie: Movie?): String? {
        return Moshi.Builder().build().adapter(Movie::class.java).toJson(movie)
    }

    /**
     * Converts menuJson of type [String] to menu of type [Movie]
     * @param movieJson of type [String]
     * @return [Movie]
     */
    @TypeConverter
    fun toMatch(movieJson: String?): Movie? {
        return Moshi.Builder().build().adapter(Movie::class.java).fromJson(movieJson ?: "")
    }
}







