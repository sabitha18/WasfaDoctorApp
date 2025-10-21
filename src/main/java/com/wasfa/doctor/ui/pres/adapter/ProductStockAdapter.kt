package com.wasfa.doctor.ui.pres.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.ui.model.Cat

class ProductStockAdapter(
    private val onSkuSelected: (Cat) -> Unit
) : RecyclerView.Adapter<ProductStockAdapter.SkuViewHolder>() {

    private var skuList: List<Cat> = emptyList()

    fun submitList(newList: List<Cat>) {
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

        fun bind(sku: Cat) {
            tvSkuName.text = sku.name
            itemView.setOnClickListener {
                onSkuSelected(sku)
            }
        }
    }
}
