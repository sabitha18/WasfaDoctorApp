package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemBestSelling2ProductBinding
import com.wasfa.doctor.network.response.MostViewed

class BestSeller2HomeAdapter(
    private val CatList: List<MostViewed>?,
    private val listener: (MostViewed, Int) -> Unit
) :
    RecyclerView.Adapter<BestSeller2HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemBestSelling2ProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

    }

    override fun getItemCount(): Int {
        return if (CatList.isNullOrEmpty()) 0 else minOf(CatList.size, 5)
    }

    class ViewHolder(var itemBinding: ItemBestSelling2ProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: MostViewed) {
            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)
            itemBinding.txtPrize.text = data?.basePrice

            itemBinding.txtName.text = data?.name
            itemBinding.txtStock.text = data?.currentStock

        }
    }
}