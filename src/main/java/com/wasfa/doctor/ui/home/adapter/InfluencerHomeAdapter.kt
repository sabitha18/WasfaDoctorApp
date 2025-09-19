package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemInfluencerHomeBinding
import com.wasfa.doctor.network.response.NewDoctors

class InfluencerHomeAdapter(
    private val CatList: List<NewDoctors>?,
    private val listener: (NewDoctors, Int) -> Unit
) :
    RecyclerView.Adapter<InfluencerHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemInfluencerHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return if (CatList.isNullOrEmpty()) 0 else minOf(CatList.size, 5)
    }

    class ViewHolder(var itemBinding: ItemInfluencerHomeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: NewDoctors) {

            itemBinding.txtId.text = data?.ifr_id
            itemBinding.txtName.text = data?.name
            itemBinding.txtNumber.text =  data?.mobile_no
        }
    }
}