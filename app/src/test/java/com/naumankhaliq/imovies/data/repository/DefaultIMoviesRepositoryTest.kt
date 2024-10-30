package com.naumankhaliq.imovies.data.repository

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.naumankhaliq.imovies.data.local.IMovieDatabase
import com.naumankhaliq.imovies.data.local.dao.FavouriteMoviesDao
import com.naumankhaliq.imovies.data.local.dao.MoviesDao
import com.naumankhaliq.imovies.data.remote.api.IMoviesService
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.utils.MockResponseFileReader
import com.naumankhaliq.imovies.utils.TestRetrofitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP], application = HiltTestApplication::class)
class DefaultIMoviesRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    private lateinit var repository: DefaultIMoviesRepository

    @MockK
    lateinit var testApiService: IMoviesService

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()
        MockKAnnotations.init()
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable

        mockWebServer = MockWebServer()
        mockWebServer.start()
        testApiService = TestRetrofitHelper.getTestRetrofitServiceForMockServer<IMoviesService>(mockWebServer)
        repository = DefaultIMoviesRepository(moviesDao, favouriteMoviesDao, testApiService)
    }

    @After
    fun tearDown() {
        imovieDB.close()
        mockWebServer.shutdown()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun tests_getIMovies_is_getting_and_storing_response_correctly_first_time() = runTest(UnconfinedTestDispatcher()) {
        val mockedResponse = MockResponseFileReader("imovie_response.json").content
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
            val res = repository.getIMovies().toList()

        val firstEmission = res[0]
        val secondEmission = res[1]
            when (firstEmission) {
                is Resource.Success -> {
                    assert(firstEmission.data.isEmpty())
                }

                is Resource.Failed -> {
                    assert(firstEmission.message.isEmpty())
                }
            }

        when (secondEmission) {
            is Resource.Success -> {
                assert(secondEmission.data.isNotEmpty())
            }

            is Resource.Failed -> {
                assert(secondEmission.message.isNotEmpty())
            }
        }
        var isMovieStoredToDB = false
        isMovieStoredToDB = moviesDao.getAllMovies().isNotEmpty()
        assert(isMovieStoredToDB)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun tests_getFavouriteLocalIMovies_is_getting_response_correctly_first_time() = runTest(UnconfinedTestDispatcher()) {

        favouriteMoviesDao.upsertFavouriteMovies(listOf(FavouriteMovie(1L, Movie(trackId = 1L))))
        val res = repository.getFavouriteLocalIMovies().toList()
        val firstEmission = res[0]
        when (firstEmission) {
            is Resource.Success -> {
                assert(firstEmission.data.get(0).trackId == 1L)
            }

            is Resource.Failed -> {
                assert(firstEmission.message.isEmpty())
            }
        }

        var isFavMovieStoredToDB = false
        isFavMovieStoredToDB = favouriteMoviesDao.getAllFavouriteMovies().isNotEmpty()
        assert(isFavMovieStoredToDB)
    }
}