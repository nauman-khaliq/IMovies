@file:Suppress("NOTHING_TO_INLINE")

package com.naumankhaliq.imovies.utils.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by frisovanwaveren on 11/08/2017.
 */

//inflate views (used in adapters)
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun ViewGroup.asSequence(): Sequence<View> = object : Sequence<View> {

    override fun iterator(): Iterator<View> = object : Iterator<View> {
        private var nextValue: View? = null
        private var done = false
        private var position: Int = 0

        override public fun hasNext(): Boolean {
            if (nextValue == null && !done) {
                nextValue = getChildAt(position)
                position++
                if (nextValue == null) done = true
            }
            return nextValue != null
        }

        override fun next(): View {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val answer = nextValue
            nextValue = null
            return answer!!
        }
    }
}

/*
 * Copyright (C) 2017 Andrew Chen
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


/**
 * ref. https://www.youtube.com/watch?v=fPzxfeDJDzY
 */

inline operator fun ViewGroup.get(i: Int): View? = getChildAt(i)

/**
 * -=
 */
inline operator fun ViewGroup.minusAssign(child: View) = removeView(child)

/**
 * +=
 */
inline operator fun ViewGroup.plusAssign(child: View) = addView(child)

/**
 * if (viwe in views)
 */
inline fun ViewGroup.contains(child: View) = indexOfChild(child) != -1

inline fun ViewGroup.first(): View? = this[0]

inline fun ViewGroup.forEach(action: (View) -> Unit) {
    for (i in 0 until childCount) {
        action(getChildAt(i))
    }
}

inline fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
    for (i in 0 until childCount) {
        action(i, getChildAt(i))
    }
}

/**
 * for (view in views.children())
 */
inline fun ViewGroup.children() = object : Iterable<View> {
    override fun iterator() = object : Iterator<View> {
        var index = 0
        override fun hasNext() = index < childCount
        override fun next() = getChildAt(index++)
    }
}

val ViewGroup.views: List<View>
    get() = asSequence().toList()

val ViewGroup.viewsRecursive: List<View>
    get() = views.flatMap {
        when (it) {
            is ViewGroup -> it.viewsRecursive
            else -> listOf(it)
        }
    }

