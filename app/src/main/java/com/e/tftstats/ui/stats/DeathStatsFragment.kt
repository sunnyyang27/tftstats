package com.e.tftstats.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import java.util.*

class DeathStatsFragment : Fragment() {
    val gameDao = MainActivity.db!!.gameDao()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_death_stats, container, false)

        // Create line chart
        val lineChart = root.findViewById<LineChart>(R.id.death_stats_linechart)
        Helper.designLineChart(lineChart)
        initializeData(lineChart)
        Helper.setupAxis(lineChart.axisLeft, 8f, 1f, 7, true)
        Helper.setupAxis(lineChart.axisRight, 8f, 1f, 7, true)
        val desc = Description()
        desc.text = resources.getString(R.string.death_stats_title)
        desc.textSize = 15f
        lineChart.description = desc

        return root
    }

    private fun initializeData(lineChart: LineChart) {
        val dataPoints = ArrayList<Entry>()
        val allGames = gameDao.getAll()
        for (game in allGames) {
            // Convert stage and round to decimal
            val x = game.stageDied + (game.roundDied.toDouble() / 7)
            val y = game.placement
            dataPoints.add(Entry(x.toFloat(), y.toFloat()))
        }

        val dataSet = Helper.createDataSet(dataPoints, "", R.color.steel_blue)
        dataSet.enableDashedLine(0f, 1f, 0f);
        Helper.setLineChartData(lineChart, listOf(dataSet))
    }
}