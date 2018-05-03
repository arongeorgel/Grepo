package com.aron.grepo.mvi

import io.reactivex.ObservableTransformer

/**
 * @author Georgel Aron
 * @since 03/05/2018
 * @version 1.0.0
 */
abstract class Interactor<Action, Result> {

    abstract fun actionsProcessor(): ObservableTransformer<Action, Result>
}