package com.wasfa.doctor.doctor.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.wasfa.doctor.databinding.ItemSliderViewBinding
import com.wasfa.doctor.doctor.home.DashboardItem

class SliderAdapter(
    private val context: Context,
    private val data: List<DashboardItem>,
) : RecyclerView.Adapter<SliderAdapter.ViewHolder>() {

    private var selectedPosition = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderAdapter.ViewHolder {
        val v = ItemSliderViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: SliderAdapter.ViewHolder, position: Int) {
        holder.bindItem(data!![position])

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    class ViewHolder(var itemBinding: ItemSliderViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: DashboardItem) {
            itemBinding.txtTitle.text = data?.title
            itemBinding.txtValue.text = data?.value
        }
    }
}

