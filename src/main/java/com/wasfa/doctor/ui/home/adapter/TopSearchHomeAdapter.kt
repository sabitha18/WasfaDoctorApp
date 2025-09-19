package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemLastSearchHomeBinding
import com.wasfa.doctor.network.response.TopSearches

class TopSearchHomeAdapter(
    private val CatList: List<TopSearches>?,
    private val listener: (TopSearches, Int) -> Unit
) :
    RecyclerView.Adapter<TopSearchHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemLastSearchHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemLastSearchHomeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: TopSearches) {
            itemBinding.txtName.text = data?.query

        }
    }
}