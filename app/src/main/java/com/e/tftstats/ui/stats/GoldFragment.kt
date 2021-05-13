package com.e.tftstats.ui.stats

import com.e.tftstats.R

class GoldFragment : StatsFragment() {
    override val title: String
        get() = getString(R.string.menu_gold)

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