package com.aron.grepo.repositories

import com.aron.grepo.models.RepositoryModel

/**
 * @author Georgel Aron
 * @since 01/05/2018
 * @version 1.0.0
 */
sealed class RepositoriesIntent {

    object InitialIntent : RepositoriesIntent()
    object RefreshIntent : RepositoriesIntent()
    object LoadNextBatch : RepositoriesIntent()
}

sealed class RepositoriesAction {

    object LoadFirstBatch : RepositoriesAction()
    object LoadNextBatch : RepositoriesAction()
}

sealed class RepositoriesResult {

    object InProgress : RepositoriesResult()
    data class Success(val list: List<RepositoryModel>) : RepositoriesResult()
    data class Error(val errorMessage: String) : RepositoriesResult()
}

data class RepositoriesState(
        val networkRefresh: Boolean = false,
        val dataLoading: Boolean = false,
        val list: List<RepositoryModel> = emptyList(),
        val isError: Boolean = false,
        val errorMessage: String = ""
) {
    companion object {
        fun default() = RepositoriesState()
    }
}
