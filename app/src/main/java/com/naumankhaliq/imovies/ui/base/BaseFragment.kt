/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : Fragment() {

    protected abstract val mViewModel: VM
    protected lateinit var mViewBinding: VB

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
        (activity as? BaseActivity<*,*>)?.navigateTo(direction, navExtras)
    }

    /**
     * open function to navigate to back
     * Override in child activity for specific functionality but do call super method
     */
    open fun navigateBack() {
        (activity as? BaseActivity<*,*>)?.navigateBack()
    }

    abstract fun getViewBinding(): VB
}