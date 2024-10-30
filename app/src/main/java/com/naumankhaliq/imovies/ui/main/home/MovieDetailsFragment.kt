/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.ui.main.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.navigation.fragment.navArgs
import coil.load
import com.naumankhaliq.imovies.R
import com.naumankhaliq.imovies.databinding.FragmentMovieDetailsBinding
import com.naumankhaliq.imovies.model.response.movie.Movie
import com.naumankhaliq.imovies.ui.base.BaseFragment
import com.naumankhaliq.imovies.ui.main.MainViewModel
import com.naumankhaliq.imovies.utils.PreferenceHelper
import com.naumankhaliq.imovies.utils.extensions.gone
import com.naumankhaliq.imovies.utils.extensions.showSnackBar
import com.naumankhaliq.imovies.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


// Instances of this class are fragments representing a single
// object in our collection.

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MovieDetailsFragment : BaseFragment<MainViewModel, FragmentMovieDetailsBinding>() {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    override val mViewModel: MainViewModel by activityViewModels()

    lateinit var movie: Movie

    private val navArgs: MovieDetailsFragmentArgs by navArgs()

    private var player: ExoPlayer? = null
    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        movie = navArgs.movie
        movie.previewVideoUrl?.let { initPlayer(it) }
        initViews()
    }


    override fun getViewBinding(): FragmentMovieDetailsBinding {
        return FragmentMovieDetailsBinding.inflate(layoutInflater)
    }

    override fun onPause() {
        pausePlayer()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initViews() {
        mViewBinding.run {

            mViewBinding.movieTitle.text = movie.movieName
            mViewBinding.genreTV.text = movie.primaryGenre
            mViewBinding.priceTV.text = movie.price.toString() + " " + movie.currency
            mViewBinding.descTV.text = movie.longDescription
            val artwork = movie.artWorkUrl1100?.removeRange(((movie.artWorkUrl1100?.length ?: 0) - 13), movie.artWorkUrl1100?.length ?: 0)?.let {
                it + "300x300.webp"
            }
            mViewBinding.thumbnailMovie.load(artwork) {
                placeholder(R.color.black30)
                error(R.drawable.ic_broken_image)
                crossfade(true)
            }

            updateFavUI()
            favBtn.setOnClickListener {
                movie.makeMovieFavOrNot()
                if (movie.isFavourite) {
                    mViewModel.addMovieToFavourites(movie)
                    mViewBinding.root.showSnackBar(getString(R.string.added_to_favourites_mess))
                }
                else {
                    mViewModel.removeMovieFromFavourites(movie)
                    mViewBinding.root.showSnackBar(getString(R.string.removed_from_favourites_mess))
                }
                updateFavUI()
            }
            initData()

//            linkDetailsLabel.setOnClickListener {
//                lifecycleScope.launch(Dispatchers.IO) {
//
//                }
//            }
        }
    }

    /**
     * Updates Favourite btn ui
     */
    private fun updateFavUI() {
        if (movie.isFavourite) {
            mViewBinding.favBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_24))
        }
        else {
            mViewBinding.favBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_favorite_border_24))
        }
    }
    private fun initData() {
        mViewBinding.run {

        }

    }

    /**
     * Initiates media3 exoplayer set media source and prepares media player
     * @param mediaUrl pass url of the media
     */
    @OptIn(UnstableApi::class)
    private fun initPlayer(mediaUrl: String) {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .apply {
                val source = getProgressiveMediaSource(mediaUrl)
                setMediaSource(source)
                prepare()
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {

                            }
                            Player.STATE_ENDED -> {
                                player?.seekTo(1000)
                                player?.play()
                            }
                            Player.STATE_READY -> {
                                mViewBinding.playerView.player = player
                                player?.playWhenReady = true
                                mViewBinding.playIcon.gone()
                                mViewBinding.playerView.visible()
                            }
                        }
                    }
                })
            }
    }

    @OptIn(UnstableApi::class)
    private fun getProgressiveMediaSource(mediaUrl: String): MediaSource {
        // Create a Regular media source pointing to a playlist uri.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
    }

    private fun releasePlayer() {
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }

    private fun pausePlayer() {
        player?.pause()
    }

    companion object {

        fun watchYoutubeVideo(id: String, context: Context) {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=$id"))
            try {
                context.startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                context.startActivity(webIntent)
            }
        }
    }
}