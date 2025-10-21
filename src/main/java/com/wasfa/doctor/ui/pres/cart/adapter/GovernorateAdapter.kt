package com.wasfa.doctor.ui.pres.cart.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.GovernorateResponse

class GovernorateAdapter(context: Context, governorates: List<GovernorateResponse>) :
    ArrayAdapter<GovernorateResponse>(context, R.layout.dropdown_item, governorates) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val governorate = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.dropdown_item, parent, false)

        val textViewItem = view.findViewById<TextView>(R.id.textViewItem)
        textViewItem.text = governorate?.name ?: ""

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
