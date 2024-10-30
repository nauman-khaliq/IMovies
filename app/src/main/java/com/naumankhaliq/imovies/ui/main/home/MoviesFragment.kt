/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.ui.main.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.data.repository.DataFrom
import com.naumankhaliq.imovies.databinding.FragmentHomeBinding
import com.naumankhaliq.imovies.databinding.ListItemMovieBinding
import com.naumankhaliq.imovies.model.State
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.ui.adapters.GenericAdapterSearchable
import com.naumankhaliq.imovies.ui.base.BaseFragment
import com.naumankhaliq.imovies.ui.main.MainActivity
import com.naumankhaliq.imovies.ui.main.MainViewModel
import com.naumankhaliq.imovies.utils.PreferenceHelper
import com.naumankhaliq.imovies.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

// Instances of this class are fragments representing a single
// object in our collection.

@ExperimentalCoroutinesApi
@AndroidEntryPoint
open class MoviesFragment : BaseFragment<MainViewModel, FragmentHomeBinding>(), MovieListViewHolder.MovieActionListener, GenericAdapterSearchable.DatasetChangeListener<Movie> {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    override val mViewModel: MainViewModel by activityViewModels()

    private val moviesListAdapter: GenericAdapterSearchable<Movie> = GenericAdapterSearchable(
        { view -> MovieListViewHolder(ListItemMovieBinding.bind(view), this) },
        object : GenericAdapterSearchable.ClickListener<Movie> {
            override fun onClicked(item: Movie) {
                onMovieItemClicked(item)
            }
        },
        R.layout.list_item_movie,
        null,
        this
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mViewBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    /**
     * Gets movies through viewmodel from api or local storage
     */
    protected open fun getMovies() {
        mViewModel.getMovies()
    }
    fun getViewModel(): MainViewModel {
        return mViewModel
    }

    protected open fun onMovieItemClicked(item: Movie) {
        navigateTo(MoviesFragmentDirections.moviesFragmentToMovieDetailsFragment(item))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        observeMovies()
        mViewModel._movies.value = State.idle()
        getMovies()
    }

    override fun getViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initViews() {
        mViewBinding.run {
            if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                (mViewBinding.moviesList.layoutManager as? GridLayoutManager)?.apply {
                    spanCount = 2
                }
            }
            else {
                (mViewBinding.moviesList.layoutManager as? GridLayoutManager)?.apply {
                    spanCount = 4
                }
            }
            moviesList.setHasFixedSize(true)
            moviesList.adapter = moviesListAdapter
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                getMovies()
            }
            swipeRefreshLayout.isRefreshing = false
            searchEditText.doAfterTextChanged {
                moviesListAdapter.search(it.toString().lowercase()) {}
            }
        }
    }


    /**
     * Observing users list State Flow data with states
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun observeMovies() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.movies.collect {
                    when (it) {
                        is State.Loading -> {
                            Log.i("LoadingStateChecking", "Users list Loading Called")
                            mViewBinding.swipeRefreshLayout.isRefreshing = true
                        }
                        is State.Success -> {
                            Log.i("LoadingStateChecking", "Users list Success Called")
                            it.data.let { movies ->
                                moviesListAdapter.items = ArrayList(movies)
                                moviesListAdapter.originalList = ArrayList(movies)
                                moviesListAdapter.notifyDataSetChanged()
                                checkIfSearching()
                                if (it.dataFrom == DataFrom.REMOTE) {
                                    mViewBinding.swipeRefreshLayout.isRefreshing = false
                                }
                            }
                        }
                        is State.Error -> {
                            Log.i("LoadingStateChecking", "Users list ERROR Called")
                            val obj = it.message
                            Toast.makeText(context, obj, Toast.LENGTH_SHORT).show()
                            mViewBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun checkIfSearching() {
        mViewBinding.searchEditText.text.toString().let {
            if (it.isNotEmpty()) {
                moviesListAdapter.search(it.lowercase()) {}
            }
        }
    }

    override fun onFavBtnClicked(item: Movie) {
        if (item.isFavourite) {
            mViewModel.addMovieToFavourites(item)
            mViewBinding.root.showSnackBar(getString(R.string.added_to_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
        else {
            mViewModel.removeMovieFromFavourites(item)
            mViewBinding.root.showSnackBar(getString(R.string.removed_from_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
    }

    override fun onDatasetChanged(queryString: String) {
        if (moviesListAdapter.items.isEmpty() && queryString.isNotEmpty()) {
            mViewBinding.nothingFoundTV.text = "Nothing found for '${queryString}'"
            mViewBinding.nothingFoundTV.visibility = View.VISIBLE
        }
        else {
            mViewBinding.nothingFoundTV.visibility = View.GONE
        }
    }
}