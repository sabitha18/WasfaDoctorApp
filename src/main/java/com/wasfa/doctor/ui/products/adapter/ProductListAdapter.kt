package com.wasfa.doctor.ui.products.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemProductListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.ui.home.model.Cat

class ProductListAdapter(
    private val CatList: List<Cat>,
    private val listener: (Cat, Int) -> Unit
) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener {
            listener(CatList!![position],position)
        }
        val cat = CatList[position]
        when (cat.name) {

            "Add New product" -> {
                holder.itemBinding.root.visibility =
                    if (PermissionManager.hasPermission(PermissionKeys.ADD_NEW_PRODUCT))
                        View.VISIBLE else View.GONE
            }
            "All Products" -> {
                holder.itemBinding.root.visibility =
                    if (PermissionManager.hasPermission(PermissionKeys.SHOW_ALL_PRODUCT))
                        View.VISIBLE else View.GONE
            }
            else -> holder.itemBinding.root.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemProductListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(cat: Cat) {

            itemBinding.txtTitle.text = cat?.name
        }
    }
}