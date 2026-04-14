package com.wasfa.doctor.ui.med.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemFilterDeliveryStaffBinding
import com.wasfa.doctor.network.response.Brands

class FilterBrandAdapter(
    private val list: MutableList<Brands>,
    private val listener: (Brands, Int) -> Unit
) : RecyclerView.Adapter<FilterBrandAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterDeliveryStaffBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.bindItem(item)

        holder.itemView.setOnClickListener {
            listener(item, position)
        }
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(private val binding: ItemFilterDeliveryStaffBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(data: Brands) {
            binding.itemName.text = data.name
        }
    }
}