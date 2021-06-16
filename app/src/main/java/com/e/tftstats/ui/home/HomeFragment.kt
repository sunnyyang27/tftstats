package com.e.tftstats.ui.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.*
import com.e.tftstats.ui.game.GameModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.*

class HomeFragment : Fragment() {

    private lateinit var gameDao: GameDao
    private lateinit var teamDao: TeamDao
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        gameDao = MainActivity.db!!.gameDao()
        teamDao = MainActivity.db!!.teamDao()

        val createGame = root.findViewById<FloatingActionButton>(R.id.new_game)
        createGame.setOnClickListener {
            MainActivity.currentGame = GameModel()
            root.findNavController().navigate(R.id.nav_stage)
        }

        // Avg placement
        updateAvgPlacement()

        // Avg stage of death
        updateAvgDeath()

        // Last 10 games
        val games = gameDao.getAll()
        for (i in 0 until min(10, games.size)) {
            createGameRow(games[i])
        }

        // Set scrollview's max height
        val bigScroll = root.findViewById<ScrollView>(R.id.games_scroll)
        val constraintLayout = root.findViewById<ConstraintLayout>(R.id.parent_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        root.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    //Remove the listener before proceeding
                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // measure your views here
                    val distance = Helper.measureDistance(bigScroll, root.findViewById(R.id.new_game))
                    constraintSet.constrainMaxHeight(R.id.games_scroll, distance)
                    constraintSet.applyTo(constraintLayout)
                }
            }
        )

        return root
    }

    // placement, table, buttons
    private fun createGameRow(game: Game) {
        // Horizontal linear layout
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setOnClickListener {
            val args = Bundle()
            args.putInt("gameId", game.id)
            args.putInt("placement", game.placement)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_game_stats, args)
        }

        // 1. Placement
        val placement = TextView(context)
        placement.textSize = 30f
        placement.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        placement.setTextAppearance(R.style.TextAppearance_AppCompat_Title)
        placement.text = Helper.getPlacement(game.placement, resources, placement)
        val placementParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        placementParams.marginEnd = 20
        placement.layoutParams = placementParams

        // 2.1 Three rows for stars, champion images, and items
        val starRow = Helper.createRow(context, 3)
        val championRow = Helper.createRow(context, 3)
        val itemRow = Helper.createRow(context, 3)

        val champWidth = 150
        val teamComp = teamDao.getTeamByGame(game.id)
        // Sort by cost then number of items
        val custom = Comparator<Team> { a, b ->
            val champACost = Helper.getChampion(a.champId).cost * 3.0.pow(a.starLevel - 1)
            val champBCost = Helper.getChampion(b.champId).cost * 3.0.pow(b.starLevel - 1)
            val champAItems = a.items.split(",").size
            val champBItems = b.items.split(",").size
            when {
                // Cost
                (champACost > champBCost) -> -1
                (champACost < champBCost) -> 1
                // Number of items
                (champAItems > champBItems) -> -1
                (champAItems < champBItems) -> 1
                else -> 0
            }
        }
        val sortedTeamComp = teamComp.sortedWith(custom)
        for (team in sortedTeamComp) {
            val champion = Helper.getChampion(team.champId)

            // Stars: horizontal
            val starsLayout = LinearLayout(context)
            starsLayout.orientation = LinearLayout.HORIZONTAL
            starsLayout.gravity = Gravity.CENTER_HORIZONTAL
            for (i in 1..team.starLevel) {
                val starParams = LinearLayout.LayoutParams(champWidth / 3, LinearLayout.LayoutParams.WRAP_CONTENT)
                val star = Helper.createImageView(root.context, R.drawable.ic_star, starParams)
                starsLayout.addView(star)
            }
            starRow.addView(starsLayout)

            // Champion image
            val imageParams = TableRow.LayoutParams(champWidth, TableRow.LayoutParams.WRAP_CONTENT)
            imageParams.marginEnd = 3
            val image = Helper.createImageView(context, champion.imagePath, imageParams)
            var color = R.color.gray
            when (champion.cost) {
                2 -> {
                    color = R.color.dark_cyan
                }
                3 -> {
                    color = R.color.steel_blue
                }
                4 -> {
                    color = R.color.orchid
                }
                5 -> {
                    color = R.color.golden_rod
                }
            }
            image.setBackgroundColor(resources.getColor(color, null))
            image.setPadding(5, 5, 5, 5)
            championRow.addView(image)

            // Items: horizontal
            val itemsLayout = LinearLayout(context)
            itemsLayout.orientation = LinearLayout.HORIZONTAL
            itemsLayout.gravity = Gravity.CENTER_HORIZONTAL
            val items = team.items.split(",")
            for (item in items) {
                if (item.isBlank() || item == "-1") continue
                val layoutParams = LinearLayout.LayoutParams((champWidth - 6)/ 3, (champWidth - 6)/ 3)
                layoutParams.marginEnd = 3
                val itemImage = Helper.createImageView(context, Helper.getItem(item.toInt()).imagePath, layoutParams)
                itemsLayout.addView(itemImage)
            }
            itemRow.addView(itemsLayout)
        }

        // Add rows to table
        val gameTable = TableLayout(context)
        gameTable.addView(starRow)
        gameTable.addView(championRow)
        gameTable.addView(itemRow)

        // 2.2 Traits: sort by level, then add to row
        val traitLayout = LinearLayout(context)
        traitLayout.orientation = LinearLayout.HORIZONTAL
        traitLayout.gravity = Gravity.CENTER_VERTICAL
        val traitMap = Helper.calculateTeamsTraits(teamComp)
        val traitImageMap = HashMap<ImageView, Pair<Int, Int>>() // ImageView, (levelRank, actualLevel)
        for (origin in traitMap) {
            if (origin.key == Champion.Origin.GODKING && origin.value > 1) continue
            val trait = Helper.getTrait(origin.key)
            val numLevels = trait.levels.size
            val levels = trait.levels
            for (i in numLevels - 1 downTo 0) {
                if (origin.value >= levels[i]) {
                    // Create and Add to trait layout
                    val imageParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 75)
                    val traitImage = Helper.createImageView(context, trait.imagePath, imageParams, "${Helper.originName(origin.key)} ${levels[i]}")
                    traitImage.imageTintList = ColorStateList.valueOf(resources.getColor(
                        trait.traitColor(i), null))
                    traitImageMap[traitImage] = Pair(trait.colors[i], levels[i])
                    break
                }
            }
        }
        // Sort images by levelRank then actualLevel
        val imageComparator = Comparator<Pair<ImageView, Pair<Int, Int>>> { a, b ->
            when {
                // levelRank
                (a.second.first > b.second.first) -> -1
                (a.second.first < b.second.first) -> 1
                // actualLevel
                (a.second.second > b.second.first) -> -1
                (a.second.second < b.second.first) -> 1
                else -> 0
            }
        }
        val sortedImages = traitImageMap.toList().sortedWith(imageComparator).map { it.first }
        // Add to traitlayout
        sortedImages.forEach {
            traitLayout.addView(it)
        }

        // 2.3 Add table and traits to vertical layout
        val teamLayout = LinearLayout(context)
        teamLayout.orientation = LinearLayout.VERTICAL
        teamLayout.addView(gameTable)
        teamLayout.addView(traitLayout)

        // 3. Create buttons
        // 3.1 Add edit button
        val editBtn = Helper.createSmallButton(context, getString(R.string.edit))
        editBtn.setOnClickListener {
            val args = Bundle()
            args.putInt("gameId", game.id)
            root.findNavController().navigate(R.id.nav_game_edit, args)
        }

        // 3.2 Add delete button
        val gamesLayout = root.findViewById<LinearLayout>(R.id.games_layout)
        val horizontalScroll = HorizontalScrollView(context)
        val deleteBtn = Helper.createSmallButton(context, getString(R.string.delete))
        deleteBtn.setOnClickListener {
            gameDao.deleteGames(game)
            gamesLayout.removeView(horizontalScroll)
            updateAvgPlacement()
            updateAvgDeath()
        }
        // 3.3 Create and add to button layout
        val buttonLayout = LinearLayout(context)
        buttonLayout.orientation = LinearLayout.VERTICAL
        buttonLayout.gravity = Gravity.CENTER_HORIZONTAL
        buttonLayout.addView(editBtn)
        buttonLayout.addView(deleteBtn)

        // 4. Add children to row: placement, team layout, button layout
        row.addView(placement)
        row.addView(teamLayout)
        row.addView(buttonLayout)

        // Add row to horizontal scroll
        horizontalScroll.addView(row)

        // Add scroll to games_layout
        val horizontalScrollParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        horizontalScrollParams.bottomMargin = 20
        gamesLayout.addView(horizontalScroll, horizontalScrollParams)
    }

    private fun updateAvgPlacement() {
        val avgPlacementLabel = root.findViewById<TextView>(R.id.avg_placement)
        avgPlacementLabel.text = getString(R.string.avg_placement, Helper.getPlacement(gameDao.getAvgPlacement().roundToInt(), resources))
    }

    private fun updateAvgDeath() {
        val avgStageLabel = root.findViewById<TextView>(R.id.avg_stage_of_death)
        val games = gameDao.getAll()
        var total = 0.0
        for (game in games) {
            total += game.stageDied + (game.roundDied.toDouble() / 7)
        }
        val avg = if (games.isNotEmpty()) total / games.size else 0.0
        val stage = truncate(avg).toInt()
        val round = ((avg - stage) * 7).roundToInt()
        avgStageLabel.text = getString(R.string.avg_death, stage, round)
    }
}