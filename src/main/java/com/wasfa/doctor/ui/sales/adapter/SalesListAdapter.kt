package com.wasfa.doctor.ui.sales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemSalesListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.ui.home.model.Cat

class SalesListAdapter(
    private val CatList: List<Cat>,
    private val listener: (Cat, Int) -> Unit
) :
    RecyclerView.Adapter<SalesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemSalesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemView.setOnClickListener{
            listener(CatList!![position],position)
        }


    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemSalesListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Cat) {

            itemBinding.txtTitle.text = data?.name
        }
    }
}