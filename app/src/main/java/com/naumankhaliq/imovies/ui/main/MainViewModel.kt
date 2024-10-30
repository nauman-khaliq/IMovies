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

package com.naumankhaliq.imovies.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naumankhaliq.imovies.data.repository.DataFrom
import com.naumankhaliq.imovies.data.repository.IMoviesRepository
import com.naumankhaliq.imovies.model.State
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.utils.DateUtils
import com.naumankhaliq.imovies.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for [MainActivity]
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val iMoviesRepository: IMoviesRepository,
    private val preferenceHelper: PreferenceHelper
) :
    ViewModel() {

    val _movies: MutableStateFlow<State<List<Movie>>> = MutableStateFlow(State.idle())
    val movies: StateFlow<State<List<Movie>>> = _movies

    val _favMovies: MutableStateFlow<State<List<Movie>>> = MutableStateFlow(State.idle())
    val favMovies: StateFlow<State<List<Movie>>> = _favMovies
    /**
     * Gets movies list using [IMoviesRepository] mapping on [State] and and passing data to _orders [StateFlow]
     */
    fun getMovies() {
        _movies.value = State.loading()
        viewModelScope.launch {
            iMoviesRepository.getIMovies()
                .map {
                    State.fromResource(it)
                }
                .collectLatest {
                    _movies.value = it
                }
        }
    }

    /**
     * Gets favourite movies list using [IMoviesRepository] mapping on [State] and and passing data to [StateFlow]
     */
    fun getFavouriteMovies() {
        _favMovies.value = State.loading()
        viewModelScope.launch {
            iMoviesRepository.getFavouriteLocalIMovies()
                .map {
                    State.fromResource(it)
                }
                .collectLatest {
                    _favMovies.value = generateMoviesListFromFav(it)
                }
        }
    }

    /**
     * Generates movies list from favourite movie model
     */
    private fun generateMoviesListFromFav(state: State<List<FavouriteMovie>>): State<List<Movie>> {
        when (state) {
            is State.Success -> {
                val arrayList = ArrayList<Movie>()
                state.data.forEach {
                    arrayList.add(it.movie)
                }
                return State.Success(arrayList.toList(), DataFrom.CACHED)
            }

            is State.Error -> {
                return State.Error(state.message)
            }

            else -> {

            }
        }
        return State.Error("Something went wrong")
    }

    /**
     * Add or update movie to favourites
     * @param movie pass [Movie]
     */
    fun addMovieToFavourites(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            movie.trackId?.let {
                iMoviesRepository.insertOrUpdateFavouriteMovie(FavouriteMovie(it, movie))
                iMoviesRepository.updateIMovies(listOf(movie))
            }
        }
    }

    /**
     * Delete movie from favourites
     * @param movie pass [Movie]
     */
    fun removeMovieFromFavourites(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            movie.trackId?.let {
                iMoviesRepository.deleteFavouriteMovie(FavouriteMovie(it, movie))
            }
        }
    }

    /**
     * Saves current time as Last visited time to shared preferences
     */
    fun saveCurrentTime() {
        preferenceHelper.saveLastVisitedTime(Calendar.getInstance().timeInMillis)
    }

    /**
     * Gets last saved time from storage and format it
     * if visiting first time the returns now
     * @return [String] last visited time
     */
    fun getLastVisitedTimeFormatted(): String {
        return if(preferenceHelper.getLastVisitedTime() != 0L) (DateUtils.formatDateTime(preferenceHelper.getLastVisitedTime()) ?: "") else "Now"
    }
}


