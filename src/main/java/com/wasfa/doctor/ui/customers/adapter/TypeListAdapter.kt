package com.wasfa.doctor.ui.customers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemTypeListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.Types

class TypeListAdapter(
    private val data: MutableList<Types>,
    private val listener: (Types, String) -> Unit,
    private val listenerSwitch: (Types, String,String) -> Unit
) :
    RecyclerView.Adapter<TypeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemTypeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])


        holder.itemBinding.btnDelete.setOnClickListener {
            listener(data!![position],"delete")
        }
        holder.itemBinding.btnEdit.setOnClickListener {
            listener(data!![position],"edit")
        }

        holder.itemBinding.switchStatus.setOnCheckedChangeListener(null)

        holder.itemBinding.switchStatus.isChecked = data!![position]?.status == "1"

        holder.itemBinding.switchStatus.setOnCheckedChangeListener { _, isChecked ->

            if ((data!![position]?.status == "1") != isChecked) {
                listenerSwitch(data!![position], if (isChecked) "1" else "0", "switch")
            }
        }

        holder.itemBinding.btnDelete.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.DELETE_TYPE)) View.VISIBLE else View.GONE

        holder.itemBinding.btnEdit.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.EDIT_TYPE)) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemTypeListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Types) {

            itemBinding.txtName.text = data?.name

        }
    }
    fun setProducts(newProducts: List<Types>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<Types>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}