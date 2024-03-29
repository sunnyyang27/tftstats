package com.e.tftstats.ui.stats

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.util.keyIterator
import androidx.core.util.set
import androidx.core.util.valueIterator
import androidx.fragment.app.Fragment
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Champion
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Helper.Companion.formatStat
import com.e.tftstats.model.SpatItem
import com.e.tftstats.model.Trait
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlin.math.min
import kotlin.math.pow

class FinalCompStatsFragment : Fragment() {

    private val gameDao = MainActivity.db!!.gameDao()
    private val teamDao = MainActivity.db!!.teamDao()
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_final_comp_stats, container, false)

        // Section 1
        createAutoCompleteTextView(R.id.trait_name, Helper.getTraits(), R.id.placement_by_trait_table, ::onClickSection1)

        // Section 2
        section2Table()

        // Section 3
        createAutoCompleteTextView(R.id.champion_name, Helper.getChampionsById(teamDao.getUniqueChampIds()), R.id.placement_by_champion_table, ::onClickSection3)

        // Section 4
        section4Table()

        // Section 5
        section5Chart()
        return root
    }

    private fun onClickSection1(selected: Trait, table: TableLayout) {
        // Calculate everything
        val statsMap = SparseArray<Pair<Int, Int>>()
        // Iterate through all games
        val games = gameDao.getAll()
        for (game in games) {
            // Get all champs in the game
            val champions = teamDao.getTeamByGame(game.id)
            // Only include those that match the trait
            val numChamps = champions.filter { team ->
                val champ = Helper.getChampion(team.champId)
                if (champ.origins.contains(selected.origin))
                    true
                else {
                    // Spat item check
                    val items = team.items.split(",").map { if (it.toInt() == -1) null else Helper.getItem(it.toInt()) }
                        .filter { item -> item != null && item is SpatItem && item.origin == selected.origin }
                    items.isNotEmpty()
                }
            }.size
            for (level in selected.levels.reversedArray()) {
                if (numChamps >= level) {
                    // Add or update map
                    val pair = statsMap[level]
                    if (pair != null) {
                        statsMap[level] = Pair(pair.first + 1, pair.second + game.placement)
                    } else {
                        statsMap[level] = Pair(1, game.placement)
                    }
                    break
                }
            }
        }
        // Create header row
        table.addView(createHeaderRow(arrayOf("", "Count", "Placement")))
        // Create row per trait level
        for ((i, level) in selected.levels.withIndex()) {
            val pair = statsMap[level]
            val statsRow = createRow(
                arrayOf(pair?.first?.toString() ?: "N/A", pair?.second?.toString() ?: "N/A")
            )
            // Add trait image
            val imageParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            val traitImage = Helper.createImageView(context, selected.imagePath, imageParams, level.toString())
            traitImage.imageTintList = ColorStateList.valueOf(resources.getColor(selected.traitColor(i), null))
            statsRow.addView(traitImage, 0)
            table.addView(statsRow)
        }
    }

    private fun section2Table() {
        val statsMap = HashMap<Pair<Champion.Origin, Int>, Pair<Int, Int>>()    // Origin, Trait level index, Placement, Count
        // Iterate through all games
        val games = gameDao.getAll()
        for (game in games) {
            // Get all champs in the game
            val teamComp = teamDao.getTeamByGame(game.id)
            // Track members per trait
            val traitMap = Helper.calculateTeamsTraits(teamComp)
            // For each trait, calculate its trait levels
            for (trait in traitMap) {
                val levels = Helper.getTrait(trait.key).levels
                val numLevels = levels.size
                for (i in numLevels - 1 downTo 0) {
                    if (trait.value >= levels[i]) {
                        // Add or update map
                        val pairKey = Pair(trait.key, i)
                        val pair = statsMap[pairKey]
                        if (pair != null) {
                            statsMap[pairKey] = Pair(pair.first + game.placement, pair.second + 1)
                        } else {
                            statsMap[pairKey] = Pair(game.placement, 1)
                        }
                        break
                    }
                }
            }
        }
        // toList returns Pair<key, Pair<Int, Int>>
        val custom = Comparator<Pair<Pair<Champion.Origin, Int>, Pair<Int, Int>>> { a, b ->
            val traita = Helper.getTrait(a.first.first)
            val traitb = Helper.getTrait(b.first.first)
            val alevel = traita.levels[a.first.second]
            val blevel = traitb.levels[b.first.second]
            when {
                // Placement
                (a.second.first < b.second.first) -> -1
                (a.second.first > b.second.first) -> 1
                // Count
                (a.second.second > b.second.second) -> -1
                (a.second.second < b.second.second) -> 1
                // Trait level
                (alevel > blevel) -> -1
                (alevel < blevel) -> 1
                else -> 0
            }
        }
        val sortedMap = statsMap.toList().sortedWith(custom).toMap()
        val top3 : MutableList<Pair<Champion.Origin, Int>> = ArrayList()                // origin, trait level index
        top3.addAll(sortedMap.keys.toList().subList(0, min(3, sortedMap.size)))

        val table = root.findViewById<TableLayout>(R.id.best_traits_table)
        // Header
        table.addView(createHeaderRow(arrayOf("", "Count", "Placement")))
        for (pair in top3) {
            val trait = Helper.getTrait(pair.first)
            val traitLevel = trait.levels[pair.second]
            val stats = arrayOf(
                statsMap[pair]!!.second.toString(),         // count
                statsMap[pair]!!.first.toString())          // placement
            val statsRow = createRow(stats)
            // Add trait image
            val imageParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            val traitImage = Helper.createImageView(context, trait.imagePath, imageParams,
                "${Helper.originName(pair.first)} $traitLevel")
            traitImage.imageTintList = ColorStateList.valueOf(resources.getColor(trait.traitColor(pair.second), null))
            statsRow.addView(traitImage, 0)
            table.addView(statsRow)
        }
    }

    private fun onClickSection3(selected: Champion, table: TableLayout) {
        // Create header row
        table.addView(createHeaderRow(arrayOf("Count", "Placement", "Star level", "Carry", "Items")))
        // Get data
        val champStats = teamDao.getAvgStatsByChamp(selected.id)
        // Create row: count, avg placement, avg star level, and how often it was the carry (percent)
        val statsRow = createSection4Row(selected.id, arrayOf(champStats.count.toString(), formatStat(champStats.avgPlacement),
            formatStat(champStats.avgStarLevel), formatStat(champStats.carryCount * 100 / champStats.count.toDouble()) + "%"),
            getMostSuccessfulItems(selected.id), false)
        table.addView(statsRow)
        val champImage = requireView().findViewById<ImageView>(R.id.champion_image)
        champImage.setImageResource(selected.imagePath)
    }

    private fun section4Table() {
        val table = root.findViewById<TableLayout>(R.id.best_champions_table)
        // Header
        table.addView(createHeaderRow(arrayOf("", "Count", "Placement", "Star level", "Carry", "Items")))
        val top3 = teamDao.getAvgChampStatsTopThree()
        for (champStats in top3) {
            val statsRow = createSection4Row(champStats.champId,
                arrayOf(champStats.count.toString(), formatStat(champStats.avgPlacement),
                formatStat(champStats.avgStarLevel), formatStat(champStats.carryCount * 100 / champStats.count.toDouble()) + "%"),
                getMostSuccessfulItems(champStats.champId), true)
            table.addView(statsRow)
        }
    }

    private fun createSection4Row(champId: Int, row: Array<String>, items: List<Int>, includeChampImage: Boolean) : TableRow {
        // 1. Stats Row
        val statsRow = createRow(row)
        // 2. Champ Image
        if (includeChampImage) {
            val imageParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            val champion = Helper.getChampion(champId)
            val champImage = Helper.createImageView(context, champion.imagePath, imageParams, champion.name)
            statsRow.addView(champImage, 0)
        }
        // 3. Champ items
        if (items.isNotEmpty()) {
            val linearLayout = LinearLayout(context)
            linearLayout.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val layoutParams = TableRow.LayoutParams(50, 50)
            layoutParams.marginEnd = 3
            for (itemId in items) {
                if (itemId == -1) continue
                val item = Helper.getItem(itemId)
                val iv = Helper.createImageView(context, item.imagePath, layoutParams, item.name)
                linearLayout.addView(iv)
            }
            statsRow.addView(linearLayout)
        }
        return statsRow
    }

    private fun section5Chart() {
        val teams = teamDao.getChampCostAndPlacement()
        // Calculate team cost per game
        val teamCostMap = SparseArray<Pair<Int, Int>>() // key: gameId; teamcost, placement
        for (team in teams) {
            val champ = Helper.getChampion(team.champId)
            val cost = champ.cost * 3.0.pow(team.starLevel - 1).toInt()
            val pair = teamCostMap[team.gameId]
            if (pair != null) {
                teamCostMap[team.gameId] = Pair(pair.first + cost, pair.second)
            } else {
                teamCostMap[team.gameId] = Pair(cost, team.placement)
            }
        }
        // Get total team costs per placement
        val totalCostPlacement = SparseArray<Pair<Int, Int>>() // key: placement; totalcost, count
        for (teamCost in teamCostMap.valueIterator()) {
            val pair = totalCostPlacement[teamCost.second]
            if (pair != null) {
                totalCostPlacement[teamCost.second] = Pair(pair.first + teamCost.first, pair.second + 1)
            } else {
                totalCostPlacement[teamCost.second] = Pair(teamCost.first, 1)
            }
        }
        // Calculate average team cost per placement and create data points
        val dataPoints = ArrayList<Entry>()
        for (placement in totalCostPlacement.keyIterator()) {
            val cost = totalCostPlacement[placement]
            val avgCost = cost.first.toFloat() / cost.second
            dataPoints.add(Entry(placement.toFloat(), avgCost))
        }
        // Create line chart
        val dataSet = Helper.createDataSet(dataPoints, "Avg team cost", Color.BLUE)
        val lineChart = root.findViewById<LineChart>(R.id.avg_team_cost_linechart)
        Helper.designLineChart(lineChart)
        Helper.setLineChartData(lineChart, listOf(dataSet))
        lineChart.description = null
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
        layoutParams.bottomMargin = 3
        tv.layoutParams = layoutParams
        return tv
    }

    private fun createRow(columns: Array<String>? = null, rowLabel: Boolean = false) : TableRow {
        val row = Helper.createRow(context, 5)
        var isBold = rowLabel
        if (columns == null)
            return row
        for (column in columns) {
            val tv = createTextView(column, isBold)
            if (isBold) isBold = false
            row.addView(tv)
        }
        return row
    }

    private fun createHeaderRow(columns: Array<String>) : TableRow {
        val row = createRow()

        for (column in columns) {
            val tv = createTextView(column, true)
            row.addView(tv)
        }
        return row
    }

    private fun <T>createAutoCompleteTextView(autoId: Int, adapterItems: List<T>, tableId: Int, onClick: (selected: T, table: TableLayout) -> Unit) {
        val auto = root.findViewById<AutoCompleteTextView>(autoId)
        val adapter = ArrayAdapter<T>(requireContext(), android.R.layout.simple_list_item_1, adapterItems)
        auto.setAdapter(adapter)
        auto.onItemClickListener = AdapterView.OnItemClickListener { arg0, _, arg2, _ ->
            @Suppress("UNCHECKED_CAST") val selected = arg0.adapter.getItem(arg2) as T
            val table = root.findViewById<TableLayout>(tableId)
            table.removeAllViews()
            onClick(selected, table)
        }
    }

    @SuppressLint("UseSparseArrays")
    private fun getMostSuccessfulItems(champId: Int) : List<Int> {
        val top3 : MutableList<Int> = ArrayList()
        val itemsTuple = teamDao.getItemsAndPlacementByChamp(champId)
        val itemCount = HashMap<Int, Pair<Int, Int>>() // itemId, <placement, count>
        for (itemEntry in itemsTuple) {
            val itemsString = itemEntry.items
            if (itemsString == null || itemsString.isEmpty()) continue
            // Exclude -1
            val items = itemsString.split(",").map {
                if (it.isEmpty()) -1
                else it.toInt()
            }
            for (item in items) {
                if (item == -1) continue
                val pair = itemCount[item]
                if (pair != null) {
                    itemCount[item] = Pair(pair.first + itemEntry.placement, pair.second + 1)
                } else {
                    itemCount[item] = Pair(itemEntry.placement, 1)
                }
            }
        }

        val sortedMap = itemCount.map { (key, value) -> key to value.first.toDouble() / value.second }
            .sortedByDescending { (_, value) -> value }.toMap()
        top3.addAll(sortedMap.keys.toList().subList(0, min(3, sortedMap.size)))
        return top3
    }
}