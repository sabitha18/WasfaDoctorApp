package com.wasfa.doctor.doctor.pres.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemMedicationPreviewBinding
import com.wasfa.doctor.network.response.CartItem
import com.wasfa.doctor.network.response.PresDetails

class MedicationListPreviewEditAdapter(

    private val CatList: List<PresDetails>?,
    private val listener: (PresDetails, String) -> Unit
) :
    RecyclerView.Adapter<MedicationListPreviewEditAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemMedicationPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener{
            listener(CatList!![position],"view")
        }

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemMedicationPreviewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: PresDetails) {
            Glide.with(itemBinding.root.context)
                .load(data?.productThumbnailImage)
                .into(itemBinding.imgProduct)
            itemBinding.txtProductName.text = data?.productName
            itemBinding.txtPrize.text = data?.unitPrice
            itemBinding.txtNotes.text = data?.description
            val dose = data?.dose?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }
            val doseday = data?.doseday?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }
            val doseTime =
                data?.dose_time?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }

//            val unit = if (doseday != null) {
//                data?.unit?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }
//            } else null


            val doseUnit = listOfNotNull(doseday, "").joinToString(" ")


            val otherParts = listOfNotNull(dose, doseTime).joinToString(" / ")


            itemBinding.txtDose.text = listOfNotNull(
                doseUnit.takeIf { it.isNotBlank() },
                otherParts.takeIf { it.isNotBlank() }
            ).joinToString(" / ")


            itemBinding.txtCourse.text = formatText(data?.course_duration, data?.course_day)
        }

        private fun formatText(vararg parts: String?): String {
            return parts
                .filter { !it.isNullOrBlank() && it.lowercase() != "select" }
                .joinToString(" ")
        }
    }
}