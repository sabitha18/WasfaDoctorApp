package com.wasfa.doctor.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemNewCustomersHomeBinding
import com.wasfa.doctor.network.response.NewCustomers

class NewCustHomeAdapter(
    private val CatList: List<NewCustomers>?,
    private val listener: (NewCustomers, Int) -> Unit
) :
    RecyclerView.Adapter<NewCustHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemNewCustomersHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return if (CatList.isNullOrEmpty()) 0 else minOf(CatList.size, 5)
    }

    class ViewHolder(var itemBinding: ItemNewCustomersHomeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: NewCustomers ){

            itemBinding.txtStatus.text = data?.status
            itemBinding.txtName.text = data?.name
            itemBinding.txtDate.text = data?.enrolled_on
            itemBinding.txtPhone.text = data?.phone
            if (!data?.recentAddress.isNullOrEmpty()){
                itemBinding.txtAddress.text = data?.recentAddress?.get(0)?.addressTitle + ", "+data?.recentAddress?.get(0)?.governorateName+", "+data?.recentAddress?.get(0)?.areaName+", "+data?.recentAddress?.get(0)?.block+", "+data?.recentAddress?.get(0)?.street

            }else{
                itemBinding.txtAddress.text = ""
            }


            if (data?.status == "Active"){
                itemBinding.cardStatus.setCardBackgroundColor(Color.parseColor("#5DB245"))
            }else{
                itemBinding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF0000"))
            }
        }
    }
}