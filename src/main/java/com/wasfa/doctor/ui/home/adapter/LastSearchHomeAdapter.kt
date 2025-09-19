package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemLastSearchHomeBinding
import com.wasfa.doctor.network.response.LastSearchTerm

class LastSearchHomeAdapter(
    private val CatList: List<LastSearchTerm>?,
    private val listener: (LastSearchTerm, Int) -> Unit
) :
    RecyclerView.Adapter<LastSearchHomeAdapter.ViewHolder>() {

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
        fun bindItem(data: LastSearchTerm) {

            itemBinding.txtName.text = data?.query
        }
    }
}