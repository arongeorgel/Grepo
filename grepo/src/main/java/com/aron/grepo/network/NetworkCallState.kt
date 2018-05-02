package com.aron.grepo.network

import okhttp3.Headers

/**
 * @author Georgel Aron
 * @since 30/04/2018
 * @version 1.0.0
 */
interface NetworkCallState

data class ErrorState(
        val geekErrorMessage: String? = "",
        val errorMessage: String,
        val exception: Throwable? = null
) : NetworkCallState

data class SuccessState<T> (
        val content: T,
        val headers: Headers
) : NetworkCallState