package com.naumankhaliq.imovies.ui.main

import android.os.Build
import com.naumankhaliq.imovies.data.local.IMovieDatabase
import com.naumankhaliq.imovies.data.local.dao.FavouriteMoviesDao
import com.naumankhaliq.imovies.data.local.dao.MoviesDao
import com.naumankhaliq.imovies.data.remote.api.IMoviesService
import com.naumankhaliq.imovies.data.repository.DataFrom
import com.naumankhaliq.imovies.data.repository.DefaultIMoviesRepository
import com.naumankhaliq.imovies.data.repository.Resource
import com.naumankhaliq.imovies.model.State
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.utils.PreferenceHelper
import com.naumankhaliq.imovies.utils.TestRetrofitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP], application = HiltTestApplication::class)
class MainViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mainViewModelT: MainViewModel

    @MockK
    lateinit var testApiService: IMoviesService

    @MockK
    lateinit var fakeRepository: FakeIMovieRepository

    @MockK
    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @MockK
    @Inject
    lateinit var imovieDB: IMovieDatabase

    @MockK
    @Inject
    lateinit var moviesDao: MoviesDao

    @MockK
    @Inject
    lateinit var favouriteMoviesDao: FavouriteMoviesDao

    private lateinit var mockWebServer: MockWebServer


    @Before
    fun setUp() {
        hiltRule.inject()
        MockKAnnotations.init()
        mockWebServer = MockWebServer()
        mockWebServer.start()
        testApiService =
            TestRetrofitHelper.getTestRetrofitServiceForMockServer<IMoviesService>(mockWebServer)
        fakeRepository = FakeIMovieRepository(moviesDao, favouriteMoviesDao, testApiService)
        mainViewModelT = MainViewModel(fakeRepository, preferenceHelper)
    }

    @After
    fun tearDown() {
        imovieDB.close()
        mockWebServer.close()
    }

    @Test
    fun test_state_flows_are_in_idle_state() {
        assert(mainViewModelT.movies.value is State.Idle)
        assert(mainViewModelT.favMovies.value is State.Idle)
    }

    @Test
    fun test_movies_state_flow_is_behaving_correctly() = runTest(UnconfinedTestDispatcher()) {
        mainViewModelT.getMovies()
        mainViewModelT.movies.value.let { state ->
            when (state) {
                is State.Loading -> {
                    assert(state.isLoading() == true)
                }
                else -> {}
            }
        }
        fakeRepository.emitMovieFlow(Resource.Success(listOf(Movie()), DataFrom.REMOTE))
        mainViewModelT.movies.value.let { state ->
            when (state) {
                is State.Success -> {
                    assert(state.data.size == 1)
                }
                else -> {}
            }
        }
    }

    @Test
    fun test_favmovies_state_flow_is_behaving_correctly() = runTest(UnconfinedTestDispatcher()) {
        mainViewModelT.getFavouriteMovies()
        mainViewModelT.favMovies.value.let { state ->
            when (state) {
                is State.Loading -> {
                    assert(state.isLoading() == true)
                }
                else -> {}
            }
        }
        fakeRepository.emitFavMovieFlow(Resource.Success(listOf(FavouriteMovie(1L, Movie(movieName = "test"))), DataFrom.REMOTE))
        mainViewModelT.favMovies.value.let { state ->
            when (state) {
                is State.Success -> {
                    assert(state.data.get(0).movieName == "test")
                }
                else -> {}
            }
        }
    }

    class FakeIMovieRepository(
        iMoviesDao: MoviesDao,
        favouriteMoviesDao: FavouriteMoviesDao,
        apiService: IMoviesService
    ) : DefaultIMoviesRepository(iMoviesDao, favouriteMoviesDao, apiService) {
        private val movieFlow = MutableSharedFlow<Resource<List<Movie>>>()
        private val favMovieFlow = MutableSharedFlow<Resource<List<FavouriteMovie>>>()
        suspend fun emitMovieFlow(value: Resource<List<Movie>>) = movieFlow.emit(value)
        suspend fun emitFavMovieFlow(value: Resource<List<FavouriteMovie>>) = favMovieFlow.emit(value)
        override fun getIMovies(): Flow<Resource<List<Movie>>> {
            return movieFlow
        }
        override fun getFavouriteLocalIMovies(): Flow<Resource<List<FavouriteMovie>>> {
            return favMovieFlow
        }
    }

}