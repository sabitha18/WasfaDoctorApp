package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemRecentOrdersHomeBinding
import com.wasfa.doctor.network.response.RecentOrders

class RecentOrderHomeAdapter(
    private val CatList: List<RecentOrders>?,
    private val listener: (RecentOrders, Int) -> Unit
) :
    RecyclerView.Adapter<RecentOrderHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemRecentOrdersHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return if (CatList.isNullOrEmpty()) 0 else minOf(CatList.size, 5)
    }

    class ViewHolder(var itemBinding: ItemRecentOrdersHomeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: RecentOrders) {

            itemBinding.txtOrderId.text =  data?.code
            itemBinding.txtDate.text = data?.date
            itemBinding.txtPrize.text = data?.total
            itemBinding.txtStatus.text = data?.delivery_status
            itemBinding.txtStoreName.text = data?.storeName

        }
    }
}