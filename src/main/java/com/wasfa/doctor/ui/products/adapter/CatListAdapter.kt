package com.wasfa.doctor.ui.products.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.CatList

class CatListAdapter(
    private val onSkuSelected: (CatList) -> Unit
) : RecyclerView.Adapter<CatListAdapter.SkuViewHolder>() {
    private var skuList: List<CatList> = emptyList()
    fun submitList(newList: List<CatList>) {
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

        fun bind(sku: CatList) {
            tvSkuName.text = sku.name

            val prefix = "-".repeat(sku.level + 1) + " "
            tvSkuName.text = prefix + sku.name


            val params = tvSkuName.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = sku.level * 16
            tvSkuName.layoutParams = params

            itemView.setOnClickListener {
                onSkuSelected(sku)
            }
        }
    }
}
