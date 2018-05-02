package com.aron.grepo.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.aron.grepo.EndlessScrollListener
import com.aron.grepo.GetRepositoriesUseCase
import com.aron.grepo.R
import com.aron.grepo.db.RepositoryEntity
import com.aron.grepo.network.InternetConnectivity
import com.aron.grepo.network.InternetConnectivity.InternetConnectivityStatus.*
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView
import timber.log.Timber

class RepositoriesActivity : AppCompatActivity(), EndlessScrollListener.LoadMoreListener, InternetConnectivity {

    private val loadMorePublisher = PublishSubject.create<Any>()

    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.repositories_swipe_refresh)
    private val repoList: RecyclerView by bindView(R.id.repositories_list)
    private val errorView: TextView by bindView(R.id.repo_list_item_footer_error)
    private val retryView: View by bindView(R.id.repo_list_item_footer_retry)

    private val listAdapter = RepositoriesAdapter()
    private lateinit var scrollListener: EndlessScrollListener

    private lateinit var presenter: RepositoriesPresenter
    private lateinit var interactor: RepositoriesInteractor
    private lateinit var useCase: GetRepositoriesUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.repositories_activity)

        val layoutManager = LinearLayoutManager(this)
        scrollListener = EndlessScrollListener(layoutManager, this)

        repoList.layoutManager = layoutManager
        repoList.adapter = listAdapter
        repoList.addOnScrollListener(scrollListener)

        inject()

        presenter.execute(intentions())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::render)
    }

    private fun inject() {
        useCase = GetRepositoriesUseCase(RepositoryEntity(), this)
        interactor = RepositoriesInteractor(useCase, Schedulers.io())
        presenter = RepositoriesPresenter(interactor)
    }

    override fun onLoadMore() {
        loadMorePublisher.onNext(Any())
    }

    override fun connectionStatus(): Observable<InternetConnectivity.InternetConnectivityStatus> {
        val connectivityManager: ConnectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected

        return Observable.just(if (isConnected) CONNECTED else DISCONNECTED)
    }

    private fun intentions(): Observable<RepositoriesIntent> {
        return Observable.merge(
                Observable.just(RepositoriesIntent.InitialIntent)
                        .doOnNext { Timber.w("initial intent") },
                RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
                        .doOnNext { Timber.w("refresh intent") }
                        .map { RepositoriesIntent.RefreshIntent },
                loadMorePublisher
                        .skip(1)
                        .doOnNext { Timber.w("load more intent") }
                        .map { RepositoriesIntent.LoadNextBatch }
        )
    }

    private fun render(it: RepositoriesState) {
        swipeRefreshLayout.isRefreshing = it.networkRefresh
        listAdapter.showLoader = it.dataLoading
        scrollListener.loading.set(false)

        if (it.isError) {
            errorView.text = it.errorMessage
            errorView.visibility = View.VISIBLE
            retryView.visibility = View.VISIBLE
        } else {
            errorView.visibility = View.GONE
            retryView.visibility = View.GONE
        }

        listAdapter.list.clear()
        listAdapter.list.addAll(it.list)
        listAdapter.notifyDataSetChanged()
    }
}
