package com.wasfa.doctor.doctor.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemReportBinding
import com.wasfa.doctor.databinding.ItemReportRxBinding
import com.wasfa.doctor.network.response.Report

class ReportRXAdapter(
    private val data: MutableList<Report>,
    private val listener: (Report, String) -> Unit,
) :
    RecyclerView.Adapter<ReportRXAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemReportRxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])


    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemReportRxBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Report) {
            itemBinding.txtOrderId.text = data?.orderCode
            itemBinding.txtOrderDate.text = data?.date


        }
    }

    fun setProducts(newProducts: List<Report>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<Report>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}