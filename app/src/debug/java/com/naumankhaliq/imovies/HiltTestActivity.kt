/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naumankhaliq.imovies

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.naumankhaliq.imovies.databinding.ActivityHiltTestBinding
import com.naumankhaliq.imovies.databinding.ActivityMainBinding
import com.naumankhaliq.imovies.ui.base.BaseActivity
import com.naumankhaliq.imovies.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class HiltTestActivity : BaseActivity<MainViewModel, ActivityHiltTestBinding>() {

    fun setNavigationController(navCntrl: NavController) {
        navController = navCntrl
    }
    override val mViewModel: MainViewModel by viewModels()
    override fun getViewBinding(): ActivityHiltTestBinding = ActivityHiltTestBinding.inflate(layoutInflater)
}
