package com.wasfa.doctor.ui.pres.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.CountryListResponse

class CountryAdapter(
    private val onSkuSelected: (CountryListResponse) -> Unit
) : RecyclerView.Adapter<CountryAdapter.SkuViewHolder>() {

    private var skuList: List<CountryListResponse> = emptyList()

    fun submitList(newList: List<CountryListResponse>) {
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

        fun bind(sku: CountryListResponse) {
            tvSkuName.text = sku.name
            itemView.setOnClickListener {
                onSkuSelected(sku)
            }
        }
    }
}
