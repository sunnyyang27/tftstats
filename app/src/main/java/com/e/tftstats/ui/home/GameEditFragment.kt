package com.e.tftstats.ui.home

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.*
import com.e.tftstats.ui.game.GameModel

class GameEditFragment : Fragment() {
    private lateinit var gameDao: GameDao
    private lateinit var stageDao: StageDao
    private lateinit var teamDao: TeamDao
    private lateinit var root: View
    private var gameId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_game_edit, container, false)
        gameDao = MainActivity.db!!.gameDao()
        stageDao = MainActivity.db!!.stageDao()
        teamDao = MainActivity.db!!.teamDao()
        if (arguments != null) {
            gameId = requireArguments().getInt("gameId", -1)
        }

        val btnLayout = root.findViewById<LinearLayout>(R.id.edit_game_buttons)
        // Stages
        val stages = stageDao.getByGame(gameId)
        for (stage in stages) {
            val stageBtn = Button(ContextThemeWrapper(context, R.style.normal_pink_button))
            stageBtn.text = getString(R.string.stage_number, stage.stageNumber)
            stageBtn.setOnClickListener {
                MainActivity.currentGame.currentStageDisplayed = stage.stageNumber
                val args = Bundle()
                args.putInt("gameId", gameId)
                root.findNavController().navigate(R.id.nav_stage, args)
            }
            btnLayout.addView(stageBtn)
        }

        // Team comp
        val teamBtn = Button(ContextThemeWrapper(context, R.style.normal_pink_button))
        teamBtn.text = getString(R.string.final_comp)
        teamBtn.setOnClickListener {
            val args = Bundle()
            args.putInt("gameId", gameId)
            root.findNavController().navigate(R.id.nav_final_comp, args)
        }
        btnLayout.addView(teamBtn)

        // Done listener
        val doneBtn = root.findViewById<Button>(R.id.gameedit_done)
        doneBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Create gameModel
        val gameModel = GameModel()
        val game = gameDao.getGame(gameId)
        gameModel.currentStageDisplayed = 0
        gameModel.stages = stages.toMutableList()
        gameModel.stages.forEach { stage ->
            val pveItems = if (stage.pveItems.isBlank()) emptyList() else stage.pveItems.split(",").map { it.toInt() }
            pveItems.forEach {
                stage.pveItemsMap[stage.pveItemCounter++] = it
            }
        }
        gameModel.stageDied = game.stageDied
        gameModel.roundDied = game.roundDied
        val teamComp = teamDao.getTeamByGame(gameId)
        for (team in teamComp) {
            gameModel.teamComp[gameModel.champCounter++] = team
        }
        MainActivity.currentGame = gameModel

        return root
    }
}