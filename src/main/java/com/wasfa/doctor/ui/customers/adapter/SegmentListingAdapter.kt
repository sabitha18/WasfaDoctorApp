package com.wasfa.doctor.ui.customers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.Segments

class SegmentListingAdapter(
    private val onSkuSelected: (Segments) -> Unit
) : RecyclerView.Adapter<SegmentListingAdapter.SkuViewHolder>() {

    private var skuList: List<Segments> = emptyList()

    fun submitList(newList: List<Segments>) {
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

        fun bind(sku: Segments) {
            tvSkuName.text = sku.name
            itemView.setOnClickListener {
                onSkuSelected(sku)
            }
        }
    }
}
