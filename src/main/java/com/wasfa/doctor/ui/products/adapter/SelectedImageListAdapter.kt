package com.wasfa.doctor.ui.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.ItemImagesBinding
import com.wasfa.doctor.helper.PhotoMetaData

class SelectedImageListAdapter(
    private val CatList: List<PhotoMetaData>,
    private val listener: (PhotoMetaData, Int) -> Unit
) :
    RecyclerView.Adapter<SelectedImageListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])

        holder.itemBinding.btnDot.setOnClickListener { view ->
            val popupMenu = PopupMenu(holder.itemView.context, view)
            popupMenu.menu.add("Delete")

            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "Delete") {
                    listener(CatList[position], position)
                    true
                } else {
                    false
                }
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(var itemBinding: ItemImagesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: PhotoMetaData) {

            itemBinding.txtImageName.text = data?.name
            val sizeInMB = data?.sizeInBytes?.toDoubleOrNull()?.let {
                String.format("%.2f MB", it / (1024 * 1024))
            } ?: "0 MB"
            itemBinding.txtImageSize.text = sizeInMB


            Glide.with(itemBinding.root.context)
                .load(data?.fileUrl)
                .into(itemBinding.imgList)


        }
    }
}