package com.naumankhaliq.imovies.ui.main.favourites

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naumankhaliq.imovies.HiltTestActivity
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.data.repository.IMoviesRepository
import com.naumankhaliq.imovies.model.response.movie.FavouriteMovie
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.ui.main.home.MovieDetailsFragment
import com.naumankhaliq.imovies.ui.main.home.MovieListViewHolder
import com.naumankhaliq.imovies.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class FavouriteMoviesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var fragment: FavouriteMoviesFragment

    @Inject
    lateinit var iMoviesRepository: IMoviesRepository

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<FavouriteMoviesFragment> {
            fragment = this as FavouriteMoviesFragment
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.favouriteMoviesFragment)
            Navigation.setViewNavController(requireView(), navController)
            (fragment.activity as? HiltTestActivity)?.setNavigationController(navController)
            runBlocking {
                iMoviesRepository.insertOrUpdateFavouriteMovie(FavouriteMovie(1, Movie(1, movieName = "Fav Movie 1", primaryGenre = "test", shortDescription = "fav movie 1 test")))
                iMoviesRepository.insertOrUpdateFavouriteMovie(FavouriteMovie(2, Movie(2, movieName = "Fav Movie 2", primaryGenre = "test", shortDescription = "fav movie 2 test")))
            }
        }
    }

    @Test
    fun testFavouriteMoviesFragment() {
        assert(::fragment.isInitialized)
    }

    @Test
    fun testsFavouritesMoviesAreShown() {
        Thread.sleep(2000)
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.logo)).check(matches(withText("Favourites")))
        // THEN - Verify task is displayed on screen
        onView(withText("Fav Movie 1")).check(matches(isDisplayed()))
        onView(withText("Fav Movie 2")).check(matches(isDisplayed()))

    }

    @Test
    fun clickingListItemShouldTakeUserToDetailsScreen() {
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.moviesList)).perform(RecyclerViewActions.actionOnItemAtPosition<FavMovieListViewHolder>(0, click()))
        // THEN - Verify that we navigate to the movie details screen
        assertEquals(navController.currentDestination?.id, R.id.movieDetailsFragment)
    }



    @After
    fun tearDown() {
    }

}