package com.wasfa.doctor.doctor.pres.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemMedicationListBinding
import com.wasfa.doctor.network.response.Products

class MedicationAdapter(
    private val data: MutableList<Products>,
    private val listener: (Products, String) -> Unit,
) :
    RecyclerView.Adapter<MedicationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemMedicationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemBinding.cardAddMed.setOnClickListener{
            listener(data!![position],"add")
        }

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemMedicationListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Products) {
            itemBinding.txtPrize.text = data?.basePrice
            itemBinding.txtProductName.text = data?.name
            itemBinding.txtStoreName.text = data?.sellerName

            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)

            val logoUrl = data?.sellerLogo
            Glide.with(itemBinding.root.context)
                .load(logoUrl)
                .error(R.drawable.wasfa_logo)
                .into(itemBinding.imgSeller)
            itemBinding.txtStock.text =  data?.currentStock
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