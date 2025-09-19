package com.wasfa.doctor.doctor.pres.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemMedicationBinding
import com.wasfa.doctor.network.response.CartItem

class MedicationListAdapter(
    private val CatList: List<CartItem>?,
    private val listener: (CartItem, String) -> Unit
) :
    RecyclerView.Adapter<MedicationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemBinding.imgRemove.setOnClickListener{
            listener(CatList!![position],"remove")
        }

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemMedicationBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: CartItem) {

            Glide.with(itemBinding.root.context)
                .load(data?.productThumbnailImage)
                .into(itemBinding.imgProduct)

            itemBinding.txtProductName.text = data?.productName
            itemBinding.txtPrize.text = data?.unitPrice
            itemBinding.txtStoreName.text = ""
            itemBinding.txtDose.text = formatText(data?.dose, data?.doseday, data?.dose_time)
            itemBinding.txtCourse.text = formatText(data?.course_day, data?.course_duration)
        }

        private fun formatText(vararg parts: String?): String {
            return parts
                .filter { !it.isNullOrBlank() && it.lowercase() != "select" }
                .joinToString(" , ")
        }
    }
}