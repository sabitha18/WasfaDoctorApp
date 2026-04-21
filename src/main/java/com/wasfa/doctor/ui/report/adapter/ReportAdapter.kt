package com.wasfa.doctor.ui.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemReportBinding
import com.wasfa.doctor.network.response.Report

class ReportAdapter(
    private val data: MutableList<Report>,
    private val listener: (Report, String) -> Unit,
) :
    RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])


    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemReportBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Report) {
            itemBinding.txtOrderId.text = data?.orderCode
          //  itemBinding.txtDoctorAppreciation.text = data?.doctorAppreciationAmount
            itemBinding.txtSellingPrice.text = data?.sellingPriceAfterAllDiscount
            itemBinding.txtPharmaStatus.text = data?.isPharmacutecal
            itemBinding.txtItemName.text = data?.itenname
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