package com.wasfa.doctor.ui.products.brand.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemBrandListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.Brands

class BrandListAdapter(
    private val data: MutableList<Brands>,
    private val listener: (Brands, String) -> Unit
) :
    RecyclerView.Adapter<BrandListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemBrandListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemView.setOnClickListener {
            listener(data!![position],"all")
        }

        holder.itemBinding.btnEdit.setOnClickListener {
            listener(data!![position],"edit")
        }

        holder.itemBinding.btnDelete.setOnClickListener {
            listener(data!![position],"delete")
        }

        holder.itemBinding.btnEdit.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.EDIT_BRAND)) View.VISIBLE else View.GONE

        holder.itemBinding.btnDelete.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.DELETE_BRAND)) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemBrandListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Brands) {
            Glide.with(itemBinding.root.context)
                .load(data.logo)
                .error(R.drawable.wasfa_logo)
                .into(itemBinding.imgBrand)
            itemBinding.txtBrandName.text = data?.name
        }
    }
    fun setProducts(newProducts: List<Brands>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<Brands>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}