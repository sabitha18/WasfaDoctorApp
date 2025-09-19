package com.wasfa.doctor.ui.influencer.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemSellerProductBinding
import com.wasfa.doctor.network.response.SCProducts
import android.os.Handler
import android.os.Looper



class InfluencerProductAdapter(
    private val data: MutableList<SCProducts>,
    private val listener: (SCProducts, String) -> Unit,
) :
    RecyclerView.Adapter<InfluencerProductAdapter.ViewHolder>() {
    private var debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemSellerProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position])
        val item = data[position]
        holder.itemBinding.edtComValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceHandler.removeCallbacks(debounceRunnable ?: Runnable { })

                val input = s.toString()

                debounceRunnable = Runnable {
                    // Call the listener with delayed input
                    listener(item, input)
                }

                // Delay for 500ms
                debounceHandler.postDelayed(debounceRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemSellerProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: SCProducts) {

            Glide.with(itemBinding.root.context)
                .load(data?.thumbnail_image)
                .into(itemBinding.imgProduct)

            itemBinding.txtStoreName.text = data?.sellerName
            itemBinding.txtProductName.text = data?.name
            itemBinding.txtApixMargin.text = data?.apixMargin
            itemBinding.txtBasePrice.text = data?.basePrice
            itemBinding.txtInfluencerMargin.text = data?.influencerrMargin
            itemBinding.txtSellerDiscount.text = data?.sellerDiscount
            itemBinding.txtCog.text = data?.cog
            itemBinding.edtComValue.setText(data?.commissionValue)



        }
    }
    fun setProducts(newProducts: List<SCProducts>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<SCProducts>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}