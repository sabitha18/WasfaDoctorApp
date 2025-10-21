package com.wasfa.doctor.ui.pres.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemDoseBinding
import com.wasfa.doctor.ui.model.Cat

class DoseAdapter(
    private val CatList: List<Cat>,
    private val listener: (Cat, String) -> Unit
) :
    RecyclerView.Adapter<DoseAdapter.ViewHolder>() {
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemDoseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position],position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

        }

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemDoseBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Cat, isSelected: Boolean) {
            if (isSelected) {
                itemBinding.cardDose.setStrokeColor(Color.parseColor("#A61C5C"))
               itemBinding.cardDose.setCardBackgroundColor(Color.parseColor("#F7EBF0"))
                itemBinding.txtDose.setTextColor(Color.parseColor("#A61C5C"))
            } else {
                itemBinding.cardDose.setStrokeColor(Color.parseColor("#F8F8F8"))
                itemBinding.cardDose.setCardBackgroundColor(Color.parseColor("#F8F8F8"))
                itemBinding.txtDose.setTextColor(Color.parseColor("#BF000000"))
            }
        }
    }
}