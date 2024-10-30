package com.naumankhaliq.imovies.ui.main.home

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naumankhaliq.imovies.HiltTestActivity
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.data.repository.IMoviesRepository
import com.naumankhaliq.imovies.utils.clickChildViewWithId
import com.naumankhaliq.imovies.utils.launchFragmentInHiltContainer
import com.naumankhaliq.imovies.utils.nthChildOf
import com.naumankhaliq.imovies.utils.withImageViewDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.allOf
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
class MoviesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var iMoviesRepository: IMoviesRepository

    val fragmentArgs = bundleOf("selectedListItem" to 0)
    lateinit var fragment: MoviesFragment

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<MoviesFragment>(fragmentArgs) {
            fragment = this as MoviesFragment
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.moviesFragment)
            Navigation.setViewNavController(requireView(), navController)
            (fragment.activity as? HiltTestActivity)?.setNavigationController(navController)
        }
    }

    @Test
    fun testMoviesFragment() {
        assert(::fragment.isInitialized)
    }

    @Test
    fun testsMoviesAreShownIfDataIsComing() {
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.logo)).check(matches(withText("I Movies")))
        // THEN - Verify task is displayed on screen
        onView(withText("A Star Is Born (2018)")).check(matches(isDisplayed()))
        onView(withText("Star Wars: The Force Awakens")).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchingExistingWordGivesCorrectResult() {
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.searchEditText)).check(matches(isEnabled()))
        onView(withId(R.id.searchEditText)).perform(replaceText("A Star is Born"))

        // THEN - Verify task is displayed on screen
        onView(withText("A Star Is Born (2018)")).check(matches(isDisplayed()))
        onView(withText("Star Wars: The Force Awakens")).check(doesNotExist())
    }

    @Test
    fun testSearchingNonExistingWorkGivesCorrectResult() {
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.searchEditText)).check(matches(isEnabled()))
        onView(withId(R.id.searchEditText)).perform(replaceText("Star Trek"))

        // THEN - Verify task is displayed on screen
        onView(withText("A Star Is Born (2018)")).check(doesNotExist())
        onView(withText("Star Wars: The Force Awakens")).check(doesNotExist())
        onView(withId(R.id.nothingFoundTV)).check(matches(isDisplayed()))

    }

    @Test
    fun clickingListItemShouldTakeUserToDetailsScreen() {
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.moviesList)).perform(RecyclerViewActions.actionOnItemAtPosition<MovieListViewHolder>(0, click()))
        // THEN - Verify that we navigate to the movie details screen
        assertEquals(navController.currentDestination?.id, R.id.movieDetailsFragment)
    }

    @Test
    fun clickingHeartBtnFromListShouldAddMovieToFavourites() {
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.moviesList)).perform(RecyclerViewActions.actionOnItemAtPosition<MovieListViewHolder>(0, clickChildViewWithId(R.id.favBtn)))

        // Check if the ImageView's color turns red after clicking
        onView(allOf(withId(R.id.favBtn), isDescendantOfA(withId(R.id.moviesList).nthChildOf(0))))
            .check(matches(withImageViewDrawable(ContextCompat.getDrawable(fragment.requireContext(),R.drawable.ic_favorite_24))))
    }




    @After
    fun tearDown() {
    }

}