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

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.databinding.ActivityMainBinding
import com.naumankhaliq.imovies.ui.base.BaseActivity
import com.naumankhaliq.imovies.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.schedule


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val mViewModel: MainViewModel by viewModels()
    var ready = false

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar) // Set AppTheme before setting content view.
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

        Timer().schedule(1000) {
            ready = true
        }
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (ready) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )

//      It's very important to set the toolbar to prevent errors of the NPE type,
//      this is because the application style is .NoActionBar
//      setSupportActionBar(mViewBinding.toolbarLayout.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        //NavigationUI.setupActionBarWithNavController(this, navController)

        NavigationUI.setupWithNavController(mViewBinding.bottomNavigationView, navController)
        initView()
        navController.currentDestination?.let { shouldShowNavigationView(it) }

        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?,
            ) {
                shouldShowNavigationView(destination)
            }
        })

    }

    override fun onStart() {
        super.onStart()
        //handleNetworkChanges()
    }

    override fun navigateTo(direction: NavDirections, navExtras: FragmentNavigator.Extras?) {
        super.navigateTo(direction, navExtras)
        mViewBinding.bottomNavigationView.visibility = View.GONE
    }
    /**
     * Initializing views and register click listeners
     */
    private fun initView() {
        mViewBinding.run {

        }
    }

    /**
     * Decides whether to show bottom navigation view based on current destination
     */
    private fun shouldShowNavigationView(currentDestination: NavDestination) {
        if (currentDestination.id == R.id.favouriteMoviesFragment || currentDestination.id == R.id.moviesFragment) {
            mViewBinding.bottomNavigationView.visibility = View.VISIBLE
        }
        else {
            mViewBinding.bottomNavigationView.visibility = View.GONE
        }
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onStop() {
        mViewModel.saveCurrentTime()
        super.onStop()
    }

    /**
     * Returns bottom navigation view
     */
    fun getBottomNavView(): BottomNavigationView {
        return mViewBinding.bottomNavigationView
    }
    companion object {
        const val ANIMATION_DURATION = 1000L
        const val TAG = "ad_i"
        const val test = "Final\nMar 18 - 7:00 PM\nLahore"
    }
}
