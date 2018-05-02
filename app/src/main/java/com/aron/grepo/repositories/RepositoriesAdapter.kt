package com.aron.grepo.repositories

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aron.grepo.R
import com.aron.grepo.models.RepositoryModel
import kotterknife.bindView

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
class RepositoriesAdapter constructor(
        val list: MutableList<RepositoryModel> = ArrayList(),
        var showLoader: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM = 0x0
        const val TYPE_LOADING = 0x1
    }

    override fun getItemViewType(position: Int): Int =
            if (showLoader && position == list.size) TYPE_LOADING else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(
                    inflater.inflate(R.layout.repo_list_adapter_item_footer, parent, false))
            TYPE_ITEM -> RepoViewHolder(
                    inflater.inflate(R.layout.repo_list_adapter_item, parent, false))
            else -> throw RuntimeException("Unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int = if (showLoader) list.size + 1 else list.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (showLoader && position == list.size) return

        val repository = list[position]
        val holder = viewHolder as RepoViewHolder

        holder.repoName.text = repository.name
        holder.repoDescription.text = repository.description
        holder.repoLastUpdate.text = repository.lastUpdate
    }

    class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val repoName: TextView by bindView(R.id.repositories_item_name)
        val repoDescription: TextView by bindView(R.id.repositories_item_description)
        val repoLastUpdate: TextView by bindView(R.id.repositories_item_updated)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}