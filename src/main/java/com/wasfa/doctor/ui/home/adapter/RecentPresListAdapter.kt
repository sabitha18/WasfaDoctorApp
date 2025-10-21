package com.wasfa.doctor.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemRecentPresBinding
import com.wasfa.doctor.network.response.PresHome

class RecentPresListAdapter(
    private val presList: List<PresHome>?,
    private val listener: (PresHome, String) -> Unit
) : RecyclerView.Adapter<RecentPresListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentPresBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = presList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presList?.get(position)?.let { presHome ->
            holder.bindItem(presHome)
            holder.itemView.setOnClickListener {
                listener(presHome, "view")
            }
        }
    }

    class ViewHolder(private val itemBinding: ItemRecentPresBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(data: PresHome) {
            // Name and medications (assuming non-nullable fields in your data model)
            itemBinding.txtCustName.text = data.name ?: ""
            itemBinding.txtMedPres.text = data.medicationsPrescribed ?: ""

            // Safely extract phone
            val phone = data.patientInfo?.firstOrNull()?.phone
            itemBinding.txtPhone.text = when {
                phone.isNullOrBlank() || phone == "null" -> ""
                else -> phone
            }

            // Safely load profile picture
            val profilePicUrl = data.patientInfo?.firstOrNull()?.profilePic
            if (!profilePicUrl.isNullOrEmpty()) {
                Glide.with(itemBinding.root.context)
                    .load(profilePicUrl)
                    .into(itemBinding.imgCust)
            } else {
                // Optional: set a placeholder or clear image
                itemBinding.imgCust.setImageDrawable(null)
            }
        }
    }
}
