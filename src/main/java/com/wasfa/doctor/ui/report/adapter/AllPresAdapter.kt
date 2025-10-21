package com.wasfa.doctor.ui.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemReportBinding
import com.wasfa.doctor.network.response.Prescriptions
import com.wasfa.doctor.network.response.Report

class AllPresAdapter(
    private val data: MutableList<Prescriptions>,
    private val listener: (Prescriptions, String) -> Unit,
) :
    RecyclerView.Adapter<AllPresAdapter.ViewHolder>() {

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
        fun bindItem(data: Prescriptions) {
            itemBinding.txtOrderId.text = data?.prescription_id
//            itemBinding.txtDoctorAppreciation.text = data?.doctorAppreciationAmount
//            itemBinding.txtSellingPrice.text = data?.sellingPriceAfterAllDiscount
//            itemBinding.txtPharmaStatus.text = data?.isPharmacutecal
//            itemBinding.txtItemName.text = data?.itenname
//            itemBinding.txtOrderDate.text = data?.date


        }
    }

    fun setProducts(newProducts: List<Prescriptions>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<Prescriptions>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}