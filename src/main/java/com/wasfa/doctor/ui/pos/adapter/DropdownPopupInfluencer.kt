package com.wasfa.doctor.ui.pos.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.InfluencerListResponse

class DropdownPopupInfluencer(
    private val context: Context,
    private val anchorView: View,
    private val items: List<InfluencerListResponse>?,
    private val onItemClickListener: (String, String,InfluencerListResponse) -> Unit
) {
    private val popupWindow: PopupWindow = PopupWindow(context)

    init {
        val layoutInflater = LayoutInflater.from(context)
        val contentView = layoutInflater.inflate(R.layout.dropdown_popup, null)
        val listView = contentView.findViewById<ListView>(R.id.listViewDropdown)
        val searchEditText = contentView.findViewById<EditText>(R.id.edit_search)


        // Assuming items is a list of Governorate objects
        val governorateAdapter = InfluencerAdapter(context,
            (items ?: emptyList()) as List<InfluencerListResponse>
        )
        listView.adapter = governorateAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = governorateAdapter.getItem(position)?.name ?: ""
            val selectedId = ""
            onItemClickListener.invoke(selectedItem, selectedId, items!![position])
            popupWindow.dismiss()
        }
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                governorateAdapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        popupWindow.contentView = contentView
        popupWindow.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable = true

        popupWindow.isFocusable = true
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
    }

    fun show() {
        popupWindow.showAsDropDown(anchorView)
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}

