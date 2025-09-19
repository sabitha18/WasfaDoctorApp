package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemBestSellingProductBinding
import com.wasfa.doctor.network.response.TopSellProd

class BestSellerHomeAdapter(
    private val CatList: List<TopSellProd>?,
    private val listener: (TopSellProd, Int) -> Unit
) :
    RecyclerView.Adapter<BestSellerHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemBestSellingProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemBestSellingProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: TopSellProd) {

            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)
            itemBinding.txtPrize.text = data?.basePrice

            itemBinding.txtProductName.text = data?.name
            itemBinding.txtQuantity.text = data?.currentStock

            if (data?.currentStock == "0"){
                itemBinding.cardStock.visibility = View.VISIBLE
            }else{
                itemBinding.cardStock.visibility = View.GONE
            }
        }
    }
}