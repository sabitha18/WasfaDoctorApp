package com.wasfa.doctor.ui.sales.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemOrderDetailsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.response.ItemsList
import com.wasfa.doctor.network.response.PurchaseForm

class OrderDetailsAdapter(
    private val CatList: List<ItemsList>?,
    private val listener: (ItemsList, Int) -> Unit
) :
    RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemOrderDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])
        holder.itemBinding.imgProduct.setOnClickListener {
            listener(CatList!![position], 1111)

        }

        if (AppPreferences.getInstance(holder.itemView.context).getLoginType() == "delivery_boy"){
            holder.itemBinding.lytCollected.visibility = View.VISIBLE
        }else{
            holder.itemBinding.lytCollected.visibility = View.GONE
        }

        val purchaseFromList = CatList!![position].purchaseFrom

        if (purchaseFromList.isNullOrEmpty()) {
            holder.itemBinding.txtPurchaseForm.visibility = View.GONE
        } else {
            holder.itemBinding.txtPurchaseForm.visibility = View.VISIBLE
        }
        holder.itemBinding.txtPurchaseForm.setOnClickListener {

            showNotes(holder.itemView.context,CatList!![position].purchaseFrom)
        }

        holder.itemBinding.switchCollected.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener(CatList!![position], 1)
            } else {
                listener(CatList!![position], 0)
            }
        }

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }
    private fun showNotes(context: Context, notes: List<PurchaseForm>) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_show_cust_notes, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val txtHead = dialogView.findViewById<TextView>(R.id.txt_head)
        val txtHeading = dialogView.findViewById<TextView>(R.id.txt_heading)
        txtHeading.text = "Purchase From"


        val bulletList = notes.joinToString(separator = "\n\n") { "• ${it.name}" }
        txtHead.text = bulletList

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    class ViewHolder(var itemBinding: ItemOrderDetailsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: ItemsList) {

            Glide.with(itemBinding.root.context)
                .load(data.thumbnailImage)
                .error(R.drawable.wasfa_logo)
                .into(itemBinding.imgProduct)

            itemBinding.txtProductName.text = data?.productName
            itemBinding.txtPrize.text = data?.price
            itemBinding.txtSale.text = data?.quantity
            itemBinding.txtSellerName.text = "Seller: "+data?.seller
            itemBinding.switchCollected.isChecked = data.PickedUpStatus != 0

            itemBinding.txtPurchaseForm.apply {
                text = "Purchase From"
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }


        }


    }
}
