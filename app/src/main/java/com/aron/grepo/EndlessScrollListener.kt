package com.aron.grepo

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
class EndlessScrollListener (
        private val layoutManager: LinearLayoutManager,
        private val loadMoreListener: LoadMoreListener,
        val loading: AtomicBoolean = AtomicBoolean(false)
) : RecyclerView.OnScrollListener() {

    private companion object {
        const val ITEM_THRESHOLD = 3
    }

    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        totalItemCount = layoutManager.itemCount
        lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        if (totalItemCount <= (lastVisibleItem + ITEM_THRESHOLD) && !loading.getAndSet(true)) {
            loadMoreListener.onLoadMore()
        }
    }

    interface LoadMoreListener {
        fun onLoadMore()
    }
}
