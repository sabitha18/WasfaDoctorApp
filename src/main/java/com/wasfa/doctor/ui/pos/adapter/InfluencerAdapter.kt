package com.wasfa.doctor.ui.pos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.wasfa.doctor.R
import com.wasfa.doctor.network.response.InfluencerListResponse

class InfluencerAdapter(
    context: Context,
    private var influencerList: List<InfluencerListResponse>
) : ArrayAdapter<InfluencerListResponse>(context, R.layout.dropdown_item, influencerList), Filterable {

    private var originalList: List<InfluencerListResponse> = influencerList.toList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val influencer = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.dropdown_item, parent, false)

        val textViewItem = view.findViewById<TextView>(R.id.textViewItem)
        textViewItem.text = influencer?.name ?: ""

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase() ?: ""
                val filtered = if (query.isEmpty()) {
                    originalList
                } else {
                    originalList.filter { it.name?.lowercase()?.contains(query) == true }
                }
                return FilterResults().apply { values = filtered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                influencerList = results?.values as? List<InfluencerListResponse> ?: emptyList()
                clear()
                addAll(influencerList)
                notifyDataSetChanged()
            }
        }
    }
}
