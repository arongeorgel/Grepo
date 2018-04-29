package com.aron.grepo

import com.aron.grepo.models.Repository
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
interface GithubApi {

    @GET("users/{user}/repos")
    fun getRepo(@Path("user") user: String,
                @Query("per_page") items: Int,
                @Query("page") page: Int
    ): Observable<Result<List<Repository>>>
}
