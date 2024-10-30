package com.naumankhaliq.imovies.ui.main.home

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naumankhaliq.imovies.HiltTestActivity
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class MovieDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val fragmentArgs = bundleOf("movie" to Movie(1, "A Star is Born", price = 39.9f, shortDescription = "Short", longDescription = "Long", currency = "AUD", primaryGenre = "Romance", artWorkUrl1100 = "https://is1-ssl.mzstatic.com/image/thumb/Video115/v4/a2/26/fd/a226fd77-c80b-5ee7-e40f-6a0222e1645d/pr_source.jpg/100x100bb.jpg", previewVideoUrl = "https://video-ssl.itunes.apple.com/itunes-assets/Video128/v4/6b/cd/60/6bcd60b0-73ce-1a9e-1bf8-d7bcc8d32c10/mzvf_2708740245690387686.640x356.h264lc.U.p.m4v"))
    lateinit var fragment: MovieDetailsFragment

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<MovieDetailsFragment>(fragmentArgs) {
            fragment = this as MovieDetailsFragment
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.movieDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
            (fragment.activity as? HiltTestActivity)?.setNavigationController(navController)
        }
    }

    @Test
    fun testMoviesFragment() {
        assert(::fragment.isInitialized)
    }

    @Test
    fun testsMovieDetailsAreShown() {
        Thread.sleep(2000)
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.logo)).check(doesNotExist())
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.descTV)).check(matches(isDisplayed()))
        onView(withId(R.id.movieTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.genreTV)).check(matches(isDisplayed()))
        onView(withId(R.id.priceTV)).check(matches(isDisplayed()))
        onView(withId(R.id.thumbnailMovie)).check(matches(isDisplayed()))
        onView(withId(R.id.playerView)).check(matches(isEnabled()))
    }



    @After
    fun tearDown() {
    }

}