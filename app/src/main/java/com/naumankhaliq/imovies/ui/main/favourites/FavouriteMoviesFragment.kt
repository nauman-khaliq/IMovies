/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.ui.main.favourites

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.ui.adapters.GenericAdapter
import com.naumankhaliq.imovies.data.repository.DataFrom
import com.naumankhaliq.imovies.databinding.FragmentFavMoviesBinding
import com.naumankhaliq.imovies.databinding.ListItemMovieBinding
import com.naumankhaliq.imovies.model.State
import com.naumankhaliq.imovies.model.response.movie.Movie
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
open class FavouriteMoviesFragment : BaseFragment<MainViewModel, FragmentFavMoviesBinding>(), FavMovieListViewHolder.FavMovieActionListener {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    override val mViewModel: MainViewModel by activityViewModels()

    private var moviesListAdapter: GenericAdapter<Movie> = GenericAdapter(
        { view -> FavMovieListViewHolder(ListItemMovieBinding.bind(view), this) },
        object : GenericAdapter.ClickListener<Movie> {
            override fun onClicked(item: Movie) {
                onMovieItemClicked(item)
            }
        },
        R.layout.list_item_movie,
        null,
        object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.trackId == newItem.trackId
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
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

    /**
     * Gets movies through viewmodel from api or local storage
     */
    protected open fun getMovies() {
        mViewModel.getFavouriteMovies()
    }

    protected open fun onMovieItemClicked(item: Movie) {
        navigateTo(FavouriteMoviesFragmentDirections.moviesFragmentToMovieDetailsFragment(item))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        observeFavMovies()
        mViewModel._favMovies.value = State.idle()
        getMovies()
    }

    override fun getViewBinding(): FragmentFavMoviesBinding {
        return FragmentFavMoviesBinding.inflate(layoutInflater)
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
        }
    }


    /**
     * Observing users list State Flow data with states
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun observeFavMovies() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.favMovies.collect {
                    when (it) {
                        is State.Loading -> {
                            Log.i("LoadingStateChecking", "Users list Loading Called")
                            mViewBinding.swipeRefreshLayout.isRefreshing = true
                        }
                        is State.Success -> {
                            Log.i("LoadingStateChecking", "Users list Success Called")
                            it.data.let { movies ->
                                if (it.dataFrom == DataFrom.CACHED) {
                                    moviesListAdapter.items = if (movies.isNotEmpty()) ArrayList(movies) else arrayListOf()
                                    moviesListAdapter.notifyDataSetChanged()
                                    updateNoDataUI()
                                }
                            }
                            mViewBinding.swipeRefreshLayout.isRefreshing = false
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

    /**
     * Updates appropriate ui of data is empty or not
     */
    private fun updateNoDataUI() {
        if (moviesListAdapter.items.isEmpty()) {
            mViewBinding.noDataTV.visibility = View.VISIBLE
        }
        else {
            mViewBinding.noDataTV.visibility = View.GONE
        }
    }

    override fun onFavBtnClicked(item: Movie) {
        if (item.isFavourite) {
            mViewModel.addMovieToFavourites(item)
            mViewBinding.root.showSnackBar(getString(R.string.added_to_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
        else {
            mViewModel.removeMovieFromFavourites(item)
            moviesListAdapter.notifyItemRemoved(moviesListAdapter.items.indexOf(item))
            moviesListAdapter.items.remove(item)
            updateNoDataUI()
            mViewBinding.root.showSnackBar(getString(R.string.removed_from_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
    }
}