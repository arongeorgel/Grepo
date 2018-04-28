package com.aron.grepo

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView

class RepoListActivity : AppCompatActivity() {

    private val repoList: RecyclerView by bindView(R.id.repo_list)
    private val refreshList: SwipeRefreshLayout by bindView(R.id.repo_list_refresh)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.repo_list_activity)
        executeUseCase()

        refreshList.setOnRefreshListener {
            executeUseCase()
        }
    }

    private fun executeUseCase() {
        refreshList.isRefreshing = true
        GetUserRepoUseCase().execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    refreshList.isRefreshing = false

                    val adapter = RepoListAdapter()
                    adapter.list.addAll(it)

                    repoList.layoutManager = LinearLayoutManager(this)
                    repoList.adapter = adapter
                }
    }
}
