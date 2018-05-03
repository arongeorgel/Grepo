package com.aron.grepo.repositories

import com.aron.grepo.mvi.Presenter
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
class RepositoriesPresenter @Inject constructor(
        interactor: RepositoriesInteractor
) : Presenter<RepositoriesIntent, RepositoriesAction, RepositoriesResult, RepositoriesState>(interactor) {

    override val defaultState: RepositoriesState
        get() = RepositoriesState.default()

    override val stateReducer: BiFunction<RepositoriesState, RepositoriesResult, RepositoriesState>
        get() = BiFunction { prevState: RepositoriesState, result: RepositoriesResult ->
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

    override fun actionFromIntent(intent: RepositoriesIntent): RepositoriesAction =
            when (intent) {
                is RepositoriesIntent.InitialIntent -> RepositoriesAction.LoadFirstBatch
                is RepositoriesIntent.RefreshIntent -> RepositoriesAction.LoadFirstBatch
                is RepositoriesIntent.LoadNextBatch -> RepositoriesAction.LoadNextBatch
            }
}