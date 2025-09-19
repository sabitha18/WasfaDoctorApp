package com.wasfa.doctor.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.AddressItemBinding
import com.wasfa.doctor.network.response.AddressListResponse


class AddressAdapter(
    private val data: List<AddressListResponse>?,
    private val listener: (AddressListResponse, String) -> Unit
) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = AddressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bindItem(position,data!![position],listener,selectedPosition)
        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)

            listener(data[position],position.toString())

        }

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(
        private val itemBinding: AddressItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(
            position: Int,
            data: AddressListResponse,
            listener: (AddressListResponse, String) -> Unit,
            selectedPosition: Int
        ) {

            itemBinding.txtEdit.setOnClickListener {
                listener(data,"edit")
            }

            if (position == selectedPosition) {
                itemBinding.cardReminderSelected.visibility = View.VISIBLE
            } else {
                itemBinding.cardReminderSelected.visibility = View.GONE
            }

            itemBinding.txtName.text = data?.firstName +" "+ data?.lastName
            itemBinding.txtNumber.text = data?.phone
            itemBinding.txtAddress.text = data?.areaName +", "+data?.governorateName+", "+data?.addressTitle

        }
    }
}