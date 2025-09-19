package com.wasfa.doctor.ui.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemAllProductBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.Products

class AllProductAdapter(
    private val data: MutableList<Products>,
    private val listener: (Products, String) -> Unit,
) :
    RecyclerView.Adapter<AllProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemAllProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemBinding.imgMore.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_product_actions, popup.menu)
            if (!PermissionManager.hasPermission(PermissionKeys.PRODUCT_EDIT)) {
                popup.menu.findItem(R.id.action_edit)?.isVisible = false
            }

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_apix_margin -> listener(data!![position], "apix_margin")
                    R.id.action_influ_margin -> listener(data!![position], "influ_margin")
                    R.id.action_view -> listener(data!![position], "view")
                    R.id.action_edit -> listener(data!![position], "edit")

                }
                true
            }
            popup.show()
        }

        holder.itemBinding.switchFav.setOnCheckedChangeListener(null)
        holder.itemBinding.switchFav.isChecked = data[position].published == "1"

        holder.itemBinding.switchFav.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener(data[position], "1")
            } else {
                listener(data[position], "0")
            }
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemAllProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Products) {

            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)

            itemBinding.txtStoreName.text = data?.sellerName
            itemBinding.txtProductName.text = data?.name
            itemBinding.txtNumOfSale.text = data?.numberOfsales
            itemBinding.txtBasePrice.text = data?.basePrice
            itemBinding.txtPurchasePrice.text = data?.purchasePrice
            itemBinding.txtCreatedAt.text = data?.createdAt


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