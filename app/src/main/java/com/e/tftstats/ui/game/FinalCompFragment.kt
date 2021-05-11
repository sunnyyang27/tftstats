package com.e.tftstats.ui.game

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Team

class FinalCompFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_final_comp, container, false)
        loadTable(root)

        // Button listeners
        val addRowBtn = root.findViewById<Button>(R.id.add_row)
        addRowBtn.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_addchamp)
        }
        return root
    }

    private fun loadTable(root: View) {
        // Load table
        for (team in MainActivity.currentGame.teamComp) {
            loadTeam(team.key, team.value, root)
        }
    }

    private fun loadTeam(id: Int, team: Team, root: View) {
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
        val edit = createButton(root.context, "Edit")
        edit.setOnClickListener {
            onEditChamp(id, team.champId)
        }

        // Delete button
        val delete = createButton(root.context, "Delete")
        delete.setOnClickListener {
            MainActivity.currentGame.teamComp.remove(tv.id)
            champTable.removeView(row)
        }

//        val size = (MainActivity.screenWidth
//                - tv.measuredWidth - edit.measuredWidth - delete.measuredWidth - 9) / 6 // resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin) * 2
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