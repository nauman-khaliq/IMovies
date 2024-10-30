/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 */
package com.naumankhaliq.imovies.model.response.movie

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.naumankhaliq.imovies.model.response.movie.Movie.Companion.TABLE_NAME
import com.naumankhaliq.imovies.ui.adapters.GenericAdapterSearchable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Entity(tableName = TABLE_NAME)
@JsonClass(generateAdapter = true)
@Parcelize
data class Movie(
    @PrimaryKey(autoGenerate = false)
    @Json(name = "trackId") var trackId: Long? = null,
    @Json(name = "trackName") var movieName: String? = null,
    @Json(name = "artworkUrl30") var artWorkUrl130: String? = null,
    @Json(name = "artworkUrl60") var artWorkUrl160: String? = null,
    @Json(name = "artworkUrl100") var artWorkUrl1100: String? = null,
    @Json(name = "releaseDate") var releaseDate: String? = null,
    @Json(name = "trackPrice") var price: Float? = null,
    @Json(name = "currency") var currency: String? = null,
    @Json(name = "shortDescription") var shortDescription: String? = null,
    @Json(name = "longDescription") var longDescription: String? = null,
    @Json(name = "primaryGenreName") var primaryGenre: String? = null,
    @Json(name = "previewUrl") var previewVideoUrl: String? = null,
    var isFavourite: Boolean = false,
): Parcelable, GenericAdapterSearchable.Searchable {
    /**
     * Makes movie favourite other wise not
     */
    fun makeMovieFavOrNot() {
        isFavourite = !isFavourite
    }

    companion object {
        const val TABLE_NAME = "movies"
    }

    override fun getSearchCriteria(): String {
        return movieName + primaryGenre
    }
}







