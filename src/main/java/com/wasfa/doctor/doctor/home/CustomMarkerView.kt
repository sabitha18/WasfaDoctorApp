package com.wasfa.doctor.doctor.home

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.wasfa.doctor.R

class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val xLabels: List<String>
) : MarkerView(context, layoutResource) {

    private val tvDate: TextView = findViewById(R.id.tvDate)
    private val tvValue: TextView = findViewById(R.id.tvValue)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            val xIndex = e.x.toInt()
            val dateLabel = if (xIndex in xLabels.indices) xLabels[xIndex] else ""
            val yValue = e.y.toInt()
            tvDate.text = dateLabel
            tvValue.text = "Number of Rx : $yValue"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}

