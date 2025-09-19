package com.wasfa.doctor.ui.products.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.PurchaseForm

class PurchaseFormAdapter(
    private val onSkuSelected: (List<PurchaseForm>) -> Unit
) : RecyclerView.Adapter<PurchaseFormAdapter.SkuViewHolder>() {

    private var skuList: List<PurchaseForm> = emptyList()
    private val selectedProductIds = mutableSetOf<String>()
    fun submitList(newList: List<PurchaseForm>) {
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
    fun restoreSelection(ids: Set<String>) {
        selectedProductIds.clear()
        selectedProductIds.addAll(ids)
        notifyDataSetChanged()
    }
    inner class SkuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSkuName: TextView = itemView.findViewById(R.id.tvSkuName)

        fun bind(sku: PurchaseForm) {
            val productId = sku.id ?: return
            tvSkuName.text = sku.purchase_from
            itemView.setOnClickListener {
                if (selectedProductIds.contains(productId)) {
                    selectedProductIds.remove(productId)
                } else {
                    selectedProductIds.add(productId)
                }
                notifyItemChanged(adapterPosition)
                val selectedList = skuList.filter { selectedProductIds.contains(it.id) }
                onSkuSelected(selectedList)
            }
        }
    }
}
