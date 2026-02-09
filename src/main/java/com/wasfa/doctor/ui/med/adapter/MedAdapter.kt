package com.wasfa.doctor.ui.med.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemMedBinding
import com.wasfa.doctor.network.response.Products

class MedAdapter(
    private val data: MutableList<Products>,
    private val listener: (Products, String) -> Unit,
) :
    RecyclerView.Adapter<MedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemMedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data[position])

        holder.itemView.setOnClickListener {
            listener(data[position],"")
        }
        holder.itemBinding.switchFav.setOnCheckedChangeListener(null)

        holder.itemBinding.switchFav.isChecked = data[position].isFavourite ?: false


        holder.itemBinding.switchFav.setOnCheckedChangeListener { _, isChecked ->
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < data.size) {
                if (isChecked) {
                    listener(data[pos], "fav")
                } else {
                    listener(data[pos], "unFav")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(var itemBinding: ItemMedBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Products) {

            itemBinding.txtPrize.text = data.basePrice
            itemBinding.txtProductName.text = data.name


            Glide.with(itemBinding.root.context)
                .load(data.thumbnail_image)
                .into(itemBinding.imgProduct)

            val logoUrl = data.sellerLogo



            itemBinding.txtStock.text =  data.currentStock



        }
    }
        fun setProducts(newProducts: List<Products>) {
            data.clear()
            data.addAll(newProducts)
            notifyDataSetChanged()
        }

        fun addProducts(newProducts: List<Products>) {
            val startPosition = data.size
            data.addAll(newProducts)
            notifyItemRangeInserted(startPosition, newProducts.size)
        }

}