package com.naumankhaliq.imovies.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

// Matcher to find the nth child of a parent view
fun Matcher<View>.nthChildOf(childPosition: Int): Matcher<View> {
    val parentMatcher = this
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("with $childPosition child of type parentMatcher")
        }

        override fun matchesSafely(view: View): Boolean {
            val parent = view.parent
            return parent is RecyclerView && parentMatcher.matches(parent) &&
                    (parent as RecyclerView).getChildAdapterPosition(view) == childPosition
        }
    }
}

// Helper function to click on a child view (ImageView in this case) inside a RecyclerView item
fun clickChildViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(View::class.java)
        }

        override fun getDescription(): String {
            return "Click on a child view with specified ID."
        }

        override fun perform(uiController: androidx.test.espresso.UiController?, view: View) {
            val childView = view.findViewById<View>(id)
            childView?.performClick()
        }
    }
}
// Matcher to verify the ImageView's drawable
fun withImageViewDrawable(drawable: Drawable?): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("ImageView with drawable: $drawable")
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is ImageView) {
                return false
            }
            val imageView = view as ImageView
            val imgDrawable = imageView.drawable.toBitmapOrNull()
            return imgDrawable != null && imgDrawable.sameAs(drawable?.toBitmapOrNull())
        }
    }
}