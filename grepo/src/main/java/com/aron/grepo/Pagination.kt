package com.aron.grepo

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
internal object DatabasePagination {
    private const val ITEMS_IN_BATCH = Configuration.ITEMS_PER_PAGE - 1
    private var firstIndex: Int = 0
    private var lastIndex: Int = firstIndex + ITEMS_IN_BATCH

    /**
     * Return current batch of items with first pair item as first index and
     * second's pair of item as last index
     */
    fun currentBatch(): Pair<Int, Int> = Pair(firstIndex, lastIndex)

    fun nextBatch() {
        firstIndex = lastIndex + 1
        lastIndex = firstIndex + ITEMS_IN_BATCH
    }

    fun resetNavigation() {
        lastIndex = 0
        lastIndex = firstIndex + ITEMS_IN_BATCH
    }
}

internal object NetworkPagination {
    internal var currentPage: Int = 1
    internal var totalPages: Int = 1

    fun nextPage(): Int = currentPage++

    fun resetNavigation(): Int {
        currentPage = 1
        return currentPage
    }
}