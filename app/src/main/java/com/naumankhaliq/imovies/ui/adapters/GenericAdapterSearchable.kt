package com.naumankhaliq.imovies.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.naumankhaliq.imovies.utils.extensions.inflate
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by naumankhaliq on 10/12/2019.
 */


class GenericAdapterSearchable<T : GenericAdapterSearchable.Searchable>(
    private val createViewHolder: (View) -> AbstractViewHolder<T>,
    private val clickListener: ClickListener<T>?,
    private val itemLayoutID: Int,
    private val touchListener: TouchListener<T>? = null,
    private val datasetChangeListener: DatasetChangeListener<T>
) : RecyclerView.Adapter<GenericAdapterSearchable.AbstractViewHolder<T>>(), Filterable {


    var items: ArrayList<T> = ArrayList()
    var originalList: ArrayList<T> = ArrayList()
    private var searchQuery: String? = null

    private var onNothingFound: (() -> Unit)? = null

    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                items.clear()
                if (constraint.isNullOrBlank()) {
                    items.addAll(originalList)
                } else {
                    val searchResults = originalList.filter {
                        it.getSearchCriteria().lowercase(Locale.ROOT).contains(constraint)
                    }
                    items = searchResults as ArrayList<T>
                }
                return filterResults.also {
                    it.values = items
                }

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (items.isNullOrEmpty())
                    onNothingFound?.invoke()

                datasetChangeListener.onDatasetChanged(constraint?.toString() ?: "")
                notifyDataSetChanged()
            }

        }


    }

    fun search(query: String?, onNothingFound: (() -> Unit)?) {
        this.onNothingFound = onNothingFound
        searchQuery = query
        filter.filter(query)
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<T>, position: Int) {
        holder.setItem(items[position], clickListener, position == 0, position == items.size - 1, touchListener, searchQuery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<T> = createViewHolder(parent.inflate(itemLayoutID))
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
        protected var searchQuery: String? = null
        abstract fun bindItem(item: T)


        fun setItem(item: T, clickListener: ClickListener<T>?, isFirst: Boolean, isLast: Boolean, touchListener: TouchListener<T>? = null, query: String?) {
            this.clickListener = clickListener
            this.touchListener = touchListener
            this.isFirst = isFirst
            this.isLast = isLast
            this.item = item
            this.searchQuery = query ?: ""
            bindItem(item)
        }
    }

    /*override fun getItemId(position: Int): Long {
       return items[position].hashCode().toLong()
   }*/


    interface ClickListener<T> {
        fun onClicked(item: T)
    }

    interface TouchListener<T> {
        fun onTouched(item: View)
    }

    interface Searchable {
        /*This method will allow to specify a search string from your model to compare against
        Search criteria could be anything depending on use case.*/
        fun getSearchCriteria(): String
    }

    interface DatasetChangeListener<T> {
        fun onDatasetChanged(queryString: String)
    }
}