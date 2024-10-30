/*
 *   MIT Licence
 *   Copyright (c) 2024 Nauman Khaliq.
 *
 */
package com.naumankhaliq.imovies.ui.main.favourites

import android.view.View
import androidx.core.content.ContextCompat
import coil.load
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.ui.adapters.GenericAdapter
import com.naumankhaliq.imovies.databinding.ListItemMovieBinding
import com.naumankhaliq.imovies.model.response.movie.Movie

/**
 * View holder for Category Item button View
 */
class FavMovieListViewHolder(private val binding: ListItemMovieBinding, private val favMovieActionListener: FavMovieActionListener) :
    GenericAdapter.AbstractViewHolder<Movie>(binding.root) {

    override fun bindItem(item: Movie) {
        binding.nameTV.text = item.movieName
        binding.genreTV.text = item.primaryGenre
        binding.priceTV.text = item.price.toString() + " " + item.currency
        binding.shortDesc.text = item.shortDescription
        val artwork = item.artWorkUrl1100?.removeRange(((item.artWorkUrl1100?.length ?: 0) - 13), item.artWorkUrl1100?.length ?: 0)?.let {
            it + "300x300.webp"
        }
        binding.movieImage.load(artwork) {
            placeholder(R.color.black30)
            error(R.drawable.ic_broken_image)
            crossfade(true)
        }
        updateFavUI()

        binding.favBtn.setOnClickListener {
            item.makeMovieFavOrNot()
            updateFavUI()
            favMovieActionListener.onFavBtnClicked(item)
        }
        itemView.setOnClickListener {
            clickListener?.onClicked(item)
        }
    }
    /**
     * Making button view selected or not selected
     * @param view of type [View] Button view which will be updated
     * @param isSelected of type [Boolean] whether Button is selected or not
     */
    private fun makeSelectedOrNot(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }

    /**
     * Updates favourite btn ui
     */
    private fun updateFavUI() {
        if (item?.isFavourite == true) {
            binding.favBtn.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.ic_favorite_24))
        }
        else {
            binding.favBtn.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.ic_favorite_border_24))
        }
    }

    /**
     * Interface for listener fav btn action listener
     */
    interface FavMovieActionListener {
        fun onFavBtnClicked(item: Movie)
    }
}