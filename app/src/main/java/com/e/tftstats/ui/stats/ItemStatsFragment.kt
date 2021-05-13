package com.e.tftstats.ui.stats

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
import com.e.tftstats.model.Item
import com.e.tftstats.model.ItemPlacementTuple
import kotlin.math.min

class ItemStatsFragment : Fragment() {

    private val stageDao = MainActivity.db!!.stageDao()
    private val maxStage = stageDao.getMaxStageWithItem()
    private val itemSize = min((MainActivity.screenWidth) / (maxStage + 1), 120)
    private lateinit var pveItemStageMap: Map<Pair<Int, Int>, Double> // (itemId, stage#), avgplacement
    private var pveStageMap =  HashMap<Int, Pair<Int, Double>>() // stage#, (itemId, avgplacement)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_item_stats, container, false)

        // Initialize PVE items
        initPvePlacement()

        // Section 1
        // Initialize auto-complete values
        createAutoCompleteTextView(root)

        // Section 2
        val section2Table = root.findViewById<TableLayout>(R.id.best_items_table)
        createHeaderRow(section2Table, "Item\\Stage")
        createSection2Row(section2Table, "Armory", stageDao::getArmoryItemWithMaxPlacementPerStage)
        createSection2Row(section2Table, "Carousel", stageDao::getCarouselItemWithMaxPlacementPerStage)
        createSection2Row(section2Table, "PVE", ::getPveItemWithMaxPlacementPerStage)

        return root
    }

    private fun initPvePlacement() {
        val stages = stageDao.getPveItemsWithFinalPlacement()
        val totalPlacementMap = HashMap<Pair<Int, Int>, Pair<Int, Int>>() // (itemId, stage#), (placement, count)
        for (stage in stages) {
            if (stage.pveItems.isNullOrBlank()) continue
            val items = stage.pveItems!!.split(",").map { it.toInt() }
            items.forEach { item ->
                val pairKey = Pair(item, stage.stageNumber)
                val pair = totalPlacementMap[pairKey]
                if (pair != null) {
                    totalPlacementMap[pairKey] = Pair(pair.first + stage.placement, pair.second + 1)
                } else {
                    totalPlacementMap[pairKey] = Pair(stage.placement, 1)
                }
            }
        }
        pveItemStageMap = totalPlacementMap
            .map { (key, value) -> key to value.first.toDouble() / value.second }
            .toMap()

        pveItemStageMap.forEach {
            val stage = it.key.second
            val pair = pveStageMap[stage]
            if (pair == null || it.value > pair.second) {
                pveStageMap[stage] = Pair(it.key.first, it.value)
            }
        }
    }

    private fun getAvgPlacementPerPveAndStage(itemId: Int, stageNumber: Int) : Double {
        val pairKey = Pair(itemId, stageNumber)
        return pveItemStageMap[pairKey] ?: 0.0
    }

    private fun getPveItemWithMaxPlacementPerStage(stageNumber: Int) : ItemPlacementTuple? {
        val result = pveStageMap[stageNumber]
        return if (result != null) {
            val tuple = ItemPlacementTuple()
            tuple.item = result.first
            tuple.avgPlacement = result.second
            tuple
        } else {
            null
        }
    }

    private fun createHeaderRow(table: TableLayout, title: String) {
        // Title Cell, then 1-max stage
        val row = Helper.createRow(context)
        val titleTv = createTextView(title, true)
        row.addView(titleTv)

        for (stage in 1..maxStage) {
            val tv = createTextView(stage.toString(), true)
            row.addView(tv)
        }
        table.addView(row)
    }

    private fun createTextView(text: String, isBold: Boolean = false) : TextView {
        val tv = TextView(context)
        tv.text = text
        tv.textSize = 20f
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
        if (isBold) tv.setTypeface(null, Typeface.BOLD)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = 10
        layoutParams.bottomMargin = 5
        tv.layoutParams = layoutParams
        return tv
    }

    private fun createAutoCompleteTextView(root: View) {
        val auto = root.findViewById<AutoCompleteTextView>(R.id.item_name)
        val itemAdapter = ArrayAdapter<Item>(requireContext(), android.R.layout.simple_list_item_1, Helper.getAllItems())
        auto.setAdapter(itemAdapter)
        auto.onItemClickListener = AdapterView.OnItemClickListener { arg0, _, arg2, _ ->
            val selected = arg0.adapter.getItem(arg2) as Item
            val table = root.findViewById<TableLayout>(R.id.placement_by_item_table)
            table.removeAllViews()
            createHeaderRow(table, "Placement\\Stage")
            createSection1Row(table, selected.id, "Armory", stageDao::getAvgPlacementPerArmoryAndStage)
            createSection1Row(table, selected.id, "Carousel", stageDao::getAvgPlacementPerCarouselAndStage)
            createSection1Row(table, selected.id, "PVE", ::getAvgPlacementPerPveAndStage)
        }
    }

    private fun createSection1Row(table: TableLayout, itemId: Int, itemType: String, getPlacement: (itemId: Int, stageNumber: Int) -> Double) {
        val row = Helper.createRow(context)
        val label = createTextView(itemType, true)
        row.addView(label)
        for (stage in 1..maxStage) {
            val itemPlacement = getPlacement(itemId, stage)
            if (itemPlacement == 0.0) {
                val tv = createTextView("N/A")
                row.addView(tv)
                continue
            }
            val placement = createTextView(Helper.formatStat(itemPlacement))
            row.addView(placement)
        }
        table.addView(row)
    }

    private fun createSection2Row(table: TableLayout, itemType: String, getItem: (stageNumber: Int) -> ItemPlacementTuple?) {
        val row = Helper.createRow(context)
        val label = createTextView(itemType, true)
        row.addView(label)
        for (stage in 1..maxStage) {
            val itemPlacement = getItem(stage)
            if (itemPlacement == null || itemPlacement.item == -1) {
                val tv = createTextView("N/A")
                row.addView(tv)
                continue
            }
            val layoutParams = TableRow.LayoutParams(itemSize, TableRow.LayoutParams.WRAP_CONTENT)
            layoutParams.marginEnd = 3
            layoutParams.bottomMargin = 3
            val item = Helper.getItem(itemPlacement.item)
            val iv = Helper.createImageView(context, item.imagePath, layoutParams,
                "${item.name}, ${Helper.formatStat(itemPlacement.avgPlacement)}")
            row.addView(iv)
        }
        table.addView(row)
    }
}