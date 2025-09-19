package com.wasfa.doctor.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.databinding.RxCartDetailItemBinding
import com.wasfa.doctor.ui.home.model.Cat

class CartRXDetailAdapter(
    private val CatList: List<Cat>,
    private val listener: (Cat, Int) -> Unit
) :
    RecyclerView.Adapter<CartRXDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = RxCartDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ViewHolder(var itemBinding: RxCartDetailItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(cat: Cat) {
        }
    }
}