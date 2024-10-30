package com.naumankhaliq.imovies.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar

/**
 * Makes view visible using fade animation
 */
fun View.visible() {
    this.animate()
        .alpha(1f)
        .setDuration(1000L)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.VISIBLE
            }
        })
}

/**
 * Makes view invisible using fade animation
 */
fun View.invisible() {
    this.animate()
        .alpha(0f)
        .setDuration(1000L)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.INVISIBLE
            }
        })
}

/**
 * Makes view visibility Gone using fade animation
 */
fun View.gone() {
    this.animate()
        .alpha(0f)
        .setDuration(1000L)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
        })
}

fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)
fun Fragment.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(requireContext(), colorRes)

/**
 * Easy toast function for Activity.
 */
fun FragmentActivity.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

/**
 * Inflate the layout specified by [layoutRes].
 */
//fun ViewGroup.inflate(layoutRes: Int): View {
//    return LayoutInflater.from(context).inflate(layoutRes, this, false)
//}

@Suppress("DEPRECATION")
fun Context.getDrawableCompat(@DrawableRes resId: Int, @ColorRes tintColorRes: Int = 0) = when {
    tintColorRes != 0 -> AppCompatResources.getDrawable(this, resId)?.apply {
        setColorFilter(getColorCompat(tintColorRes), PorterDuff.Mode.SRC_ATOP)
    }
    else -> AppCompatResources.getDrawable(this, resId)
}!!

/**
 * Easy function to show snack
 * Use root view of your layout to call this
 * @param message pass message of type [String] to show
 * @param length pass snack bar length default is [Snackbar.LENGTH_SHORT]
 */
fun View.showSnackBar(message: String, length: Int = Snackbar.LENGTH_SHORT, anchorView: View? = null) {
    val snackBar = Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_SHORT
    )
    anchorView?.let {
        snackBar.setAnchorView(it)
    }
    snackBar.show()
}