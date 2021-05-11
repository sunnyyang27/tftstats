package com.e.tftstats.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.github.mikephil.charting.charts.LineChart

class PlacementFragment : StatsFragment() {
    override val title: String
        get() = "Placement"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        val lineChart = root.findViewById<LineChart>(R.id.stats_linechart)

        Helper.setupAxis(lineChart.axisLeft, 8f, 1f, 7, true)
        Helper.setupAxis(lineChart.axisRight, 8f, 1f, 7, true)

        return root
    }

    override fun dataPoint(placement: Int, stage: Int) : Double {
        return if (stageDao.hasStatPerPlacementAndStage(placement, stage) > 0)
            stageDao.getAvgPlacementPerPlacementAndStage(placement, stage)
        else
            -1.0
    }

    override fun avgDataPoint(stage: Int) : Double {
        return if (stageDao.hasStatPerStage(stage) > 0)
            stageDao.getAvgPlacementPerStage(stage)
        else
            -1.0
    }
}