package com.aron.grepo.mvi

import com.aron.grepo.repositories.RepositoriesIntent
import com.aron.grepo.repositories.RepositoriesState
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * @author Georgel Aron
 * @since 03/05/2018
 * @version 1.0.0
 */
abstract class Presenter<Intent, Action, Result, State> constructor(
        private val interactor: Interactor<Action, Result>
) {

    abstract val defaultState: State
    abstract val stateReducer: BiFunction<State, Result, State>
    abstract fun actionFromIntent(intent: Intent): Action

    fun execute(intents: Observable<Intent>): Observable<State> {
        return intents
                .map { actionFromIntent(it) }
                .compose(interactor.actionsProcessor())
                .scan(defaultState, stateReducer)
    }
}