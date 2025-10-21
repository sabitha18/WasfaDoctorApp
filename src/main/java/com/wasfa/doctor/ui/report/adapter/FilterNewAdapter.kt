package com.wasfa.doctor.ui.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemFilterDeliveryStaffBinding
import com.wasfa.doctor.ui.model.Cat

class FilterNewAdapter(
    private val CatList: List<Cat>?,
    private val listener: (Cat, Int) -> Unit
) :
    RecyclerView.Adapter<FilterNewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemFilterDeliveryStaffBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener {
            listener(CatList!![position], position)
        }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemFilterDeliveryStaffBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Cat) {
            itemBinding.itemName.text = data?.name
        }
    }
}