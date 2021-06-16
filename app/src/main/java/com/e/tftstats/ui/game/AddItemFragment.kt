package com.e.tftstats.ui.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.e.tftstats.MainActivity
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.e.tftstats.model.Item

class AddItemFragment : Fragment() {

    private lateinit var root: View

    private var itemSize: Int = 0
    private var imageId: Int = 0                       // imageId counter
    private var currentImageClicked: Int = -1          // imageView ID, not item ID
    private var selectedItemId: Int = -1
    private var imageUpdated: Boolean = false
    private var itemType: Double = 0.0 // 1 = armory, 2 = carousel, 3.1 = champItem1, 3.2 = champItem2, 3.3 = champItem3
    private var pveRowId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_add_item, container, false)
        if (arguments != null) {
            selectedItemId = requireArguments().getInt("itemId", -1)
            itemType = requireArguments().getDouble("itemType", 0.0)
            pveRowId = requireArguments().getInt("rowId", -1)
        }

        itemSize = (MainActivity.screenWidth - 16) / (Helper.numRows + 1)
        val itemTable = root.findViewById<TableLayout>(R.id.item_table)
        val shadowItemTable = root.findViewById<TableLayout>(R.id.shadow_item_table)
        createTable(Helper.itemTable, itemTable)
        createTable(Helper.shadowItemTable, shadowItemTable)

        if (selectedItemId >= 0) {
            val itemSelectedImage = root.findViewById<ImageView>(R.id.item_selected_image)
            val item = Helper.getItem(selectedItemId)
            itemSelectedImage.setImageResource(item.imagePath)
            itemSelectedImage.tag = selectedItemId
        }

        // Switch
        val itemSwitch = root.findViewById<Switch>(R.id.item_type_switch)
        itemSwitch.setOnCheckedChangeListener { _, isChecked ->
            Helper.setVisible(itemTable, !isChecked)
            Helper.setVisible(shadowItemTable, isChecked)
        }

        // Button listeners
        val addBtn = root.findViewById<Button>(R.id.additem_add)
        addBtn.setOnClickListener {
            updateItem(it)
        }
        val saveBtn = root.findViewById<Button>(R.id.additem_save)
        saveBtn.setOnClickListener {
            updateItem(it)
        }

        return root
    }

    private fun createTable(tableSource: Array<Array<Item?>>, champTable: TableLayout) {
        // Row 0-9
        for (items in tableSource) {
            val row = Helper.createRow(context)
            for (item in items) {
                if (item == null) {
                    row.addView(createImageView())
                    continue
                }
                row.addView(createImageView(item.id, item.imagePath))
            }
            champTable.addView(row)
            if (itemType == 1.0) break
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createImageView(itemId: Int = -1, imagePath: Int = 0, itemName: String = "") : ImageView {
        val layoutParams = TableRow.LayoutParams(itemSize, TableRow.LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = 3
        layoutParams.bottomMargin = 3
        val iv = Helper.createImageView(context, imagePath, layoutParams)
        if (selectedItemId == itemId && !imageUpdated) {
            iv.setColorFilter(Color.argb(80, 255, 255, 0))
            imageUpdated = true
            currentImageClicked = imageId
        }
        iv.id = imageId++

        if (itemId == -1) return iv

        iv.setOnTouchListener { _, event ->
            // Change color
            if (event.action == MotionEvent.ACTION_DOWN) {
                iv.setColorFilter(Color.argb(80, 255, 255, 0))
            }
            if (currentImageClicked > -1) {
                val currentItemClicked: ImageView = requireView().findViewById(currentImageClicked)
                currentItemClicked.setColorFilter(Color.argb(0, 0, 0, 0))
            }

            val itemSelectedImage = root.findViewById<ImageView>(R.id.item_selected_image)
            // if this is unselect, don't highlight item, empty item selected and name
            if (currentImageClicked == iv.id) {
                currentImageClicked = -1
                itemSelectedImage.setImageResource(0)
                itemSelectedImage.tag = -1
                itemSelectedImage.tooltipText = ""
            } else {
                currentImageClicked = iv.id

                // Update image selected
                itemSelectedImage.setImageResource(imagePath)
                itemSelectedImage.tag = itemId
                itemSelectedImage.tooltipText = itemName
            }
            false
        }

        return iv
    }

    private fun updateItem(view: View) {
        // Get selected item
        val selectedItem = root.findViewById<ImageView>(R.id.item_selected_image)
        val tag = selectedItem.tag as? Int ?: -1
        if (tag == -1) {
            // Go back to stage
            requireActivity().onBackPressed()
            return
        }

        // Add item to stage, then go back
        val stages = MainActivity.currentGame.stages
        val index = MainActivity.currentGame.currentStageDisplayed - 1
        if (itemType == 1.0) {
            stages[index].armoryItem = tag
        } else if (itemType == 2.0) {
            stages[index].carouselItem = tag
        } else if (itemType >= 3.0 && itemType < 4.0) {
            MainActivity.currentGame.addItemToTeam(tag, itemType)
        } else if (itemType == 4.0) {
            if (pveRowId == -1) {
                pveRowId = stages[index].pveItemCounter++
            }
            stages[index].pveItemsMap[pveRowId] = tag
        }

        requireActivity().onBackPressed()
        itemType = 0.0
    }
}