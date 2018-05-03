package com.aron.grepo.repositories

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
class RepositoriesInteractor @Inject constructor(
        private val useCase: GetRepositoriesUseCase,
        @Named("scheduler-io") private val scheduler: Scheduler
) {

    private var firstBatchProcessor = ObservableTransformer<RepositoriesAction, RepositoriesResult> {
        it.switchMap {
            useCase.execute(true)
                    .onErrorReturn {
                        Timber.w("Failed to execute use case. Reason: ${it.message}")
                        RepositoriesResult.Error("Well this is embarrassing...")
                    }
                    .subscribeOn(scheduler)
                    .startWith(RepositoriesResult.InProgress(true, true))
        }
    }

    private var nextBatchProcessor = ObservableTransformer<RepositoriesAction, RepositoriesResult> {
        it.switchMap {
            useCase.execute(false)
                    .onErrorReturn { RepositoriesResult.Error("Well this is embarrassing...") }
                    .subscribeOn(scheduler)
                    .startWith(RepositoriesResult.InProgress(true, false))
        }
    }

    var actionsProcessor = ObservableTransformer<RepositoriesAction, RepositoriesResult> { actions ->
        actions.publish { shared ->
            Observable.merge(
                    shared.ofType(RepositoriesAction.LoadFirstBatch::class.java)
                            .compose(firstBatchProcessor),
                    shared.ofType(RepositoriesAction.LoadNextBatch::class.java)
                            .compose(nextBatchProcessor)
            )
        }
    }
}
