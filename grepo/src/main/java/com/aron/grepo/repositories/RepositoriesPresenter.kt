package com.aron.grepo.repositories

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
class RepositoriesPresenter @Inject constructor(
        private val interactor: RepositoriesInteractor
) {

    private val stateReducer = BiFunction { prevState: RepositoriesState, result: RepositoriesResult ->
        when (result) {
            is RepositoriesResult.InProgress -> {
                prevState.copy(dataLoading = result.dataLoading,
                        networkRefresh = result.networkRefresh)
            }
            is RepositoriesResult.Error -> {
                prevState.copy(dataLoading = false,
                        networkRefresh = false,
                        isError = true,
                        errorMessage = result.errorMessage)
            }
            is RepositoriesResult.Success -> {
                prevState.copy(dataLoading = false,
                        networkRefresh = false,
                        isError = false,
                        errorMessage = "",
                        list = result.list)
            }
        }
    }

    private fun actionFromIntent(intent: RepositoriesIntent): RepositoriesAction =
            when (intent) {
                is RepositoriesIntent.InitialIntent -> RepositoriesAction.LoadFirstBatch
                is RepositoriesIntent.RefreshIntent -> RepositoriesAction.LoadFirstBatch
                is RepositoriesIntent.LoadNextBatch -> RepositoriesAction.LoadNextBatch
            }

    fun execute(intents: Observable<RepositoriesIntent>): Observable<RepositoriesState> {
        return intents
                .map { actionFromIntent(it) }
                .compose(interactor.actionsProcessor)
                .scan(RepositoriesState.default(), stateReducer)
    }
}