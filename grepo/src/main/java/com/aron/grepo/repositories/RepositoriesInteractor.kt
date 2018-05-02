package com.aron.grepo.repositories

import com.aron.grepo.GetRepositoriesUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber

/**
 * @author Georgel Aron
 * @since 02/05/2018
 * @version 1.0.0
 */
class RepositoriesInteractor(
        private val useCase: GetRepositoriesUseCase,
        private val scheduler: Scheduler
) {

    private var firstBatchProcessor = ObservableTransformer<RepositoriesAction, RepositoriesResult> {
        it.switchMap {
            useCase.execute(true)
                    .onErrorReturn { RepositoriesResult.Error("Well this is embarrassing...") }
                    .subscribeOn(scheduler)
                    .startWith(RepositoriesResult.InProgress)
        }
    }

    private var nextBatchProcessor = ObservableTransformer<RepositoriesAction, RepositoriesResult> {
        it.switchMap {
            useCase.execute(false)
                    .onErrorReturn { RepositoriesResult.Error("Well this is embarrassing...") }
                    .subscribeOn(scheduler)
                    .startWith(RepositoriesResult.InProgress)
        }
    }

    var actionsProcessor = ObservableTransformer<RepositoriesAction, RepositoriesResult> { actions ->
        actions.publish { shared ->
            Observable.merge(
                    shared.ofType(RepositoriesAction.LoadFirstBatch::class.java)
                            .doOnNext { Timber.w("processing first batch action") }
                            .compose(firstBatchProcessor),
                    shared.ofType(RepositoriesAction.LoadNextBatch::class.java)
                            .doOnNext { Timber.w("processing next batch action") }
                            .compose(nextBatchProcessor)
            )
        }
    }
}
