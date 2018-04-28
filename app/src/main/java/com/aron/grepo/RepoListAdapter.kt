package com.aron.grepo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aron.grepo.models.Repository
import kotterknife.bindView

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
class RepoListAdapter constructor(
        val list: MutableList<Repository> = ArrayList()
) : RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repo_list_adapter_item, parent, false)
        return RepoViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repository = list[position]

        holder.repoName.text = repository.name
        holder.repoDescription.text = repository.description
        holder.repoLastUpdate.text = repository.lastUpdate
    }

    class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val repoName: TextView by bindView(R.id.repo_list_item_name)
        val repoDescription: TextView by bindView(R.id.repo_list_item_description)
        val repoLastUpdate: TextView by bindView(R.id.repo_list_item_updated)
    }
}