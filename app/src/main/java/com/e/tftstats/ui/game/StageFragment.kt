package com.e.tftstats.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Stage


class StageFragment : Fragment() {

    private lateinit var root: View
    private val currentGame = MainActivity.currentGame
    private val currentStage = currentGame.currentStageDisplayed

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_stage, container, false)

        // Title
        val textView = root.findViewById<TextView>(R.id.text_stage)
        textView.text = getString(R.string.stage_number, currentStage)

        // Placement
        val placementSpin = root.findViewById<Spinner>(R.id.placement_spin)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.placement_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            placementSpin.adapter = adapter
        }

        // Level
        val levelSpin = root.findViewById<Spinner>(R.id.level_spin)
        val minLevel = if (currentStage > 1) currentGame.stages[currentStage - 2].level else 3
        val levelAdapter = ArrayAdapter<Int>(requireContext(), android.R.layout.simple_spinner_item, (minLevel..9).toMutableList())
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        levelSpin.adapter = levelAdapter
        levelSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val xp = root.findViewById<EditText>(R.id.xp_input)
                val selected = parent!!.getItemAtPosition(position).toString()
                if (selected == "9") {
                    xp.setText("0")
                    xp.isEnabled = false
                } else {
                    xp.isEnabled = true
                }
            }
        }

        // Item layouts
        val armoryLayout = root.findViewById<LinearLayout>(R.id.armory_layout)
        val carouselLayout = root.findViewById<LinearLayout>(R.id.carousel_layout)
        val pveLayout = root.findViewById<LinearLayout>(R.id.pve_layout)

        // Hide armory and died stuff if stage = 1
        Helper.setVisible(armoryLayout, currentStage in 2..4)
        val diedCheck = root.findViewById<CheckBox>(R.id.died_check)
        Helper.setVisible(diedCheck, currentStage > 1)

        // Add item listeners
        setItemListeners()

        // Round died
        val roundSpin = root.findViewById<Spinner>(R.id.round_spin)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.round_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roundSpin.adapter = adapter
        }
        roundSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Helper.setVisible(carouselLayout, position >= 3)
                Helper.setVisible(armoryLayout, position >= 1 && currentStage < 5)
                Helper.setVisible(pveLayout, position >= 6)
            }
        }

        // Buttons
        val finalCompBtn = root.findViewById<Button>(R.id.final_comp_btn)
        val nextBtn = root.findViewById<Button>(R.id.next_stage)

        // Checkbox
        diedCheck.setOnCheckedChangeListener { _, isChecked ->
            val roundLayout = root.findViewById<LinearLayout>(R.id.round_layout)
            Helper.setVisible(finalCompBtn, isChecked)
            Helper.setVisible(nextBtn, !isChecked)
            Helper.setVisible(roundLayout, isChecked)
            Helper.setVisible(carouselLayout, !(isChecked && roundSpin.selectedItemPosition < 3))
            Helper.setVisible(armoryLayout, !(isChecked && roundSpin.selectedItemPosition < 1) && currentStage < 5)
            Helper.setVisible(pveLayout, !(isChecked && roundSpin.selectedItemPosition < 6))
            if (!isChecked && currentStage == MainActivity.currentGame.stageDied) {
                MainActivity.currentGame.stageDied = -1
            }
        }

        // Health: if 0, diedCheck should be checked
        val healthInput = root.findViewById<EditText>(R.id.health_input)
        healthInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val et = v as EditText
                if (et.text.toString().isNotBlank() && diedCheck.visibility == View.VISIBLE) {
                    if (et.text.toString().toInt() == 0) {
                        diedCheck.isChecked = true
                        diedCheck.isEnabled = false
                    } else
                        diedCheck.isEnabled = true
                }
            }
        }

        // Buttons
        val prevBtn = root.findViewById<Button>(R.id.prev_stage)
        if (currentStage == 1) {
            prevBtn.text = getString(R.string.cancel)
        }
        prevBtn.setOnClickListener {
            currentGame.currentStageDisplayed--
            if (currentGame.currentStageDisplayed < 1) {
                MainActivity.currentGame = GameModel()
            }
            requireActivity().onBackPressed()
        }
        nextBtn.setOnClickListener {
            val errors = currentGame.addStage(requireActivity(), -1)
            if (errors.isNotEmpty()) {
                Helper.showErrorDialog(errors.removeSuffix("\n"), context)
            } else {
                currentGame.currentStageDisplayed++
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_stage)
            }
        }
        finalCompBtn.setOnClickListener {
            val roundDied = Integer.parseInt(roundSpin.selectedItem.toString())
            val errors = currentGame.addStage(requireActivity(), roundDied)
            if (errors.isNotEmpty()) {
                Helper.showErrorDialog(errors.removeSuffix("\n"), context)
            } else {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_final_comp)
            }
        }

        // Load fragment
        if (MainActivity.currentGame.stages.size >= currentStage) {
            loadStageFragment(minLevel)
        } else {
            MainActivity.currentGame.stages.add(Stage())
            createPveItemsTable(emptyMap())
        }

        return root
    }

    private fun loadStageFragment(minLevel: Int) {
        val gameModel = MainActivity.currentGame
        val s: Stage = gameModel.stages[gameModel.currentStageDisplayed - 1]
        root.findViewById<EditText>(R.id.gold_input).setText(s.gold.toString())
        root.findViewById<EditText>(R.id.health_input).setText(s.health.toString())
        root.findViewById<Spinner>(R.id.placement_spin).setSelection(s.placement - 1)
        root.findViewById<Spinner>(R.id.level_spin).setSelection(s.level - minLevel)
        root.findViewById<EditText>(R.id.xp_input).setText(s.xp.toString())

        // Armory image - hide if roundDied < 2
        // If died, hide everything. Otherwise, don't set image if == 0
        if (gameModel.stageDied == gameModel.currentStageDisplayed && gameModel.roundDied == 1) {
            // Hide armory item
            root.findViewById<LinearLayout>(R.id.armory_layout).visibility = View.GONE
        } else {
            if (s.armoryItem >= 0) {
                val armoryImage = root.findViewById<ImageView>(R.id.armory_image)
                armoryImage.setImageResource(Helper.getItem(s.armoryItem).imagePath)
                armoryImage.tag = s.armoryItem
                armoryImage.visibility = View.VISIBLE
                root.findViewById<Button>(R.id.armory_button).visibility = View.GONE
            }
        }

        // Carousel image - hide if roundDied < 4
        if (gameModel.stageDied == gameModel.currentStageDisplayed && gameModel.roundDied <= 3) {
            root.findViewById<LinearLayout>(R.id.carousel_layout).visibility = View.GONE
        } else {
            if (s.carouselItem >= 0) {
                val carouselImage = root.findViewById<ImageView>(R.id.carousel_image)
                carouselImage.setImageResource(Helper.getItem(s.carouselItem).imagePath)
                carouselImage.tag = s.carouselItem
                carouselImage.visibility = View.VISIBLE
                root.findViewById<Button>(R.id.carousel_button).visibility = View.GONE
            }
        }

        val diedCheck = root.findViewById<CheckBox>(R.id.died_check)

        if (gameModel.stageDied == gameModel.currentStageDisplayed) {
            // Update died and round died
            diedCheck.isChecked = true
            if (s.health == 0) {
                diedCheck.isChecked = true
                diedCheck.isEnabled = false
            }
            root.findViewById<Spinner>(R.id.round_spin).setSelection(gameModel.roundDied - 1)
        }

        createPveItemsTable(s.pveItemsMap)
    }

    private fun setItemListeners() {
        val armoryBtn = root.findViewById<Button>(R.id.armory_button)
        armoryBtn.setOnClickListener {
            val args = Bundle()
            args.putDouble("itemType", 1.0)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
        val armoryImage = root.findViewById<ImageView>(R.id.armory_image)
        armoryImage.setOnClickListener {
            val args = Bundle()
            args.putInt("itemId", it.tag as? Int ?: -1)
            args.putDouble("itemType", 1.0)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
        val carouselBtn = root.findViewById<Button>(R.id.carousel_button)
        carouselBtn.setOnClickListener {
            val args = Bundle()
            args.putDouble("itemType", 2.0)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
        val carouselImage = root.findViewById<ImageView>(R.id.carousel_image)
        carouselImage.setOnClickListener {
            val args = Bundle()
            args.putInt("itemId", it.tag as? Int ?: -1)
            args.putDouble("itemType", 2.0)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
    }

    private fun createPveItemsTable(itemMap: Map<Int, Int>) {
        val pveTable = root.findViewById<TableLayout>(R.id.pve_table)
        val itemSize = resources.getDimensionPixelSize(R.dimen.item_size)
        var index = 0
        for (entry in itemMap.toSortedMap()) {
            val row = Helper.createRow(context)
            // Image
            val imageLayoutParams = TableRow.LayoutParams(itemSize, itemSize)
            val image = Helper.createImageView(context, Helper.getItem(entry.value).imagePath, imageLayoutParams)
            image.isClickable = true
            image.isFocusable = true
            image.setOnClickListener {
                val args = Bundle()
                args.putInt("rowId", entry.key)
                args.putInt("itemId", entry.value)
                args.putDouble("itemType", 4.0)
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
            }
            row.addView(image)

            // Clear
            val clear = Button(context)
            clear.text = getString(R.string.clear)
            clear.setOnClickListener {
                currentGame.stages[currentGame.currentStageDisplayed - 1].pveItemsMap.remove(entry.key)
                pveTable.removeView(row)
            }
            row.addView(clear)

            pveTable.addView(row, index++)
        }

        // One row with +
        val addBtn = root.findViewById<Button>(R.id.pve_button)
        addBtn.setOnClickListener {
            val args = Bundle()
            args.putDouble("itemType", 4.0)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_additem, args)
        }
    }
}