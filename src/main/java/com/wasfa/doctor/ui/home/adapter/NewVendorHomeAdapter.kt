package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemNewVendorHomeBinding
import com.wasfa.doctor.network.response.NewVendors

class NewVendorHomeAdapter(
    private val CatList: List<NewVendors>?,
    private val listener: (NewVendors, Int) -> Unit
) :
    RecyclerView.Adapter<NewVendorHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemNewVendorHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemNewVendorHomeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: NewVendors) {

            itemBinding.txtId.text = "VDE"+data?.id
            itemBinding.txtName.text = data?.name
            itemBinding.txtNumber.text = data?.phone

        }
    }
}