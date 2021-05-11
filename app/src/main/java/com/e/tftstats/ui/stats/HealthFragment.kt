package com.e.tftstats.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.github.mikephil.charting.charts.LineChart

class HealthFragment : StatsFragment() {
    override val title: String
        get() = "Health"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        val lineChart = root.findViewById<LineChart>(R.id.stats_linechart)

        // Y axis scale
        Helper.setupAxis(lineChart.axisLeft, 100f, 0f, 10)
        Helper.setupAxis(lineChart.axisRight, 100f, 0f, 10)

        return root
    }

    override fun dataPoint(placement: Int, stage: Int) : Double {
        return if (stageDao.hasStatPerPlacementAndStage(placement, stage) > 0)
            stageDao.getAvgHealthPerPlacementAndStage(placement, stage)
        else
            -1.0
    }

    override fun avgDataPoint(stage: Int) : Double {
        return if (stageDao.hasStatPerStage(stage) > 0)
            stageDao.getAvgHealthPerStage(stage)
        else
            -1.0
    }
}