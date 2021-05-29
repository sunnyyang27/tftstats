package com.e.tftstats.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Stage
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry

class GameStatsFragment : Fragment() {
    private val stageDao = MainActivity.db!!.stageDao()
    private lateinit var root: View
    private var gameId: Int = -1
    private var currentChartId = R.id.gold_chart
    private val dataPoints: Array<ArrayList<Entry>> = arrayOf(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())
    private lateinit var stages: List<Stage>
    private var itemSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_game_stats, container, false)
        gameId = arguments!!.getInt("gameId", -1)
        itemSize = resources.getDimensionPixelSize(R.dimen.item_size)

        // Placement
        val placement = arguments!!.getInt("placement", 0)
        val placementTv = root.findViewById<TextView>(R.id.game_placement)
        placementTv.text = getString(R.string.placement_game, Helper.getPlacement(placement, resources, placementTv))

        // Initialize radio buttons
        createRadioButtonListener(R.id.gold_radio, R.id.gold_chart)
        createRadioButtonListener(R.id.health_radio, R.id.health_chart)
        createRadioButtonListener(R.id.level_radio, R.id.level_chart)
        createRadioButtonListener(R.id.placement_radio, R.id.placement_chart)

        // Get data
        stages = stageDao.getByGame(gameId)
        for (stage in stages) {
            dataPoints[0].add(Entry(stage.stageNumber.toFloat(), stage.gold.toFloat()))
            dataPoints[1].add(Entry(stage.stageNumber.toFloat(), stage.health.toFloat()))
            dataPoints[2].add(Entry(stage.stageNumber.toFloat(), Helper.getLevelXp(stage.level, stage.xp).toFloat()))
            dataPoints[3].add(Entry(stage.stageNumber.toFloat(), stage.placement.toFloat()))
        }

        // Create charts
        createLineChart(R.id.gold_chart, "Gold", dataPoints[0], resources.getColor(R.color.gold, null))
        createLineChart(R.id.health_chart, "Health", dataPoints[1], Color.RED, 100f, 0f, 10)
        createLineChart(R.id.level_chart, "Level", dataPoints[2], resources.getColor(R.color.steel_blue, null), 9f, 3f, 6)
        createLineChart(R.id.placement_chart, "Placement", dataPoints[3], resources.getColor(R.color.orchid, null), 8f, 1f, 7)

        // Item stats
        val itemTable = root.findViewById<TableLayout>(R.id.game_item_table)
        for (stage in stages) {
            val row = Helper.createRow(context, 5)
            val label = createTextView(stage.stageNumber.toString(), true)
            row.addView(label)
            // Armory and carousel items
            row.addView(createItem(stage.armoryItem))
            row.addView(createItem(stage.carouselItem))
            // Pve
            val pve = if (stage.pveItems.isBlank()) emptyList() else stage.pveItems.split(",").map { it.toInt() }
            val linearLayout = LinearLayout(context)
            linearLayout.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val layoutParams = LinearLayout.LayoutParams(itemSize, itemSize)
            layoutParams.marginEnd = 5
            for (itemId in pve) {
                if (itemId == -1) continue
                val item = Helper.getItem(itemId)
                val iv = Helper.createImageView(context, item.imagePath, layoutParams, item.name)
                linearLayout.addView(iv)
            }
            row.addView(linearLayout)
            itemTable.addView(row)
        }
        return root
    }

    private fun createRadioButtonListener(btnId: Int, chartId: Int = 0) {
        val btn = root.findViewById<RadioButton>(btnId)
        val chart = root.findViewById<LineChart>(chartId)
        btn.setOnClickListener {
            root.findViewById<LineChart>(currentChartId).visibility = View.GONE
            if (chart != null) {
                chart.visibility = View.VISIBLE
                currentChartId = chartId
            }
        }
    }

    private fun createLineChart(chartId: Int, title: String, data: List<Entry>, color: Int, max: Float = 0f, min: Float = 0f, labelCount: Int = 0) {
        val chart = root.findViewById<LineChart>(chartId)
        Helper.designLineChart(chart)
        val dataSet = Helper.createDataSet(data, title, color)
        Helper.setLineChartData(chart, listOf(dataSet))
        val desc = Description()
        desc.text = "$title per stage"
        desc.textSize = 15f
        chart.description = desc

        // Avoid setting gold chart
        if (max != min || max != 0f) {
            Helper.setupAxis(chart.axisLeft, max, min, labelCount, chartId == R.id.placement_chart)
            Helper.setupAxis(chart.axisRight, max, min, labelCount, chartId == R.id.placement_chart)
        }
    }

    private fun createTextView(text: String, isLabel: Boolean = false) : TextView {
        val tv = TextView(context)
        tv.text = text
        tv.textSize = 20f
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = 10
        if (isLabel) {
            tv.setTypeface(null, Typeface.BOLD)
            layoutParams.bottomMargin = 5
        }
        tv.layoutParams = layoutParams
        return tv
    }

    private fun createItem(itemId: Int) : View {
        return if (itemId == -1) {
            createTextView("N/A")
        } else {
            val layoutParams = TableRow.LayoutParams(itemSize, itemSize)
            val item = Helper.getItem(itemId)
            Helper.createImageView(context, item.imagePath, layoutParams, item.name)
        }
    }
}