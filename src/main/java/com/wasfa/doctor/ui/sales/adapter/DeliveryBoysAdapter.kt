package com.wasfa.doctor.ui.sales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.DeliveryBoys

class DeliveryBoysAdapter(
    private val originalList: List<DeliveryBoys>,
    private val onItemSelected: (DeliveryBoys) -> Unit,
    private val preSelectedName: String? = null
) : RecyclerView.Adapter<DeliveryBoysAdapter.ViewHolder>() {

    private var filteredList: MutableList<DeliveryBoys> = originalList.toMutableList()
    private var selectedPosition = -1

    init {
        selectedPosition = originalList.indexOfFirst { it.name == preSelectedName }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: AppCompatRadioButton = view.findViewById(R.id.radio_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_influencer, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val influencer = filteredList[position]
        holder.radioButton.text = influencer.name
        holder.radioButton.isChecked = position == selectedPosition

        holder.radioButton.setOnClickListener {
            val prevSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(prevSelected)
            notifyItemChanged(selectedPosition)
            onItemSelected(influencer)
        }
    }

    fun filterList(query: String) {
        val lowerQuery = query.lowercase()
        filteredList = if (lowerQuery.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name?.lowercase()?.contains(lowerQuery) == true
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}

