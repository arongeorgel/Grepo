package com.aron.grepo

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView

class RepoListActivity : AppCompatActivity(), EndlessScrollListener.LoadMoreListener {

    private val repoList: RecyclerView by bindView(R.id.repo_list)
    private val refreshList: SwipeRefreshLayout by bindView(R.id.repo_list_refresh)

    private val listAdapter = RepoListAdapter()
    private val useCase = GetUserRepoUseCase(Schedulers.io())
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.repo_list_activity)
        executeUseCase(true)

        val layoutManager = LinearLayoutManager(this)
        scrollListener = EndlessScrollListener(layoutManager, this)

        repoList.layoutManager = layoutManager
        repoList.adapter = listAdapter
        repoList.addOnScrollListener(scrollListener)

        refreshList.setOnRefreshListener {
            executeUseCase(true)
        }
    }

    override fun onLoadMore() {
        executeUseCase(false)
    }

    private fun executeUseCase(isRefreshing: Boolean) {
        if (isRefreshing) {
            refreshList.isRefreshing = true
        } else {
            listAdapter.showLoader = true
            listAdapter.notifyDataSetChanged()
        }

        useCase.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    refreshList.isRefreshing = false
                    listAdapter.showLoader = false
                    scrollListener.loading.set(false)

                    listAdapter.list.addAll(it)
                    listAdapter.notifyDataSetChanged()
                }
    }
}
