package com.e.tftstats.ui.game

import android.content.Context
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
import com.google.android.flexbox.FlexboxLayout

class FinalCompFragment : Fragment() {
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_final_comp, container, false)
        loadTable()
        loadTraitsRow()

        // Button listeners
        val addRowBtn = root.findViewById<Button>(R.id.add_row)
        addRowBtn.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_addchamp)
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
        val row = Helper.createRow(root.context)

        val size = 75

        // Champ image
        val imageLayoutParams = TableRow.LayoutParams(size, TableRow.LayoutParams.WRAP_CONTENT)
        imageLayoutParams.marginEnd = 5
        val image = Helper.createImageView(context, champ.imagePath, imageLayoutParams)
        row.addView(image)

        // Champ name
        val tv = TextView(root.context)
        tv.text = champ.name
        tv.textSize = 18f
        tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
        tv.measure(0,0)
        row.addView(tv)

        // Edit button
        val edit = createButton(root.context, getString(R.string.edit))
        edit.setOnClickListener {
            onEditChamp(id, team.champId)
        }

        // Delete button
        val delete = createButton(root.context, getString(R.string.delete))
        delete.setOnClickListener {
            MainActivity.currentGame.teamComp.remove(id)
            champTable.removeView(row)
            loadTraitsRow()
        }

        // Stars
        for (i in 1..team.starLevel) {
            val layoutParams = TableRow.LayoutParams(size, TableRow.LayoutParams.WRAP_CONTENT)
            val star = Helper.createImageView(root.context, R.drawable.ic_star, layoutParams)
            row.addView(star)
        }
        for (i in team.starLevel until 3) {
            val layoutParams = TableRow.LayoutParams(size, TableRow.LayoutParams.WRAP_CONTENT)
            val star = Helper.createImageView(root.context, R.drawable.ic_star_empty, layoutParams)
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
        val traitImageMap = HashMap<ImageView, Pair<Double, Int>>() // ImageView, (levelIndex / levels.size, actualLevel)
        val imageParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, 100)
        for (origin in traitMap) {
            val trait = Helper.getTrait(origin.key)
            val traitImage = Helper.createImageView(context, trait.imagePath, imageParams)
            traitImage.alpha = 0.1f

            var fullLevel = trait.levels[0]             // default to first in case no level is met
            var levelRank = 0.0
            if (origin.key != Champion.Origin.GODKING || origin.value == 1) {
                val numLevels = trait.levels.size
                val levels = trait.levels
                for (i in numLevels - 1 downTo 0) {
                    if (origin.value >= levels[i]) {
                        traitImage.imageTintList = ColorStateList.valueOf(resources.getColor(Helper.getTraitTint(i, numLevels), null))
                        traitImage.alpha = 1f
                        fullLevel = levels[i]
                        levelRank = (i.toDouble() + 1) / numLevels
                        break
                    }
                }
            }
            val tooltip = "${Helper.originName(origin.key)} ${origin.value}/$fullLevel"
            traitImage.tooltipText = tooltip
            traitImageMap[traitImage] = Pair(levelRank, fullLevel)
        }

        // Sort images by levelRank then actualLevel
        val custom = Comparator<Pair<ImageView, Pair<Double, Int>>> { a, b ->
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

    private fun createButton(context: Context, text: String) : Button {
        val btn = Button(context)
        btn.text = text
        btn.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        btn.textSize = 12f
        btn.minimumWidth = 0
        btn.minWidth = 0
        btn.minimumHeight = 0
        btn.minHeight = 0
        return btn
    }
}