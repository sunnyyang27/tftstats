package com.e.tftstats.ui.game

import android.app.Activity
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.e.tftstats.R
import com.e.tftstats.model.Helper
import com.e.tftstats.model.SpatItem
import com.e.tftstats.model.Stage
import com.e.tftstats.model.Team

class GameModel {
    var currentStageDisplayed: Int = 1          // change depending on nav
    var stages: MutableList<Stage> = ArrayList()
    var stageDied: Int = -1                     // set when deathbtn pressed, overwritten if died is changed
    var roundDied: Int = -1                     // set when deathbtn pressed
    var tmpRoundDied: Int = -1                  // set when round died selector changes, used when editing Game
    var teamComp: HashMap<Int, Team> = hashMapOf()
    var teamItems = mutableListOf(-1, -1, -1)   // set when Edit pressed or when AddItemFragment.save. cleared when AddChamp.save or AddChamp.cancel
    var champCounter: Int = 0

    // Validate and add stage
    // Return error message
    fun addStage(activity: Activity, roundDied: Int) : String {
        val s = stages[currentStageDisplayed - 1]
        val error = StringBuilder()
        try {
            s.gold = Integer.parseInt(activity.findViewById<EditText>(R.id.gold_input).text.toString())
            s.health = Integer.parseInt(activity.findViewById<EditText>(R.id.health_input).text.toString())
            s.placement = Integer.parseInt(activity.findViewById<Spinner>(R.id.placement_spin).selectedItem.toString())
            s.level = Integer.parseInt(activity.findViewById<Spinner>(R.id.level_spin).selectedItem.toString())
            s.xp = Integer.parseInt(activity.findViewById<EditText>(R.id.xp_input).text.toString())
        } catch (e: Exception) {
            if (activity.findViewById<EditText>(R.id.gold_input).text.isBlank()) {
                error.append("Missing gold.\n")
            }
            if (activity.findViewById<EditText>(R.id.health_input).text.isBlank()) {
                error.append("Missing health.\n")
            }
            if (activity.findViewById<EditText>(R.id.xp_input).text.isBlank()) {
                error.append("Missing XP.\n")
            }
        }

        // Armory item: must exist if stage >= 2 and roundDied >= 2
        if ((roundDied < 2 && roundDied > -1) || currentStageDisplayed == 1 || currentStageDisplayed >= 5)
            s.armoryItem = -1
        else {
            s.armoryItem = activity.findViewById<ImageView>(R.id.armory_image).tag as? Int ?: -1
            if (s.armoryItem == -1) {
                error.append("Missing armory item.\n")
            }
        }
        // Carousel item: must exist if roundDied >= 4
        if (roundDied < 4 && roundDied > -1)
            s.carouselItem = -1
        else {
            s.carouselItem = activity.findViewById<ImageView>(R.id.carousel_image).tag as? Int ?: -1
            if (s.carouselItem == -1) {
                error.append("Missing carousel item.\n")
            }
        }
        if (error.isNotEmpty()) {
            return error.toString()
        }
        validateStage(s, error, roundDied)
        if (error.isNotEmpty()) {
            return error.toString()
        }

        // PVE items cleared if roundDied < 7
        if (roundDied in 1..6) {
            s.pveItemsMap.clear()
        } else {
            s.pveItems = Helper.sortAndJoin(s.pveItemsMap)
        }

        s.stageNumber = currentStageDisplayed
        if (stages.size >= currentStageDisplayed) {
            stages[currentStageDisplayed-1] = s
        }

        if (roundDied > -1) {
            // Died in this stage
            stageDied = currentStageDisplayed
            this.roundDied = roundDied
            this.tmpRoundDied = -1
            if (stages.size > stageDied) stages.subList(stageDied, stages.size).clear()
        } else if (stageDied == currentStageDisplayed) {
            // if this stage was supposed to be the time I died, then fix it
            stageDied = -1
            this.roundDied = -1
        }
        return ""
    }

    private fun validateStage(s: Stage, error: StringBuilder, roundDied: Int) {
        // Health cannot exceed 100
        if (s.health > 100) error.append("Health cannot exceed 100.\n")

        // Throw error if xp doesn't make sense (exceeds or is odd)
        if (s.xp % 2 != 0) error.append("XP must be even.\n")
        if (s.level != 9 && s.xp >= Helper.xpTable[s.level - 1]) error.append("XP for level ${s.level} must be less than ${Helper.xpTable[s.level - 1]}.\n")

        // Compare with previous stage
        if (currentStageDisplayed < 2) return
        val previous = stages[currentStageDisplayed - 2]
        if (s.health > previous.health) error.append("Health must be less than or equal to the previous stage's health.\n")
        if (s.level < previous.level) error.append("Level must be greater than or equal to the previous stage's level.\n")
        if (s.level == previous.level && s.xp - previous.xp < 12 && roundDied == -1) error.append("XP must be at least ${previous.xp + 12}.\n")
    }

    // itemType = 3.1, 3.2, 3.3
    fun addItemToTeam(itemId: Int, itemType: Double) {
        val index = (itemType * 10).toInt() - 31
        teamItems[index] = itemId
    }

    fun resetItems() {
        teamItems[0] = -1
        teamItems[1] = -1
        teamItems[2] = -1
    }

    fun validateItems(champId: Int) : String {
        val error = StringBuilder()
        val item1 = teamItems[0]
        val item2 = teamItems[1]
        val item3 = teamItems[2]
        // Cannot have unique items
        var duplicated = -1
        if (item1 == item2 || item2 == item3) duplicated = item2
        if (item1 == item3) duplicated = item1
        if (duplicated != -1 && Helper.getItem(duplicated).unique) error.append("A champion cannot equip ${Helper.getItem(duplicated).name} more than once. Please remove the duplicate(s).\n")

        // If TG, cannot have more items. Includes shadow as well
        var tgId = -1
        if (teamItems.contains(Helper.tgId)) {
            tgId = Helper.tgId
        } else if (teamItems.contains(Helper.shadowTgId)) {
            tgId = Helper.shadowTgId
        }

        val tgMsg = StringBuilder()
        var hasExtraItems = false
        // Can't have more than one base component
        val components = StringBuilder()
        var numComponents = 0
        // Cannot have spat item if already that trait
        val champ = if (champId != -1) Helper.getChampion(champId) else null
        for (itemId in teamItems) {
            if (itemId == -1) continue
            val item = Helper.getItem(itemId)
            // TG items
            if (tgId != -1 && itemId != tgId) {
                tgMsg.append(item.name)
                tgMsg.append(" and ")
                hasExtraItems = true
            }
            // Base component
            if (Helper.itemTable[0].contains(item) || Helper.shadowItemTable[0].contains(item)) {
                numComponents++
                components.append(item.name)
                components.append(", ")
            }
            // Spat item
            if (champ != null && item is SpatItem && champ.origins.contains(item.origin)) {
                error.append("${champ.name} is already a ${Helper.originName(item.origin)} and cannot equip ${item.name}.\n")
            }
        }
        if (hasExtraItems) {
            error.append("${Helper.getItem(tgId).name} counts for three item slots. Please remove ")
            error.append(tgMsg.removeSuffix(" and "))
            error.append(".\n")
        }
        if (numComponents > 1) {
            error.append("A champion cannot equip more than one base component. Please remove ${if (numComponents == 3) "two of" else "one of"} ")
            error.append(components.removeSuffix(", "))
            error.append(".\n")
        }

        return error.toString()
    }
}