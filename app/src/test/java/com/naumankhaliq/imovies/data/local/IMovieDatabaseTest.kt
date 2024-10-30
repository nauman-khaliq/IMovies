package com.naumankhaliq.imovies.data.local

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.naumankhaliq.imovies.data.local.dao.MoviesDao
import com.naumankhaliq.imovies.model.response.movie.Movie
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
@OptIn(ExperimentalCoroutinesApi::class)
class IMovieDatabaseTest: TestCase() {

    // get reference to the LanguageDatabase and LanguageDao class
    private lateinit var db: IMovieDatabase
    private lateinit var movieDao: MoviesDao

    // Override function setUp() and annotate it with @Before
    // this function will be called at first when this test class is called
    @Before
    public override fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
        db = Room.inMemoryDatabaseBuilder(context, IMovieDatabase::class.java).build()
        movieDao = db.getMoviesDao()
    }

    @After
    fun close() {
        db.close()
    }

    // create a test function and annotate it with @Test
    // here we are first adding an item to the db and then checking if that item
    // is present in the db -- if the item is present then our test cases pass
    @Test
    fun writeAndReadLanguage() = runBlocking (Dispatchers.IO){
        val movie = Movie(trackId = 123, movieName = "Test")
        movieDao.upsertMovies(listOf(movie))
        val movies = movieDao.getAllMovies()
        assert(movies.get(0).trackId == movie.trackId)
    }
}