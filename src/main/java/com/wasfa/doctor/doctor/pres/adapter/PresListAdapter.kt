package com.wasfa.doctor.doctor.pres.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemPresBinding
import com.wasfa.doctor.network.response.Prescriptions

class PresListAdapter(
    private val data: MutableList<Prescriptions>,
    private val listener: (Prescriptions, String) -> Unit,
) :
    RecyclerView.Adapter<PresListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemPresBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemBinding.cardView.setOnClickListener {
            listener(data!![position], "view")
        }

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemPresBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Prescriptions) {
            itemBinding.txtCustName.text = data?.name
            itemBinding.txtMedPres.text = data?.medicationsPrescribed
            itemBinding.txtMedCondition.text =
                if (data?.patientInfo?.get(0)?.phone.isNullOrBlank() || data?.patientInfo?.get(0)?.phone == "null") {
                    ""
                } else {
                    data?.patientInfo?.get(0)?.phone
                }


            Glide.with(itemBinding.root.context)
                .load(data?.patientInfo?.get(0)?.profilePic)
                .into(itemBinding.imgCust)
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