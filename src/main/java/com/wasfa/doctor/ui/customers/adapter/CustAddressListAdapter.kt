package com.wasfa.doctor.ui.customers.adapter

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
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemCustAddressListBinding
import com.wasfa.doctor.network.response.AddressList

class CustAddressListAdapter(
    private val CatList: List<AddressList>?,
    private val listener: (AddressList, String) -> Unit
) :
    RecyclerView.Adapter<CustAddressListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemCustAddressListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemBinding.btnEdit.setOnClickListener{
            listener(CatList!![position],"edit")
        }
        holder.itemBinding.btnDelete.setOnClickListener{
            listener(CatList!![position],"delete")
        }
        holder.itemBinding.txtAddress.setOnClickListener {
            showPopupArea(holder.itemView.context,CatList!![position])
        }

    }
    private fun showPopupArea(
        context: Context,
        customerAddress: AddressList
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


        txtGovernorate.text = customerAddress?.governorateName
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

//        txtLane.text = customerAddress?.lane
//            ?.takeIf { it.isNotBlank() } ?: ""
//
//        txtFlat.text = customerAddress?.flat
//            ?.takeIf { it.isNotBlank() } ?: ""

        txtContent.visibility = View.GONE

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
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemCustAddressListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: AddressList) {

            itemBinding.txtName.text = listOfNotNull(data?.firstName, data?.lastName)
                .joinToString(" ")

            itemBinding.txtNumber.setText(data?.phone
                ?.takeIf { it.isNotBlank() } ?: "")

            itemBinding.txtEmail.setText(data?.email
                ?.takeIf { it.isNotBlank() } ?: "")


            itemBinding.txtAddress.apply {
                text = listOfNotNull(data?.governorateName, data?.areaName,data?.block,data?.building)
                    .joinToString(", ")
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
        }
    }

}