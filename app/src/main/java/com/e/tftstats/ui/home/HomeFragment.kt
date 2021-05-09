package com.e.tftstats.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Game
import com.e.tftstats.model.GameDao
import com.e.tftstats.model.Helper
import com.e.tftstats.model.TeamDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.truncate

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
            root.findNavController().navigate(R.id.nav_stage)
        }

        // Avg placement
        val avgPlacementLabel = root.findViewById<TextView>(R.id.avg_placement)
        avgPlacementLabel.text = "Avg placement: ${Helper.getPlacement(gameDao.getAvgPlacement().roundToInt(), resources)}"

        // Avg stage of death
        val avgStageLabel = root.findViewById<TextView>(R.id.avg_stage_of_death)
        val games = gameDao.getAll()
        var total = 0.0
        for (game in games) {
            total += game.stageDied + (game.roundDied.toDouble() / 7)
        }
        val avg = if (games.isNotEmpty()) total / games.size else 0.0
        val stage = truncate(avg).toInt()
        val round = ((avg - stage) * 7).roundToInt()
        avgStageLabel.text = "Avg death: stage ${stage}-${round}"

        // Last 10 games
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
                    val distance = measureDistance(bigScroll, root.findViewById(R.id.new_game))
                    constraintSet.constrainMaxHeight(R.id.games_scroll, distance)
                    constraintSet.applyTo(constraintLayout)
                }
            }
        )

        return root
    }

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

        // Placement
        val placement = TextView(context)
        placement.textSize = 30f
        placement.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        placement.setTextAppearance(R.style.TextAppearance_AppCompat_Title)
        placement.text = Helper.getPlacement(game.placement, resources, placement)
        val placementParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        placementParams.marginEnd = 20
        placement.layoutParams = placementParams
        row.addView(placement)

        val champWidth = 150

        val teamComp = teamDao.getTeamByGame(game.id)
        for (team in teamComp) {
            val champion = Helper.getChampion(team.champId)

            // Vertical linear layout
            val champLayout = LinearLayout(context)
            val champParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            champParams.marginEnd = 3
            champLayout.layoutParams = champParams
            champLayout.orientation = LinearLayout.VERTICAL

            // Stars: horizontal
            val starsLayout = LinearLayout(context)
            starsLayout.orientation = LinearLayout.HORIZONTAL
            starsLayout.gravity = Gravity.CENTER_HORIZONTAL
            for (i in 1..team.starLevel) {
                val starParams = LinearLayout.LayoutParams(champWidth / 3, LinearLayout.LayoutParams.WRAP_CONTENT)
                val star = Helper.createImageView(root.context, R.drawable.ic_star, starParams)
                starsLayout.addView(star)
            }
            champLayout.addView(starsLayout)

            // Champion image
            val imageParams = LinearLayout.LayoutParams(champWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
            imageParams.bottomMargin = 3
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
            champLayout.addView(image)

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
            champLayout.addView(itemsLayout)

            // Add layout to parent layout
            row.addView(champLayout)
        }

        // Add row to horizontal scroll
        val horizontalScroll = HorizontalScrollView(context)
        horizontalScroll.addView(row)

        // Add scroll to games_layout
        val gamesLayout = root.findViewById<LinearLayout>(R.id.games_layout)
        gamesLayout.addView(horizontalScroll)
    }

    private fun measureDistance(view1: View, view2: View) : Int {
        view1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val location1 = IntArray(2)
        view1.getLocationInWindow(location1)
        val location2 = IntArray(2)
        view2.getLocationInWindow(location2)
        return abs(location1[1] - location2[1])
    }
}