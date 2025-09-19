package com.wasfa.doctor.ui.influencer.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemInfluencerListBinding
import com.wasfa.doctor.network.response.InfluencerListResponse

class InfluencerListAdapter(
    private val CatList: List<InfluencerListResponse>,
    private val listener: (InfluencerListResponse, Int) -> Unit
) :
    RecyclerView.Adapter<InfluencerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemInfluencerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener{
            listener(CatList!![position],position)
        }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemInfluencerListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: InfluencerListResponse) {

            itemBinding.txtDoctorName.text = data?.name
            itemBinding.txtNumber.text = data?.mobileNo
            itemBinding.txtDest.text =  data?.ifr_id
            itemBinding.txtClinic.text = data?.enrolledOn

            if (data?.status == "1"){
                itemBinding.txtStatus.text = "Active"
                itemBinding.cardStatus.setCardBackgroundColor(Color.parseColor("#5DB245"))
            }else{
                itemBinding.txtStatus.text = "InActive"
                itemBinding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF0000"))
            }
        }
    }
}