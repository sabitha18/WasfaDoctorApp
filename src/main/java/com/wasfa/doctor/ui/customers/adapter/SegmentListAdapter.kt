package com.wasfa.doctor.ui.customers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemSegmentListBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.Segments

class SegmentListAdapter(
    private val data: MutableList<Segments>,
    private val listener: (Segments, String) -> Unit,
    private val listenerSwitch: (Segments, String, String) -> Unit
) :
    RecyclerView.Adapter<SegmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemSegmentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemBinding.switchStatus.setOnCheckedChangeListener(null)

        holder.itemBinding.switchStatus.isChecked = data!![position]?.status == "1"

        holder.itemBinding.switchStatus.setOnCheckedChangeListener { _, isChecked ->

            if ((data!![position]?.status == "1") != isChecked) {
                listenerSwitch(data!![position], if (isChecked) "1" else "0", "switch")
            }
        }

        holder.itemBinding.btnDelete.setOnClickListener {
            listener(data!![position],"delete")
        }
        holder.itemBinding.btnEdit.setOnClickListener {
            listener(data!![position],"edit")
        }

        holder.itemBinding.btnDelete.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.DELETE_SEGMANT)) View.VISIBLE else View.GONE

        holder.itemBinding.btnEdit.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.EDIT_SEGMANT)) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemSegmentListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Segments) {

            itemBinding.txtName.text = data?.name
        }
    }
    fun setProducts(newProducts: List<Segments>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<Segments>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}