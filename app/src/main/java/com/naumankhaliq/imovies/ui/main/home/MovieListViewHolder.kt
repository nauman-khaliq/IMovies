/*
 *   MIT Licence
 *   Copyright (c) 2024 Nauman Khaliq.
 *
 */
package com.naumankhaliq.imovies.ui.main.home

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.databinding.ListItemMovieBinding
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.ui.adapters.GenericAdapterSearchable

/**
 * View holder for Category Item button View
 */
class MovieListViewHolder(private val binding: ListItemMovieBinding, private val movieActionListener: MovieActionListener) :
    GenericAdapterSearchable.AbstractViewHolder<Movie>(binding.root) {

    override fun bindItem(item: Movie) {

        if (!whileSearchingShowHighlights(searchQuery ?: "", item)) {
            binding.nameTV.text = item.movieName
            binding.genreTV.text = item.primaryGenre
        }
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
            movieActionListener.onFavBtnClicked(item)
        }
        itemView.setOnClickListener {
            clickListener?.onClicked(item)
        }
    }

    private fun whileSearchingShowHighlights(searchQuery: String, item: Movie): Boolean {
        if (searchQuery.isNotEmpty()) {
            val startIndOfMovName = item.movieName?.indexOf(searchQuery, 0, true) ?: -1
            if (startIndOfMovName > -1) {
                val highlightedMovieName = getColoredSpannableString(
                    item.movieName ?: "",
                    startIndOfMovName,
                    (startIndOfMovName + searchQuery.length)
                )
                binding.nameTV.setText(highlightedMovieName, TextView.BufferType.SPANNABLE)
            } else {
                binding.nameTV.text = item.movieName
            }
            val startIndOfGenre = item.primaryGenre?.indexOf(searchQuery, 0, true) ?: -1
            if (startIndOfGenre > -1) {
                val highlightedGenre = getColoredSpannableString(
                    item.primaryGenre ?: "",
                    startIndOfGenre,
                    (startIndOfGenre + searchQuery.length)
                )
                binding.genreTV.setText(highlightedGenre, TextView.BufferType.SPANNABLE)
            } else {
                binding.genreTV.text = item.primaryGenre
            }
            return true
        }
        return false
    }

    /**
     * Highlights background color of range of string's chars
     * @param startInd start index of char to highlight
     * @param endInd end index of char to highlight
     * @return [SpannableString]
     */
    private fun getColoredSpannableString(
        string: String,
        startInd: Int,
        endInd: Int,
    ): SpannableString {
        val spannable = SpannableString(string)
        spannable.setSpan(
            BackgroundColorSpan(ContextCompat.getColor(itemView.context, R.color.colorAccent)),
            startInd,
            endInd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
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
    interface MovieActionListener {
        fun onFavBtnClicked(item: Movie)
    }
}