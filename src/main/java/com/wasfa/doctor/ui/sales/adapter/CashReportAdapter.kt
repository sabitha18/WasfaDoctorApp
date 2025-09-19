package com.wasfa.doctor.ui.sales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemCashReportBinding
import com.wasfa.doctor.network.response.Orders

class CashReportAdapter(
    private val data: MutableList<Orders>,
    private val listener: (Orders, String) -> Unit,
) :
    RecyclerView.Adapter<CashReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemCashReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])


    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemCashReportBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Orders) {
            itemBinding.txtOrderId.text = data?.orderCode
            itemBinding.txtOrderAmount.text = data?.orderAmount




        }
    }

    fun setOrder(newProducts: List<Orders>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addOrder(newProducts: List<Orders>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}