package com.wasfa.doctor.ui.pos.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemPosShopProductBinding
import com.wasfa.doctor.network.response.Products

class POSShopListAdapter(
    private val data: MutableList<Products>,
    private val listener: (Products, String) -> Unit,
) :
    RecyclerView.Adapter<POSShopListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            ItemPosShopProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemView.setOnClickListener {
            listener(data!![position], "detail")
        }
        holder.itemBinding.cardAddCart.setOnClickListener {
            listener(data!![position],"add")
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemPosShopProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Products) {

            itemBinding.txtPrize.text = data?.basePrice
            itemBinding.txtProductName.text = data?.name


            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)

            if (data?.currentStock == "0") {
                itemBinding.cardAddCart.isEnabled = false
                itemBinding.cardAddCart.alpha = 0.5f
                itemBinding.txtStock.text = "Out of stock"
                itemBinding.cardStock.visibility = View.VISIBLE
                itemBinding.cardStock.setCardBackgroundColor(Color.parseColor("#EC1448"))
            } else {
                itemBinding.cardAddCart.isEnabled = true
                itemBinding.cardAddCart.alpha = 1.0f
                itemBinding.cardAddCart.visibility = View.VISIBLE
                itemBinding.cardStock.visibility = View.GONE
            }
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