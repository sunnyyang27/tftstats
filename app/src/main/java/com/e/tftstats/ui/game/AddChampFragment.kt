package com.e.tftstats.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Champion
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Team

class AddChampFragment : Fragment() {

    private var teamId = -1
    private var champId = -1
    private val currentGame = MainActivity.currentGame
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_add_champ, container, false)

        if (arguments != null) {
            teamId = requireArguments().getInt("teamId", -1)
            champId = requireArguments().getInt("champId", -1)
        }
        // Champ name
        val tv = root.findViewById<AutoCompleteTextView>(R.id.champ_name)
        val champAdapter = ArrayAdapter<Champion>(requireContext(), android.R.layout.simple_list_item_1, Helper.getChampions())
        tv.setAdapter(champAdapter)
        tv.onItemClickListener = OnItemClickListener { arg0, _, arg2, _ ->
            val selected = arg0.adapter.getItem(arg2) as Champion
            champId = selected.id
            createChampLabels(selected)
        }
        if (champId != -1) {
            createChampLabels(Helper.getChampion(champId))
        }

        // Star
        val starSpin = root.findViewById<Spinner>(R.id.star_spin)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.star_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            starSpin.adapter = adapter
        }

        val carryCheck = root.findViewById<CheckBox>(R.id.carry_check)

        // Button listeners
        val addBtn = root.findViewById<Button>(R.id.addchamp_add)
        addBtn.setOnClickListener {
            val errors = (if (champId == -1) "Missing champion.\n" else "") + currentGame.validateItems(champId)
            if (errors.isNotEmpty()) {
                Helper.showErrorDialog(errors.removeSuffix("\n"), context)
            } else {
                // Add champ to team
                val newTeam = Team(
                    gameId = -1,
                    champId = champId,
                    starLevel = Integer.parseInt(starSpin.selectedItem.toString()),
                    isCarry = carryCheck.isChecked,
                    items = currentGame.teamItems.joinToString(",")
                )

                currentGame.teamComp[currentGame.champCounter] = newTeam
                currentGame.champCounter++
                currentGame.resetItems()

                // Go back to final comp fragment
                requireActivity().onBackPressed()
            }
        }
        val saveBtn = root.findViewById<Button>(R.id.addchamp_save)
        saveBtn.setOnClickListener {
            val errors = currentGame.validateItems(champId)
            if (errors.isNotEmpty()) {
                Helper.showErrorDialog(errors.removeSuffix("\n"), context)
            } else {
                val team = currentGame.teamComp[teamId]!!

                team.champId = champId
                team.starLevel = Integer.parseInt(starSpin.selectedItem.toString())
                team.isCarry = carryCheck.isChecked
                team.items = currentGame.teamItems.joinToString(",")
                currentGame.resetItems()

                // Navigate back
                requireActivity().onBackPressed()
            }
        }

        // Items
        setupItems()

        // Initialize team values
        if (teamId != -1) {
            val team: Team = currentGame.teamComp[teamId]!!
            val champ = Helper.getChampion(team.champId)
            tv.setText(champ.name)
            createChampLabels(champ)
            starSpin.setSelection(team.starLevel - 1)
            carryCheck.isChecked = team.isCarry

            addBtn.visibility = View.GONE
            saveBtn.visibility = View.VISIBLE
        }

        createBackPressed()

        return root
    }

    private fun setupItems() {
        setItemListeners(R.id.champ_item_1_button, R.id.champ_item_1_image, R.id.champ_item_1_clear, 3.1)
        setItemListeners(R.id.champ_item_2_button, R.id.champ_item_2_image, R.id.champ_item_2_clear, 3.2)
        setItemListeners(R.id.champ_item_3_button, R.id.champ_item_3_image, R.id.champ_item_3_clear, 3.3)
    }

    private fun setItemListeners(btnId: Int, imageId: Int, clearId: Int, itemType: Double) {
        // Each btn goes to item fragment
        val itemBtn = root.findViewById<Button>(btnId)
        itemBtn.setOnClickListener {
            val args = Bundle()
            args.putDouble("itemType", itemType)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
        val image = root.findViewById<ImageView>(imageId)
        image.setOnClickListener {
            val args = Bundle()
            args.putInt("itemId", it.tag as? Int ?: -1)
            args.putDouble("itemType", itemType)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
        val clear = root.findViewById<Button>(clearId)
        clear.setOnClickListener {
            image.setImageResource(0)
            image.tag = -1
            image.visibility = View.GONE
            itemBtn.visibility = View.VISIBLE
            clear.visibility = View.INVISIBLE
            currentGame.addItemToTeam(-1, itemType)
        }

        // On edit
        if (teamId != -1 && currentGame.teamItems[0] == -1 && currentGame.teamItems[1] == -1 && currentGame.teamItems[2] == -1) {
            val team: Team = currentGame.teamComp[teamId]!!
            currentGame.teamItems = team.items.split(",").map { it.toInt() }.toMutableList()
        }
        val index = (itemType * 10).toInt() - 31
        val item = currentGame.teamItems[index]
        if (item != -1) {
            itemBtn.visibility = View.GONE
            image.visibility = View.VISIBLE
            clear.visibility = View.VISIBLE
            image.setImageResource(Helper.getItem(item).imagePath)
            image.tag = item
        }
    }

    private fun createChampLabels(selected: Champion) {
        // Champion image
        val champImage = root.findViewById<ImageView>(R.id.champ_image)
        champImage.setImageResource(selected.imagePath)

        // Traits
        val table = root.findViewById<TableLayout>(R.id.champ_trait_table)
        table.removeAllViews()
        // Update image and trait name
        for (origin in selected.origins) {
            val row = Helper.createRow(context, 10)
            val layoutParams = TableRow.LayoutParams(100, 100)
            val traitImage = Helper.createImageView(context, Helper.getTrait(origin).imagePath, layoutParams)

            val traitLabel = TextView(context)
            traitLabel.text = Helper.getTrait(origin).toString()
            traitLabel.setTextAppearance(R.style.TextAppearance_AppCompat_Caption)
            traitLabel.textSize = 14f

            row.addView(traitImage)
            row.addView(traitLabel)
            table.addView(row)
        }
    }

    private fun createBackPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                currentGame.resetItems()
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}