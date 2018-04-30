package com.aron.grepo

import com.aron.grepo.db.DatabaseEntity
import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import com.aron.grepo.models.UrlRel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
class GetUserRepoUseCase constructor(
        private val scheduler: Scheduler,
        private val databaseEntity: DatabaseEntity<ApiRepository, RepositoryModel>
) {

    companion object {
        const val ITEMS_PER_PAGE = 15
    }

    private val retrofit: Retrofit
    private val githubApi: GithubApi
    private var currentPage = 0
    private var totalPages = 1

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

        githubApi = retrofit.create(GithubApi::class.java)
    }

    fun execute(): Observable<List<RepositoryModel>> {
        return when {
            currentPage < totalPages ->
                githubApi.getRepo("JakeWharton", ITEMS_PER_PAGE, ++currentPage)
                        .compose(a)
                        .switchMap { list ->
                            databaseEntity.saveAll(list)
                        }
                        .subscribeOn(scheduler)

            else -> Observable.just(emptyList())
        }
    }

    var a: ObservableTransformer<Result<List<ApiRepository>>, List<ApiRepository>> = ObservableTransformer {
        it.map { result ->
            when {
            // TODO return some error as well
                result.isError -> return@map emptyList<ApiRepository>()
                else -> {
                    val response = result.response() ?: return@map emptyList<ApiRepository>()

                    val links = GithubHeaderParser.getNavigationLinks(response.headers())
                    for (link in links) {
                        if (link.rel == UrlRel.LAST) {
                            totalPages = getLastPageNumber(link.httpUrl)
                        }
                    }
                    return@map result.response()!!.body()
                }
            }
        }
    }

    private fun getLastPageNumber(url: String): Int {
        val parsedUrl: HttpUrl = HttpUrl.parse(url)!!
        return parsedUrl.queryParameter("page")!!.toInt()
    }
}
