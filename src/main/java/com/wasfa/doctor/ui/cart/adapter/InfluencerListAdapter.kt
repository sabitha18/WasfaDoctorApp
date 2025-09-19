package com.wasfa.doctor.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.InfluencerListResponse

class InfluencerListAdapter(
    private val onSkuSelected: (InfluencerListResponse) -> Unit
) : RecyclerView.Adapter<InfluencerListAdapter.SkuViewHolder>() {

    private var skuList: List<InfluencerListResponse> = emptyList()

    fun submitList(newList: List<InfluencerListResponse>) {
        skuList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_apix_sku, parent, false)
        return SkuViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkuViewHolder, position: Int) {
        holder.bind(skuList[position])
    }

    override fun getItemCount(): Int = skuList.size

    inner class SkuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSkuName: TextView = itemView.findViewById(R.id.tvSkuName)

        fun bind(sku: InfluencerListResponse) {
            tvSkuName.text = sku.name
            itemView.setOnClickListener {
                onSkuSelected(sku)
            }
        }
    }
}
