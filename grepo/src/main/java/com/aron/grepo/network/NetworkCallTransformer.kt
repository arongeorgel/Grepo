package com.aron.grepo.network

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import okhttp3.Headers
import retrofit2.adapter.rxjava2.Result

/**
 * @author Georgel Aron
 * @since 01/05/2018
 * @version 1.0.0
 */
class NetworkCallTransformer<T>(
        private val errorMessage: String
) : ObservableTransformer<Result<T>, NetworkCallState> {

    private companion object {
        const val HTTP_BODY_CODE = 200
    }

    override fun apply(upstream: Observable<Result<T>>): ObservableSource<NetworkCallState> = upstream.map { result ->
        when {
            result.isError -> ErrorState("", errorMessage, result.error())
            else -> {
                val response = result.response()
                        ?: return@map ErrorState("",
                                "The response of the result is null",
                                RuntimeException()
                        )

                if (response.code() == HTTP_BODY_CODE && response.body() != null) {
                    return@map SuccessState(response.body(), response.headers())
                } else {
                    return@map SuccessState(Unit, Headers.Builder().build())
                }
            }
        }
    }

}
