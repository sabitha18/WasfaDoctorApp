package com.wasfa.doctor.deliveryboy.adapter

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
import com.wasfa.doctor.databinding.ItemDeliveryOrderListBinding
import com.wasfa.doctor.network.response.AddressOrder
import com.wasfa.doctor.network.response.Orders

class DeliveryOrderListAdapter(
    private val data: MutableList<Orders>,
    private val listener: (Orders, String) -> Unit,
    private val listenerDelivery: (Orders, String) -> Unit,
    private val listenerPayment: (Orders, String) -> Unit
) :
    RecyclerView.Adapter<DeliveryOrderListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemDeliveryOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        holder.itemBinding.txtPaymentStatus.setOnClickListener {
            listenerPayment(data!![position], "payment")
        }


        holder.itemBinding.lytMobile.setOnClickListener {
            val phoneNumber = data!![position].mobileNumber 
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            holder.itemView.context.startActivity(intent)
        }
        println("map link -----------------    "+data[position].mapLink)
        holder.itemBinding.lytArea.setOnClickListener {
            showPopupArea(holder.itemView.context,data!![position].addressArray,data[position].areaLat,data[position].areaLong,data[position].mapLink)
        }

        holder.itemBinding.txtNotes.setOnClickListener {
            if (!data!![position].customerNote.isNullOrBlank()) {
                showNotes(holder.itemView.context,data!![position].customerNote)
            }
        }

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

    class ViewHolder(var itemBinding: ItemDeliveryOrderListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(dataa: Orders) {

            itemBinding.txtOrderId.text = dataa?.orderCode
            itemBinding.txtPatientNumber.apply {
                text = dataa?.mobileNumber
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${dataa?.mobileNumber}")
                    }
                    context.startActivity(intent)
                }
            }
            itemBinding.txtPaymentLink.text = dataa?.paymentLink
            itemBinding.txtOrderStatus.text = dataa?.deliveryStatus
                ?.replace("_", " ")
                ?.replaceFirstChar { it.uppercase() }
                ?: ""

            itemBinding.txtOrderAmount.text = dataa?.orderAmount
            itemBinding.txtOrderQuantity.text = dataa?.productCount
            itemBinding.txtOrderPlacedTime.text = dataa?.createdAt
            itemBinding.txtPaymentStatus.text = dataa?.paymentStatus
            itemBinding.txtOrderStatusTime.text = dataa?.orderStatusTime
            itemBinding.txtArea.apply {
                text = dataa?.area
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
            itemBinding.txtNotes.apply {
                text = if (dataa?.customerNote.equals("null", ignoreCase = true)) ""
                else dataa?.customerNote ?: ""

                if (!text.isNullOrBlank()) {
                    setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                } else {

                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                    setOnClickListener(null)
                }
            }



            if (dataa.deliveryStatus.equals("delivered", ignoreCase = true) ||
                dataa.deliveryStatus.equals("cancelled", ignoreCase = true) ||
                dataa.deliveryStatus.equals("closed", ignoreCase = true )) {

                itemBinding.txtOrderStatusNew.text = dataa.deliveryStatus
                itemBinding.cardChangeStatus.visibility = View.GONE

            } else {
                itemBinding.cardChangeStatus.visibility = View.VISIBLE
                itemBinding.txtOrderStatusNew.text = ""
            }

            if (dataa.paymentStatus.equals("paid0", ignoreCase = true)){
                itemBinding.txtPaymentStatus.isClickable = true
            }else{
                itemBinding.txtPaymentStatus.isClickable = true
            }




        }
    }
    private fun showNotes(context: Context, notes: String) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_show_cust_notes, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)

        val txtHead = dialogView.findViewById<TextView>(R.id.txt_head)


        txtHead.text = notes

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }
    fun setOrder(newProducts: List<Orders>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addOrder(newProducts: List<Orders>) {
        val uniqueNew = newProducts.filter { new ->
            data.none { it.id == new.id }
        }
        val startPosition = data.size
        data.addAll(uniqueNew)
        notifyItemRangeInserted(startPosition, uniqueNew.size)
    }

}