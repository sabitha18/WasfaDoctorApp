package com.wasfa.doctor.ui.products.category.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemCategoryListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.CatList

class CategoryListAdapter(
    private val data: MutableList<CatList>,
    private val listener: (CatList, String) -> Unit
) :
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            if (PermissionManager.hasPermission(PermissionKeys.EDIT_PRODUCT_CATEGORY)) View.VISIBLE else View.GONE

        holder.itemBinding.btnDelete.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.DELETE_PRODUCT_CATEGORY)) View.VISIBLE else View.GONE


    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemCategoryListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: CatList) {
            Glide.with(itemBinding.root.context)
                .load(data.icon)
                .error(R.drawable.wasfa_logo)
                .into(itemBinding.imgIcon)

            itemBinding.txtCatName.text = "${"   ".repeat(data.level)}${data.name}"

            if (data.parentCategory.isNullOrEmpty()) {
                itemBinding.cardParent.visibility = View.GONE
            } else {
                itemBinding.cardParent.visibility = View.VISIBLE
                itemBinding.txtParent.text = data.parentCategory
            }
        }
    }
    fun setProducts(newProducts: List<CatList>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<CatList>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}