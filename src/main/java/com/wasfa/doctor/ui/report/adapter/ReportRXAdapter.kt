package com.wasfa.doctor.ui.report.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemReportBinding
import com.wasfa.doctor.databinding.ItemReportRxBinding
import com.wasfa.doctor.network.response.ListRX
import com.wasfa.doctor.network.response.Report

class ReportRXAdapter(
    private val data: MutableList<ListRX>,
    private val editStatus: String,
    private val listener: (ListRX, String) -> Unit,
) :
    RecyclerView.Adapter<ReportRXAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemReportRxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])

        holder.itemBinding.imgEdit.setOnClickListener {
            listener(data[position],"edit")
        }
        holder.itemBinding.imgView.setOnClickListener {
            listener(data[position],"view")
        }

        if (data[position].status_order == "Partially Ordered" || data[position].status_order == "Ordered"){
            holder.itemBinding.imgEdit.visibility = View.GONE
        }else{
            holder.itemBinding.imgEdit.visibility = View.VISIBLE
        }
//
//        if (editStatus == "false"){
//            holder.itemBinding.imgEdit.visibility = View.VISIBLE
//        }else{
//            holder.itemBinding.imgEdit.visibility = View.VISIBLE
//        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemReportRxBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: ListRX) {
            itemBinding.txtOrderId.text = data?.prescription_id
            itemBinding.txtOrderDate.text = data?.date
            itemBinding.txtItemName.text = data?.customer
            itemBinding.txtItemCount.text = data?.product_count
            itemBinding.txtStatus.text = data?.status_order
            if (data?.status_order == "Pending"){
                itemBinding.txtStatus.setTextColor(Color.parseColor("#000000"))

            }else{
                itemBinding.txtStatus.setTextColor(Color.parseColor("#34c0f2"))
            }


            if (data?.edited == "false"){
                itemBinding.txtOrderEditedStatus.visibility = View.INVISIBLE
            }else{
                itemBinding.txtOrderEditedStatus.visibility = View.VISIBLE
            }

        }
    }

    fun setProducts(newProducts: List<ListRX>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<ListRX>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}