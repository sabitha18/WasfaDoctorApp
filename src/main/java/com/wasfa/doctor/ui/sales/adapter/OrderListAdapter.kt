package com.wasfa.doctor.ui.sales.adapter

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemOrderListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.AddressOrder
import com.wasfa.doctor.network.response.Orders

class OrderListAdapter(
    private val data: MutableList<Orders>,
    private val listener: (Orders, String) -> Unit,
    private val listenerDelivery: (Orders, String) -> Unit
) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])
        holder.itemBinding.cardView.setOnClickListener { view ->
          listener(data!![position], "view")

        }
        holder.itemBinding.cardChangeStatus.setOnClickListener {
            listenerDelivery(data!![position], "status")
        }
        holder.itemBinding.cardView.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.VIEW_ORDER_DETAILS)) View.VISIBLE else View.GONE
        holder.itemBinding.lytCopy.setOnClickListener {
            val paymentLink = data[position].paymentLink.orEmpty()

            if (paymentLink.isNotBlank()) {
                val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Payment Link", paymentLink)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(holder.itemView.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "No link to copy", Toast.LENGTH_SHORT).show()
            }
        }
        holder.itemBinding.txtPatientAddress.setOnClickListener {
            showPopupArea(holder.itemView.context,data!![position].addressArray,data[position].areaLat,data[position].areaLong,data[position].mapLink)

        }
    }
    private fun showPopupArea(
        context: Context,
        customerAddress: AddressOrder,
        areaLat: String,
        areaLong: String,
        mapLink: String
    ) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_show_text, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val txtContent = dialogView.findViewById<TextView>(R.id.txt_address)

        val txtGovernorate = dialogView.findViewById<TextView>(R.id.txt_governorate)
        val txtArea = dialogView.findViewById<TextView>(R.id.txt_area)
        val txtBlock = dialogView.findViewById<TextView>(R.id.txt_block)
        val txtStreet = dialogView.findViewById<TextView>(R.id.txt_street)
        val txtBuilding = dialogView.findViewById<TextView>(R.id.txt_building)
        val txtFloor = dialogView.findViewById<TextView>(R.id.txt_floor)
        val txtLane = dialogView.findViewById<TextView>(R.id.txt_lane)
        val txtFlat = dialogView.findViewById<TextView>(R.id.txt_flat)


        txtGovernorate.text = customerAddress?.governorate
            ?.takeIf { it.isNotBlank() } ?: ""

        txtArea.text = customerAddress?.areaName
            ?.takeIf { it.isNotBlank() } ?: ""

        txtBlock.text = customerAddress?.block
            ?.takeIf { it.isNotBlank() } ?: ""

        txtStreet.text = customerAddress?.street
            ?.takeIf { it.isNotBlank() } ?: ""

        txtBuilding.text = customerAddress?.building
            ?.takeIf { it.isNotBlank() } ?: ""

        txtFloor.text = customerAddress?.floor
            ?.takeIf { it.isNotBlank() } ?: ""

        txtLane.text = customerAddress?.lane
            ?.takeIf { it.isNotBlank() } ?: ""

        txtFlat.text = customerAddress?.flat
            ?.takeIf { it.isNotBlank() } ?: ""

        txtContent.text = mapLink
            ?.takeIf { it.isNotBlank() } ?: ""

        txtContent.apply {
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        txtContent.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
            intent.setPackage("com.google.android.apps.maps")
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
                context.startActivity(browserIntent)
            }
        }

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemOrderListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Orders) {

            itemBinding.txtOrderId.text = data?.orderCode
            itemBinding.txtPatientNumber.text = data?.mobileNumber
            itemBinding.txtPatientAddress.apply {
                text = data?.customerAddress
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
            itemBinding.txtOrderStatus.text = data?.deliveryStatus
                ?.replace("_", " ")
                ?.replaceFirstChar { it.uppercase() }
                ?: ""
            itemBinding.txtOrderAmount.text = data?.orderAmount
            itemBinding.txtOrderQuantity.text = data?.productCount
            itemBinding.txtPickUpPoint.text = data?.pickupPoints

            itemBinding.txtSeller.text = data?.seller
            itemBinding.txtZone.text = data?.zone
            itemBinding.txtRefund.text = data?.refund
            itemBinding.txtInfluencer.text = data?.influencer
            itemBinding.txtCreatedAt.text = data?.createdAt
            itemBinding.txtPaymentStatus.text = data?.paymentStatus
            itemBinding.txtPaymentMethod.text = data?.paymentMethod

            itemBinding.switchCollectedByApix.isChecked = data?.collectedByApix == "1"

            if (data.deliveryStatus.equals("delivered", ignoreCase = true) ||
                data.deliveryStatus.equals("cancelled", ignoreCase = true) ||
                data.deliveryStatus.equals("closed", ignoreCase = true)) {

                itemBinding.txtOrderStatusNew.text = data.deliveryStatus
                itemBinding.cardChangeStatus.visibility = View.GONE

            } else {
                itemBinding.cardChangeStatus.visibility = View.VISIBLE
                itemBinding.txtOrderStatusNew.text = ""
            }
        }
    }
    fun setOrder(newProducts: List<Orders>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addOrder(newProducts: List<Orders>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}