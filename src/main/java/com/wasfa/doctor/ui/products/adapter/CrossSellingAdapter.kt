package com.wasfa.doctor.ui.products.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.Products

class CrossSellingAdapter(
    private val onSelectionChanged: (List<Products>) -> Unit
) : RecyclerView.Adapter<CrossSellingAdapter.SkuViewHolder>() {

    private var skuList: List<Products> = emptyList()
    private val selectedProductIds = mutableSetOf<String>()

    fun submitList(newList: List<Products>) {
        skuList = newList
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedProductIds.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_related, parent, false)
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

        fun bind(product: Products) {
            val productId = product.id ?: return

            tvSkuName.text = product.name

            itemView.setOnClickListener {
                if (selectedProductIds.contains(productId)) {
                    selectedProductIds.remove(productId)
                } else {
                    selectedProductIds.add(productId)
                }
                notifyItemChanged(adapterPosition)
                val selectedList = skuList.filter { selectedProductIds.contains(it.id) }
                onSelectionChanged(selectedList)
            }
        }
    }
}



