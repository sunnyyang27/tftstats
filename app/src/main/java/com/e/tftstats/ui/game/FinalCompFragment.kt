package com.e.tftstats.ui.game

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Champion
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Team
import com.e.tftstats.model.TeamDao
import com.google.android.flexbox.FlexboxLayout

class FinalCompFragment : Fragment() {
    private lateinit var root: View
    private lateinit var teamDao: TeamDao
    private var gameId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_final_comp, container, false)
        if (arguments != null) {
            gameId = requireArguments().getInt("gameId", -1)
        }
        teamDao = MainActivity.db!!.teamDao()

        loadTable()
        loadTraitsRow()

        // Button listeners
        val addRowBtn = root.findViewById<Button>(R.id.add_row)
        addRowBtn.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_addchamp)
        }

        // Change button to save
        if (gameId != -1) {
            val saveBtn = root.findViewById<Button>(R.id.finish_game)
            saveBtn.text = getString(R.string.save)
            saveBtn.setOnClickListener {
                val originalTeam = teamDao.getTeamByGame(gameId).toMutableList()
                val updatedTeam = MainActivity.currentGame.teamComp.values.toList()
                // 1. Get new champs: remove champs from updated that are not in original
                val newChamps = updatedTeam.filter { new -> !originalTeam.any { original -> original.id == new.id } }
                newChamps.forEach {
                    it.gameId = gameId
                }
                // 2. Get deleted champs: remove champs from original that are still in updated. The leftover is what to delete
                originalTeam.removeIf { original -> updatedTeam.any { updated -> updated.id == original.id } }
                // 3. Add new champs, update champs, and delete
                teamDao.insertTeams(newChamps)
                teamDao.updateTeams(updatedTeam)
                teamDao.deleteTeams(originalTeam)
                requireActivity().onBackPressed()
            }
        }
        return root
    }

    private fun loadTable() {
        // Load table
        for (team in MainActivity.currentGame.teamComp) {
            loadTeam(team.key, team.value)
        }
    }

    private fun loadTeam(id: Int, team: Team) {
        // In row, create "edit" and "delete" button
        val champ = Helper.getChampion(team.champId)

        val champTable = root.findViewById<TableLayout>(R.id.champ_table)
        val row = Helper.createRow(context)

        val size = 75

        // Champ image
        val imageLayoutParams = TableRow.LayoutParams(size, TableRow.LayoutParams.WRAP_CONTENT)
        imageLayoutParams.marginEnd = 5
        val image = Helper.createImageView(context, champ.imagePath, imageLayoutParams)
        row.addView(image)

        // Champ name
        val tv = TextView(context)
        tv.text = champ.name
        tv.textSize = 18f
        tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
        tv.measure(0,0)
        row.addView(tv)

        // Edit button
        val edit = Helper.createSmallButton(context, getString(R.string.edit))
        edit.setOnClickListener {
            onEditChamp(id, team.champId)
        }

        // Delete button
        val delete = Helper.createSmallButton(context, getString(R.string.delete))
        delete.setOnClickListener {
            MainActivity.currentGame.teamComp.remove(id)
            champTable.removeView(row)
            loadTraitsRow()
        }

        // Stars
        for (i in 1..team.starLevel) {
            val layoutParams = TableRow.LayoutParams(size, TableRow.LayoutParams.WRAP_CONTENT)
            val star = Helper.createImageView(context, R.drawable.ic_star, layoutParams)
            row.addView(star)
        }
        for (i in team.starLevel until 3) {
            val layoutParams = TableRow.LayoutParams(size, TableRow.LayoutParams.WRAP_CONTENT)
            val star = Helper.createImageView(context, R.drawable.ic_star_empty, layoutParams)
            row.addView(star)
        }

        // Items
        val items = team.items.split(",")
        for (item in items) {
            val imagePath = if (item == "-1") 0 else Helper.getItem(item.toInt()).imagePath
            val layoutParams = TableRow.LayoutParams(size, size)
            layoutParams.marginEnd = 3
            val itemImage = Helper.createImageView(context, imagePath, layoutParams)
            row.addView(itemImage)
        }

        row.addView(edit)
        row.addView(delete)

        champTable.addView(row, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
    }

    private fun loadTraitsRow() {
        // Evaluate traits
        val traitLayout = root.findViewById<FlexboxLayout>(R.id.team_traits_layout)
        traitLayout.removeAllViews()
        val traitMap = Helper.calculateTeamsTraits(MainActivity.currentGame.teamComp.values.toList())
        // Get levels per trait
        val traitImageMap = HashMap<ImageView, Pair<Int, Int>>() // ImageView, (levelRank, actualLevel)
        val imageParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, 100)
        for (origin in traitMap) {
            val trait = Helper.getTrait(origin.key)
            val traitImage = Helper.createImageView(context, trait.imagePath, imageParams)
            traitImage.alpha = 0.1f

            var fullLevel = trait.levels[0]             // default to first in case no level is met
            var levelRank = -1
            val numLevels = trait.levels.size
            val levels = trait.levels
            for (i in numLevels - 1 downTo 0) {
                if (origin.value >= levels[i]) {
                    traitImage.imageTintList = ColorStateList.valueOf(
                        resources.getColor(trait.traitColor(i), null))
                    traitImage.alpha = 1f
                    fullLevel = levels[i]
                    levelRank = trait.colors[i]
                    break
                }
            }
            val tooltip = "${Helper.originName(origin.key)} ${origin.value}/$fullLevel"
            traitImage.tooltipText = tooltip
            traitImageMap[traitImage] = Pair(levelRank, origin.value)
        }

        // Sort images by levelRank then actualLevel
        val custom = Comparator<Pair<ImageView, Pair<Int, Int>>> { a, b ->
            when {
                // levelRank
                (a.second.first > b.second.first) -> -1
                (a.second.first < b.second.first) -> 1
                // actualLevel
                (a.second.second > b.second.second) -> -1
                (a.second.second < b.second.second) -> 1
                else -> 0
            }
        }

        val sortedImages = traitImageMap.toList().sortedWith(custom).map { it.first }

        // Add to traitlayout
        sortedImages.forEach {
            traitLayout.addView(it)
        }
    }

    private fun onEditChamp(id: Int, champId: Int) {
        // Open AddChamp fragment
        val args = Bundle()
        args.putInt("teamId", id)
        args.putInt("champId", champId)
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_addchamp, args)
    }
}