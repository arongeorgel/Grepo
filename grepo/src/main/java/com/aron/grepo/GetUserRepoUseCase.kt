package com.aron.grepo

import com.aron.grepo.models.Repository
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
class GetUserRepoUseCase {

    private val retrofit: Retrofit

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun execute(): Observable<List<Repository>> {
        val api: GithubApi = retrofit.create(GithubApi::class.java)
        return api.getRepo("JakeWharton")
    }
}
