package com.wasfa.doctor.ui.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemSellerListBinding
import com.wasfa.doctor.ui.home.model.Cat

class SellerListAdapter(
    private val CatList: List<Cat>,
    private val listener: (Cat, Int) -> Unit
) :
    RecyclerView.Adapter<SellerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemSellerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener{
            listener(CatList!![position],position)
        }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemSellerListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Cat) {

        }
    }
}