/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */

package com.naumankhaliq.imovies.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.viewbinding.ViewBinding

/**
 * Abstract Activity which binds [ViewModel] [VM] and [ViewBinding] [VB]
 */
abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity() {

    protected abstract val mViewModel: VM
    protected lateinit var mViewBinding: VB
    protected lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewBinding()
    }

    /**
     * open function to navigate to a specific destination using nav controller
     * Override in child activity for specific functionality but do call super method
     * @param direction pass direction from direction classes
     */
    open fun navigateTo(direction: NavDirections, navExtras: FragmentNavigator.Extras? = null) {
        try {
            if (::navController.isInitialized) {
                if (navExtras != null)
                    navController.navigate(direction, navExtras)
                else {
                    navController.navigate(direction)
                }
            }
        } catch (ex: Exception) {
            Log.i("Navigate", ex.printStackTrace().toString())
        }
    }

    /**
     * open function to navigate to back
     * Override in child activity for specific functionality but do call super method
     */
    open fun navigateBack() {
        try {
            if (::navController.isInitialized)
                navController.navigateUp()
        } catch (ex: Exception) {
            Log.i("Navigate", ex.printStackTrace().toString())
        }
    }

    /**
     * It returns [VB] which is assigned to [mViewBinding] and used in [onCreate]
     */
    abstract fun getViewBinding(): VB
}
