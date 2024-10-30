/**
 * Created by frisovanwaveren on 09/08/2017.
 */
package com.naumankhaliq.imovies.ui.adapters

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naumankhaliq.imovies.utils.extensions.inflate

class GenericAdapter<T>(
    private val createViewHolder: (View) -> AbstractViewHolder<T>,
    private val clickListener: ClickListener<T>?,
    private val itemLayoutID: Int,
    private val touchListener: TouchListener<T>? = null,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, GenericAdapter.AbstractViewHolder<T>>(diffCallback) {
    var items: ArrayList<T> = arrayListOf()

    override fun onBindViewHolder(holder: AbstractViewHolder<T>, position: Int) {

        holder.setItem(
            items[position],
            clickListener,
            position == 0,
            position == items.size - 1,
            touchListener,
            items
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<T> {
        val viewHolder = createViewHolder(parent.inflate(itemLayoutID))
        touchListener?.let {
            viewHolder.itemView.setOnTouchListener(object: OnTouchListener{
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    p0?.performClick()
                    it.onItemTouched(viewHolder.itemView)
                    return false
                }
            })
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun setHasStableIds(hasStableIds: Boolean) {
        this.setHasStableIds(true)
    }

    abstract class AbstractViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        protected var clickListener: ClickListener<T>? = null
        protected var touchListener: TouchListener<T>? = null
        protected var isFirst: Boolean? = null
        protected var isLast: Boolean? = null
        protected var item: T? = null
        protected var items: List<T>? = null
        abstract fun bindItem(item: T)


        fun setItem(
            item: T,
            clickListener: ClickListener<T>?,
            isFirst: Boolean,
            isLast: Boolean,
            touchListener: TouchListener<T>? = null,
            items: List<T>
        ) {
            this.clickListener = clickListener
            this.touchListener = touchListener
            this.isFirst = isFirst
            this.isLast = isLast
            this.item = item
            this.items = items
            bindItem(item)
        }
    }

    interface ClickListener<T> {
        fun onClicked(item: T)
    }

    interface TouchListener<T> {
        fun onTouched(item: View)
        fun onItemTouched(itemView: View)
    }

    interface Getter {
        fun getDifferenceCriteria(): String
    }

}