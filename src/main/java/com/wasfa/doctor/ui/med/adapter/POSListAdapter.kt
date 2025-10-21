package com.wasfa.doctor.ui.med.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.ItemPosListBinding
import com.wasfa.doctor.ui.model.Cat

class POSListAdapter(
    private val CatList: List<Cat>,
    private val listener: (Cat, Int) -> Unit
) :
    RecyclerView.Adapter<POSListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemPosListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])
        holder.itemView.setOnClickListener {
            listener(CatList!![position],position)
        }

    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemPosListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(cat: Cat) {

            itemBinding.txtTitle.text = cat?.name
        }
    }
}