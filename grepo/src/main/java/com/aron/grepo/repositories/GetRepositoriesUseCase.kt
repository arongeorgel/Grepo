package com.aron.grepo.repositories

import com.aron.grepo.Configuration.ITEMS_PER_PAGE
import com.aron.grepo.DatabasePagination
import com.aron.grepo.GithubApi
import com.aron.grepo.GithubHeaderParser
import com.aron.grepo.NetworkPagination
import com.aron.grepo.db.DatabaseEntity
import com.aron.grepo.models.ApiRepository
import com.aron.grepo.models.UrlRel
import com.aron.grepo.network.ErrorState
import com.aron.grepo.network.InternetConnectivity
import com.aron.grepo.network.InternetConnectivity.InternetConnectivityStatus.CONNECTED
import com.aron.grepo.network.InternetConnectivity.InternetConnectivityStatus.DISCONNECTED
import com.aron.grepo.network.NetworkCallState
import com.aron.grepo.network.NetworkCallTransformer
import com.aron.grepo.network.SuccessState
import com.aron.grepo.unsafeListCast
import io.reactivex.Observable
import okhttp3.HttpUrl
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
class GetRepositoriesUseCase @Inject constructor(
        private val databaseEntity: DatabaseEntity,
        private val internetConnectivity: InternetConnectivity,
        retrofit: Retrofit
) {

    private val inProgress: AtomicBoolean = AtomicBoolean(false)
    private val githubApi: GithubApi = retrofit.create(GithubApi::class.java)
    private var shouldUpdatePageNumber: Boolean = false

    fun execute(networkRefresh: Boolean): Observable<RepositoriesResult> {
        if (inProgress.getAndSet(true)) return Observable.empty()

        if (networkRefresh) {
            Timber.d("Requesting fresh data.")
            DatabasePagination.resetNavigation()
            NetworkPagination.resetNavigation()
        } else {
            Timber.d("Requesting next batch of data.")
        }

        return internetConnectivity.connectionStatus().publish { shared ->
            Observable.merge(
                    shared
                            .filter { it == DISCONNECTED }
                            .map { returnNoInternetConnection() },
                    readDatabase(),
                    shared
                            .filter {
                                it == CONNECTED && NetworkPagination.currentPage <= NetworkPagination.totalPages
                            }
                            .switchMap { doNetworkCall() }
            )
        }
    }

    private fun returnNoInternetConnection(): RepositoriesResult {
        Timber.d("No internet connection. Stopping here")
        inProgress.set(false)
        return RepositoriesResult.Error("No internet connection. Stopping here")
    }

    private fun readDatabase(): Observable<RepositoriesResult> {
        val startIndex = DatabasePagination.currentBatch().first
        val endIndex = DatabasePagination.currentBatch().second
        Timber.d("Reading database. Start index: $startIndex, end index: $endIndex")

        return databaseEntity.readBatch(startIndex, endIndex)
                .map {
                    DatabasePagination.nextBatch()

                    shouldUpdatePageNumber = it.isNotEmpty()
                    return@map RepositoriesResult.Success(startIndex == 0, it)
                }
    }

    private fun doNetworkCall(): Observable<RepositoriesResult> {
        Timber.d("Making network call on page ${NetworkPagination.currentPage}")

        return githubApi.getRepo("JakeWharton", ITEMS_PER_PAGE, NetworkPagination.currentPage)
                .doOnNext { inProgress.set(false) }
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
                NetworkPagination.nextPage()

                val headerLinks = GithubHeaderParser.getNavigationLinks(it.headers)
                for (link in headerLinks) {
                    if (link.rel == UrlRel.LAST) {
                        val totalPages = getLastPageNumber(link.httpUrl)
                        Timber.i("Total network pages: $totalPages")
                        NetworkPagination.totalPages = totalPages
                    }
                }
                return@map it.content?.unsafeListCast<ApiRepository>()
            }
            .switchMap {
                databaseEntity.saveAll(it)
            }
            .map {
                RepositoriesResult.Success(NetworkPagination.currentPage == 1, it)
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
