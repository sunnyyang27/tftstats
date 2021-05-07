package com.e.tftstats.ui.stats

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import java.util.*
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.flexbox.FlexboxLayout

abstract class StatsFragment : Fragment() {
    private val dataSets: HashMap<Int, ILineDataSet> = HashMap()
    private var dataSetColors: List<Int> = listOf()
    protected var stageDao = MainActivity.db!!.stageDao()
    private var maxStage = stageDao.getMaxStage()
    abstract val title: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_stats, container, false)

        // Initialize variables
        dataSetColors = listOf(
            resources.getColor(R.color.pink, null),
            Color.RED,
            resources.getColor(R.color.orange, null),
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            resources.getColor(R.color.purple, null),
            resources.getColor(R.color.brown, null))

        // Add and Initialize checkboxes
        val checkboxLayout = root.findViewById<FlexboxLayout>(R.id.stats_placement_checks_layout)
        for (i in 1..9) {
            val checkbox = CheckBox(context)
            checkbox.text = if (i < 9) i.toString() else "Avg"
            checkbox.isChecked = true
            checkbox.buttonTintList = ColorStateList.valueOf(dataSetColors[i-1])
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                updateLineChart(i, isChecked)
            }
            if (i < 9) getData(i) else getAverageData()
            checkboxLayout.addView(checkbox)
        }

        // Create line chart
        val lineChart = root.findViewById<LineChart>(R.id.stats_linechart)
        Helper.designLineChart(lineChart)
        Helper.setLineChartData(lineChart, dataSets.values.toList())
        val desc = Description()
        desc.text = "$title per stage"
        desc.textSize = 15f
        lineChart.description = desc

        return root
    }

    private fun getData(placement: Int) {
        val dataPoints = ArrayList<Entry>()

        for (stage in 1..maxStage) {
            val point = dataPoint(placement, stage)
            if (point == -1.0) continue
            dataPoints.add(Entry(stage.toFloat(), point.toFloat()))
        }
        val dataSet = Helper.createDataSet(dataPoints, placement.toString(), dataSetColors[placement - 1])
        dataSets[placement] = dataSet
    }

    abstract fun dataPoint(placement: Int, stage: Int) : Double

    abstract fun avgDataPoint(stage: Int) : Double

    private fun getAverageData() {
        val dataPoints = ArrayList<Entry>()
        for (stage in 1..maxStage) {
            val point = avgDataPoint(stage)
            if (point == -1.0) continue
            dataPoints.add(Entry(stage.toFloat(), point.toFloat()))
        }
        val dataSet = Helper.createDataSet(dataPoints, "Avg", dataSetColors[8])
        dataSets[9] = dataSet
    }

    private fun updateLineChart(placement: Int, checked: Boolean) {
        dataSets[placement]!!.isVisible = checked
        Helper.setLineChartData(view!!.findViewById(R.id.stats_linechart), dataSets.values.toList())
    }
}