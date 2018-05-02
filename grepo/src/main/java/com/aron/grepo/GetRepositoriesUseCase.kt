package com.aron.grepo

import com.aron.grepo.Configuration.ITEMS_PER_PAGE
import com.aron.grepo.db.DatabaseEntity
import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.RepositoryModel
import com.aron.grepo.models.UrlRel
import com.aron.grepo.network.ErrorState
import com.aron.grepo.network.InternetConnectivity
import com.aron.grepo.network.InternetConnectivity.InternetConnectivityStatus.CONNECTED
import com.aron.grepo.network.InternetConnectivity.InternetConnectivityStatus.DISCONNECTED
import com.aron.grepo.network.NetworkCallState
import com.aron.grepo.network.NetworkCallTransformer
import com.aron.grepo.network.SuccessState
import com.aron.grepo.repositories.RepositoriesResult
import io.reactivex.Observable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
class GetRepositoriesUseCase constructor(
        private val databaseEntity: DatabaseEntity<ApiRepository, RepositoryModel>,
        private val internetConnectivity: InternetConnectivity
) {

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
                //.client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        githubApi = retrofit.create(GithubApi::class.java)
    }

    fun execute(networkRefresh: Boolean): Observable<RepositoriesResult> {
        if (networkRefresh) {
            Timber.d("Requesting fresh data.")
            currentPage = 0
        } else {
            Timber.d("Requesting next batch of data.")
        }

        return internetConnectivity.connectionStatus().publish { shared ->
            Observable.merge(
                    shared.filter { it == DISCONNECTED }
                            .map { returnNoInternetConnection() },
                    readDatabase(networkRefresh),
                    shared.filter { it == CONNECTED }
                            .switchMap { doNetworkCall() }
            )
        }
    }

    private fun returnNoInternetConnection(): RepositoriesResult {
        Timber.d("No internet connection. Stopping here")
        return RepositoriesResult.Error("No internet connection. Stopping here")
    }

    private fun readDatabase(networkRefresh: Boolean): Observable<RepositoriesResult> {
        val startIndex = (currentPage) * ITEMS_PER_PAGE
        val endIndex = startIndex + ITEMS_PER_PAGE
        currentPage = currentPage.plus(1)
        Timber.d("Reading database. Start index: $startIndex, end index: $endIndex")

        return databaseEntity.readBatch(startIndex, endIndex)
                .map {
                    RepositoriesResult.Success(it)
                }
    }

    private fun doNetworkCall(): Observable<RepositoriesResult> {
        Timber.d("Making network call. Current page is $currentPage")
        return githubApi.getRepo("JakeWharton", ITEMS_PER_PAGE, currentPage + 1)
                .compose(NetworkCallTransformer("Ups... failed to get the next batch"))
                .publish { shared ->
                    Observable.merge(
                            onSuccessApiCall(shared),
                            onErrorApiCall(shared)
                    )
                }
    }

    private fun onSuccessApiCall(it: Observable<NetworkCallState>): Observable<RepositoriesResult> = it
            .ofType(SuccessState::class.java)
            .map {
                val headerLinks = GithubHeaderParser.getNavigationLinks(it.headers)
                for (link in headerLinks) {
                    if (link.rel == UrlRel.LAST) {
                        totalPages = getLastPageNumber(link.httpUrl)
                    }
                }
                return@map it.content!!.unsafeListCast<ApiRepository>()
            }
            .switchMap {
                databaseEntity.saveAll(it)
            }
            .map {
                RepositoriesResult.Success(it)
            }

    private fun onErrorApiCall(it: Observable<NetworkCallState>) = it
            .ofType(ErrorState::class.java)
            .map {
                RepositoriesResult.Error(it.errorMessage)
            }

    private fun getLastPageNumber(url: String): Int {
        val parsedUrl: HttpUrl = HttpUrl.parse(url)!!
        return parsedUrl.queryParameter("page")!!.toInt()
    }
}
