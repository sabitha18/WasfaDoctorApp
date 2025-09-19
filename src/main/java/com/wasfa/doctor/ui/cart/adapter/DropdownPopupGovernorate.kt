package com.wasfa.doctor.ui.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.GovernorateResponse

class DropdownPopupGovernorate(
    private val context: Context,
    private val anchorView: View,
    private val items: List<GovernorateResponse>?,
    private val onItemClickListener: (String, String,GovernorateResponse) -> Unit
) {
    private val popupWindow: PopupWindow = PopupWindow(context)

    init {
        val layoutInflater = LayoutInflater.from(context)
        val contentView = layoutInflater.inflate(R.layout.dropdown_popup, null)
        val listView = contentView.findViewById<ListView>(R.id.listViewDropdown)

        // Assuming items is a list of Governorate objects
        val governorateAdapter = GovernorateAdapter(context,
            (items ?: emptyList()) as List<GovernorateResponse>
        )
        listView.adapter = governorateAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = governorateAdapter.getItem(position)?.name ?: ""
            val selectedId = ""
            onItemClickListener.invoke(selectedItem, selectedId, items!![position])
            popupWindow.dismiss()
        }

        popupWindow.contentView = contentView
        popupWindow.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable = true
    }

    fun show() {
        popupWindow.showAsDropDown(anchorView)
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}

