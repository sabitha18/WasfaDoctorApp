package com.wasfa.doctor.ui.pres.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.TabPosRxBinding
import com.wasfa.doctor.network.response.Orders

class OrderListAdapter(
    private val CatList: List<Orders>,
    private val type: String,
    private val listener: (Orders, Int) -> Unit
) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = TabPosRxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener{
            listener(CatList!![position],position)
        }
        if (type == "doctor"){
            holder.itemBinding.txtDoctorName.visibility = View.GONE
        }else{
            holder.itemBinding.txtDoctorName.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: TabPosRxBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Orders) {
            itemBinding.txtDoctorName.text = "Doctor Name:  "+data?.influencer
            itemBinding.txtPatientName.text = "Patient Name:  "+data?.patientName
            itemBinding.txtDate.text = "Date:  "+data?.createdAt
            itemBinding.txtOrderQuantity.text = "ItemCount: "+data?.productCount
            itemBinding.txtPresId.text = "Prescription ID: "+data?.prescriptionNo


        }
    }
}