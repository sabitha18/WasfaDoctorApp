package com.wasfa.doctor.ui.med.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.CustCartItemBinding
import com.wasfa.doctor.network.response.UserDetails

class CustListCartAdapter(
    private var selectedCustomerId: String? = null,
    private val listener: (UserDetails, Int) -> Unit

) :
    RecyclerView.Adapter<CustListCartAdapter.ViewHolder>() {
    private var CatList: List<UserDetails> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = CustCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = CatList!![position]
        val isSelected = item.id == selectedCustomerId
        holder.bindItem(item, isSelected)
            holder.itemView.setOnClickListener {
                listener(CatList[position], position)
            }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: CustCartItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(
            data: UserDetails,
            isSelected: Boolean
        ) {

            itemBinding.txtCustomer.text = data?.name
            val context = itemBinding.root.context
            val selectedColor = Color.parseColor("#A61C5C")
            val unselectedColor = Color.WHITE

            itemBinding.cardCust.setCardBackgroundColor(
                if (isSelected) selectedColor else unselectedColor
            )
            itemBinding.txtCustomer.setTextColor(
                if (isSelected) Color.WHITE else Color.BLACK
            )

        }
    }
    fun submitList(newList: List<UserDetails>) {
        CatList = newList
        notifyDataSetChanged()
    }
}