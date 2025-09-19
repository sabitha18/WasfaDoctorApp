package com.wasfa.doctor.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.SummeryRxCartItemBinding
import com.wasfa.doctor.network.response.CartItem

class SummeryRXAdapter(
    private val CatList: List<CartItem>?,
    private val listener: (CartItem, Int) -> Unit,

) :
    RecyclerView.Adapter<SummeryRXAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = SummeryRxCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])
        holder.itemBinding.imgDelete.setOnClickListener {
            listener(CatList!![position],position)
        }


    }




    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(
        var itemBinding: SummeryRxCartItemBinding,
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: CartItem) {
            Glide.with(itemBinding.root.context)
                .load(data?.productThumbnailImage)
                .into(itemBinding.imgProduct)

            itemBinding.txtCount.text = "Qty: "+ data?.quantity.toString()
            itemBinding.txtPdName.text = data?.productName

            itemBinding.txtDoseDetails.text =
                "Dose: ${(data?.dose ?: "")}, " +
                        "Dose Day: ${(data?.doseday ?: "")}, " +
                        "Dose Time: ${(data?.dose_time ?: "")}, " +
                        "Course Duration: ${(data?.course_duration ?: "")}, " +
                        "Course Day: ${(data?.course_day ?: "")}"



        }

    }
}