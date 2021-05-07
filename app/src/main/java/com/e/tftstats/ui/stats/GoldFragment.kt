package com.e.tftstats.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class GoldFragment : StatsFragment() {
    override val title: String
        get() = "Gold"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)
//        val lineChart = root.findViewById<LineChart>(R.id.stats_linechart)
        return root
    }

    override fun dataPoint(placement: Int, stage: Int) : Double {
        return if (stageDao.hasStatPerPlacementAndStage(placement, stage) > 0)
            stageDao.getAvgGoldPerPlacementAndStage(placement, stage)
        else
            -1.0
    }

    override fun avgDataPoint(stage: Int) : Double {
        return if (stageDao.hasStatPerStage(stage) > 0)
            stageDao.getAvgGoldPerStage(stage)
        else
            -1.0
    }
}