package com.aron.grepo.repositories

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.aron.grepo.EndlessScrollListener
import com.aron.grepo.GrepoApplication
import com.aron.grepo.R
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView
import javax.inject.Inject

class RepositoriesActivity : AppCompatActivity(), EndlessScrollListener.LoadMoreListener {

    private val loadMorePublisher = PublishSubject.create<Any>()
    private val disposable = CompositeDisposable()

    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.repositories_swipe_refresh)
    private val repoList: RecyclerView by bindView(R.id.repositories_list)

    private val listAdapter = RepositoriesAdapter()
    private lateinit var scrollListener: EndlessScrollListener

    @Inject
    lateinit var presenter: RepositoriesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.repositories_activity)

        inject()

        val layoutManager = LinearLayoutManager(this)
        scrollListener = EndlessScrollListener(layoutManager, this)

        repoList.layoutManager = layoutManager
        repoList.adapter = listAdapter
        repoList.addOnScrollListener(scrollListener)

        disposable.add(presenter.execute(intentions())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::render))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun inject() {
        (application as GrepoApplication).appComponent
                .repositoriesComponentBuilder()
                .activity(this)
                .build()
                .inject(this)
    }

    override fun onLoadMore() {
        loadMorePublisher.onNext(Any())
    }

    private fun intentions(): Observable<RepositoriesIntent> {
        return Observable.merge(
                Observable.just(RepositoriesIntent.InitialIntent),
                RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
                        .map { RepositoriesIntent.RefreshIntent },
                loadMorePublisher
                        .map { RepositoriesIntent.LoadNextBatch }
        )
    }

    private fun render(it: RepositoriesState) {
        swipeRefreshLayout.isRefreshing = it.networkRefresh
        listAdapter.showLoader = it.dataLoading && !it.networkRefresh
        scrollListener.loading.set(false)

        if (it.isError) {
            Snackbar.make(swipeRefreshLayout, it.errorMessage, Snackbar.LENGTH_LONG).show()
        }

        if (it.isNewSet) {
            listAdapter.list.clear()
        }

        listAdapter.list.addAll(it.list)
        listAdapter.notifyDataSetChanged()
    }
}
