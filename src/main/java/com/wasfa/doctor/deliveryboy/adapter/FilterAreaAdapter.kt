package com.wasfa.doctor.deliveryboy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemFilterDeliveryStaffBinding
import com.wasfa.doctor.network.response.AreaResponse

class FilterAreaAdapter(
    private val CatList: List<AreaResponse>?,
    private val listener: (AreaResponse, Int) -> Unit
) :
    RecyclerView.Adapter<FilterAreaAdapter.ViewHolder>() {

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
        fun bindItem(data: AreaResponse) {
            itemBinding.itemName.text = data?.name
        }
    }
}