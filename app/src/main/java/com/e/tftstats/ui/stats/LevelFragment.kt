package com.e.tftstats.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.e.tftstats.model.LevelTuple
import com.github.mikephil.charting.charts.LineChart

class LevelFragment : StatsFragment() {
    override val title: String
        get() = "Level"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        val lineChart = root.findViewById<LineChart>(R.id.stats_linechart)

        Helper.setupAxis(lineChart.axisLeft, 9f, 3f, 6)
        Helper.setupAxis(lineChart.axisRight, 9f, 3f, 6)

        return root
    }

    override fun dataPoint(placement: Int, stage: Int) : Double {
        if (stageDao.hasStatPerPlacementAndStage(placement, stage) == 0)
            return -1.0
        val levels: List<LevelTuple> = stageDao.getLevelAndXpPerPlacementAndStage(placement, stage)
        return calculateDataPoint(levels)
    }

    override fun avgDataPoint(stage: Int) : Double {
        if (stageDao.hasStatPerStage(stage) == 0)
            return -1.0
        val levels: List<LevelTuple> = stageDao.getLevelAndXpPerStage(stage)
        return calculateDataPoint(levels)
    }

    private fun calculateDataPoint(levels: List<LevelTuple>) : Double {
        var sum = 0.0
        for (level in levels) {
            sum += Helper.getLevelXp(level.level, level.xp)
        }
        return sum / levels.size
    }
}