package com.wasfa.doctor.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.PosShopSummeryCartItemBinding
import com.wasfa.doctor.network.response.CartList

class POSShopSummeryCartAdapter(
    private val CatList: List<CartList>?,
    private val listener: (CartList, Int) -> Unit,
    private val incrementListener: (CartList, String) -> Unit,
    private val decrementListener: (CartList, String) -> Unit,
    private val removeListener: (CartList) -> Unit,
) :
    RecyclerView.Adapter<POSShopSummeryCartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = PosShopSummeryCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CatList?.let {
            val item = it[position]
            holder.bindItem(item,incrementListener,decrementListener,removeListener)
            holder.itemView.setOnClickListener {
                listener(item, position)
            }

        }

    }

    override fun getItemCount(): Int {
        return CatList?.size ?: 0
    }

    class ViewHolder(var itemBinding: PosShopSummeryCartItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(
            data: CartList,
            incrementListener: (CartList, String) -> Unit,
            decrementListener: (CartList, String) -> Unit,
            removeListener: (CartList) -> Unit
        ) {

            Glide.with(itemBinding.root.context)
                .load(data?.productThumbnailImage)
                .into(itemBinding.imgCart)

            itemBinding.txtProductName.text = data?.productName
            itemBinding.txtPrize.text = data?.unitPrice
            itemBinding.txtCount.text = data?.quantity

            itemBinding.cardIncreaseCount.setOnClickListener {
                val currentCount = itemBinding.txtCount.text.toString().toInt()
                val newCount = currentCount + 1
                itemBinding.txtCount.text = newCount.toString()
                incrementListener(data,itemBinding.txtCount.text.toString())
            }

            itemBinding.cardDecreaseCount.setOnClickListener {
                val currentCount = itemBinding.txtCount.text.toString().toInt()
                if (currentCount > 1) {
                    val newCount = currentCount - 1
                    itemBinding.txtCount.text = newCount.toString()
                    decrementListener(data,itemBinding.txtCount.text.toString())
                }
            }
            itemBinding.imgDelete.setOnClickListener {
                removeListener(data)
            }
        }
    }
}