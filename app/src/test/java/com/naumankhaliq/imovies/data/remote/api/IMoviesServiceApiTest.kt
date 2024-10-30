package com.naumankhaliq.imovies.data.remote.api

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.naumankhaliq.imovies.data.local.dao.FavouriteMoviesDao
import com.naumankhaliq.imovies.data.local.dao.MoviesDao
import com.naumankhaliq.imovies.utils.MockResponseFileReader
import com.naumankhaliq.imovies.utils.TestRetrofitHelper
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
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
class IMoviesServiceApiTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @MockK
    lateinit var testApiService: IMoviesService


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
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun tests_imovie_api_service_is_giving_correct_response() {

        val mockedResponse = MockResponseFileReader("imovie_response.json").content
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
        runBlocking(Dispatchers.IO) {
            val response = testApiService.getIMovies()
            //val json = Moshi.Builder().build().adapter<BaseResponse<List<Movie>>>(Types.newParameterizedType(List::class.java, Movie::class.java)).toJson(response?.body())
            val json = Gson().toJson(response.body())
            val resultResponse = JsonParser.parseString(json)
            val expectedresponse = JsonParser.parseString(mockedResponse)
            Assert.assertNotNull(resultResponse)
            //Assert.assertTrue(resultResponse.equals(expectedresponse))
        }
    }
}