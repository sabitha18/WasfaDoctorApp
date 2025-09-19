package com.wasfa.doctor.ui.images.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemMediaListBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.response.Uploads

class MediaListAdapter(
    private val context: Context,
    private val data: MutableList<Uploads>,
    private val listener: (Uploads, String?) -> Unit
) :
    RecyclerView.Adapter<MediaListAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1
    private var isUserSelection = false

    private var isMultipleSelection = false
    private val selectedItems = mutableSetOf<Int>()

    init {
        val appPref = AppPreferences.getInstance(context)
        isMultipleSelection = appPref.getImgSelectionStatus() == "multiple"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemMediaListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uploads = data[position]
        val context = holder.itemView.context
        val appPref = AppPreferences.getInstance(context)

        val isSelected = if (!isUserSelection) {
            when (appPref.getImgStatus()) {
                "banner" -> uploads.id == appPref.getCatImgId()
                "icon" -> uploads.id == appPref.getCatIconImgId()
                else -> false
            }
        } else {
            if (isMultipleSelection) selectedItems.contains(position)
            else position == selectedPosition
        }

        holder.bindItem(uploads, isSelected)

        holder.itemView.setOnClickListener {
            isUserSelection = true

            if (isMultipleSelection) {
                if (selectedItems.contains(position)) {
                    selectedItems.remove(position)
                } else {
                    selectedItems.add(position)
                }
                notifyItemChanged(position)
            } else {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
            }

            listener(data[position], data[position].file_original_name)
        }
    }


    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemMediaListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: Uploads, isSelected: Boolean) {

            Glide.with(itemBinding.root.context)
                .load(data?.url)
                .error(R.drawable.wasfa_logo)
                .into(itemBinding.imgMedia)

            itemBinding.txtMedia.text = data?.file_original_name
            itemBinding.cardMedia.strokeColor = if (isSelected) {
                ContextCompat.getColor(itemBinding.root.context, R.color.main_color) // selected
            } else {
                ContextCompat.getColor(itemBinding.root.context, R.color.white) // normal
            }


        }
    }
    fun setProducts(newProducts: List<Uploads>) {
        isUserSelection = false
        data.clear()
        data.addAll(newProducts)

        val appPref = AppPreferences.getInstance(context)
        val savedId = when (appPref.getImgStatus()) {
            "banner" -> appPref.getCatImgId()
            "icon" -> appPref.getCatIconImgId()
            else -> null
        }

        selectedPosition = data.indexOfFirst { it.id == savedId }

        notifyDataSetChanged()
    }


    fun addProducts(newProducts: List<Uploads>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
    fun getSelectedUploads(): List<Uploads> {
        return if (isMultipleSelection) {
            selectedItems.map { data[it] }
        } else {
            if (selectedPosition >= 0) listOf(data[selectedPosition]) else emptyList()
        }
    }

}