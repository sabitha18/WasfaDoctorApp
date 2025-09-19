package com.wasfa.doctor.doctor.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemTopSellingPdBinding
import com.wasfa.doctor.network.response.TopSellProd

class TopSellingPDAdapter(
    private val CatList: List<TopSellProd>?,
    private val listener: (TopSellProd, String) -> Unit
) :
    RecyclerView.Adapter<TopSellingPDAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemTopSellingPdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])
        holder.itemView.setOnClickListener {
            listener(CatList!![position],position.toString())
        }

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemTopSellingPdBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: TopSellProd) {
            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)

            val logoUrl = data?.sellerLogo
            Glide.with(itemBinding.root.context)
                .load(logoUrl)
                .error(R.drawable.wasfa_logo)
                .into(itemBinding.imgSeller)
            itemBinding.txtPrize.text = data?.basePrice
            itemBinding.txtStoreName.text = data?.sellerName
            itemBinding.txtProductName.text = data?.name
            itemBinding.txtStock.text = data?.currentStock
        }
    }
}