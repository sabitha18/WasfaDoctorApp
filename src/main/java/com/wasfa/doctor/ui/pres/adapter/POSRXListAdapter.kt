package com.wasfa.doctor.ui.pres.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemPosRxProductBinding
import com.wasfa.doctor.network.response.Products

class POSRXListAdapter(
    private val data: MutableList<Products>,
    private val type: String,
    private val listener: (Products, String) -> Unit,
) :
    RecyclerView.Adapter<POSRXListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemPosRxProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemBinding.lytView.setOnClickListener {
            listener(data!![position], "detail")
        }
        holder.itemBinding.cardAddCart.setOnClickListener {
            listener(data!![position],"add")
        }
        holder.itemBinding.lytViewEye.setOnClickListener {
            listener(data!![position],"view")
        }
        holder.itemBinding.lytInfo.setOnClickListener {
            listener(data!![position],"info")
        }

        if (type == "shop"){
            holder.itemBinding.lytExtra.visibility = View.VISIBLE
        }else{
            holder.itemBinding.lytExtra.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemPosRxProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Products) {

            itemBinding.txtPrize.text = data?.basePrice
            itemBinding.txtProductName.text = data?.name
            itemBinding.txtInfluencerMargin.text = "Influencer Margin : "+data?.influencerrMargin
            itemBinding.txtDiscount.text = "Max Discount : "+data?.discount
            itemBinding.txtCog.text = "COG : "+data?.cog
            itemBinding.txtApixMargin.text = "Apix Margin : "+data?.apixMargin

            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)

            if (data?.currentStock == "0") {
                itemBinding.cardAddCart.isEnabled = true
                itemBinding.cardAddCart.alpha = 1.0f
                itemBinding.txtStock.text = "Out of stock"
                itemBinding.cardStock.visibility = View.GONE
                itemBinding.cardStock.setCardBackgroundColor(Color.parseColor("#A61C5C"))
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