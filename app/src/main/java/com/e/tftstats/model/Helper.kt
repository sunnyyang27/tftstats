package com.e.tftstats.model

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.e.tftstats.R
import com.e.tftstats.model.Champion.Origin
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.roundToInt

class Helper {
    companion object {
        // Used by AddItemFragment
        const val numRows = 10
        val itemTable: Array<Array<Item?>> = Array(numRows) { arrayOfNulls<Item>(numRows) }
        val shadowItemTable: Array<Array<Item?>> = Array(numRows) { arrayOfNulls<Item>(numRows) }

        // Used by AddChampFragment and MainActivity.AddChamp
        private val championIdMap: MutableMap<Int, Champion> = HashMap()

        private var initialized = false
        private val champions: MutableList<Champion> = ArrayList()

        // Used by LevelFragment and GameModel
        val xpTable: Array<Int> = arrayOf(0, 2, 6, 10, 20, 36, 56, 80, 0)

        // Used by ItemStatsFragment
        private val itemIdMap: MutableMap<Int, Item> = HashMap()

        // Used by FinalCompStatsFragment
        private val traitMap: MutableMap<Origin, Trait> = EnumMap(Origin::class.java)

        // Used in GameModel
        var tgId: Int = -1
        var shadowTgId: Int = -1

        private var itemNum: Int = 1

        fun loadAssets() {
            loadChampions()
            loadItems()
            loadShadowItems()
            loadChampionsHashmap()
            loadTraitsMap()
            initialized = true
        }

        private fun loadChampions() {
            champions.add(Champion("Kalista", Origin.ABOMINATION, Origin.LEGIONNAIRE, 1, R.drawable.kalista))
            champions.add(Champion("Brand", Origin.ABOMINATION, Origin.SPELLWEAVER, 2, R.drawable.brand))
            champions.add(Champion("Nunu & Willump", Origin.ABOMINATION, Origin.BRAWLER, 3, R.drawable.nunu))
            champions.add(Champion("Ryze", Origin.ABOMINATION, Origin.FORGOTTEN, 4, R.drawable.ryze, Origin.MYSTIC))

            champions.add(Champion("Lissandra", Origin.COVEN, Origin.RENEWER, 1, R.drawable.lissandra))
            champions.add(Champion("Leblanc", Origin.COVEN, Origin.ASSASSIN, 2, R.drawable.leblanc))
            champions.add(Champion("Morgana", Origin.COVEN, Origin.NIGHTBRINGER, 3, R.drawable.morgana, Origin.MYSTIC))

            champions.add(Champion("Gragas", Origin.DAWNBRINGER, Origin.BRAWLER, 1, R.drawable.gragas))
            champions.add(Champion("Khazix", Origin.DAWNBRINGER, Origin.ASSASSIN, 1, R.drawable.khazix))
            champions.add(Champion("Soraka", Origin.DAWNBRINGER, Origin.RENEWER, 2, R.drawable.soraka))
            champions.add(Champion("Nidalee", Origin.DAWNBRINGER, Origin.SKIRMISHER, 3, R.drawable.nidalee))
            champions.add(Champion("Riven", Origin.DAWNBRINGER, Origin.LEGIONNAIRE, 3, R.drawable.riven))
            champions.add(Champion("Karma", Origin.DAWNBRINGER, Origin.INVOKER, 4, R.drawable.karma))
            champions.add(Champion("Garen", Origin.DAWNBRINGER, Origin.KNIGHT, 5, R.drawable.garen, Origin.GODKING))

            champions.add(Champion("Udyr", Origin.DRACONIC, Origin.SKIRMISHER, 1, R.drawable.udyr))
            champions.add(Champion("Sett", Origin.DRACONIC, Origin.BRAWLER, 2, R.drawable.sett))
            champions.add(Champion("Ashe", Origin.VERDANT, Origin.DRACONIC, 3, R.drawable.ashe, Origin.RANGER))
            champions.add(Champion("Zyra", Origin.DRACONIC, Origin.SPELLWEAVER, 3, R.drawable.zyra))
            champions.add(Champion("Heimerdinger", Origin.DRACONIC, Origin.CARETAKER, 5, R.drawable.heimerdinger, Origin.RENEWER))

            champions.add(Champion("Trundle", Origin.DRAGONSLAYER, Origin.SKIRMISHER, 2, R.drawable.trundle))
            champions.add(Champion("Pantheon", Origin.DRAGONSLAYER, Origin.SKIRMISHER, 3, R.drawable.pantheon))
            champions.add(Champion("Diana", Origin.DRAGONSLAYER, Origin.NIGHTBRINGER, 4, R.drawable.diana, Origin.ASSASSIN))
            champions.add(Champion("Mordekaiser", Origin.DRAGONSLAYER, Origin.LEGIONNAIRE, 4, R.drawable.mordekaiser))

            champions.add(Champion("Kindred", Origin.ETERNAL, Origin.MYSTIC, 5, R.drawable.kindred, Origin.RANGER))

            champions.add(Champion("Warwick", Origin.FORGOTTEN, Origin.BRAWLER, 1, R.drawable.warwick))
            champions.add(Champion("Vayne", Origin.FORGOTTEN, Origin.RANGER, 1, R.drawable.vayne))
            champions.add(Champion("Hecarim", Origin.FORGOTTEN, Origin.CAVALIER, 2, R.drawable.hecarim))
            champions.add(Champion("Thresh", Origin.FORGOTTEN, Origin.KNIGHT, 2, R.drawable.thresh))
            champions.add(Champion("Viktor", Origin.FORGOTTEN, Origin.SPELLWEAVER, 2, R.drawable.viktor))
            champions.add(Champion("Katarina", Origin.FORGOTTEN, Origin.ASSASSIN, 3, R.drawable.katarina))
            champions.add(Champion("Draven", Origin.FORGOTTEN, Origin.LEGIONNAIRE, 4, R.drawable.draven))
            champions.add(Champion("Viego", Origin.FORGOTTEN, Origin.ASSASSIN, 5, R.drawable.viego, Origin.SKIRMISHER))

            champions.add(Champion("Kled", Origin.HELLION, Origin.CAVALIER, 1, R.drawable.kled))
            champions.add(Champion("Poppy", Origin.HELLION, Origin.KNIGHT, 1, R.drawable.poppy))
            champions.add(Champion("Ziggs", Origin.HELLION, Origin.SPELLWEAVER, 1, R.drawable.ziggs))
            champions.add(Champion("Kennen", Origin.HELLION, Origin.SKIRMISHER, 2, R.drawable.kennen))
            champions.add(Champion("Lulu", Origin.HELLION, Origin.MYSTIC, 3, R.drawable.lulu))
            champions.add(Champion("Teemo", Origin.HELLION, Origin.CRUEL, 5, R.drawable.teemo, Origin.INVOKER))

            champions.add(Champion("Nautilus", Origin.IRONCLAD, Origin.KNIGHT, 2, R.drawable.nautilus))
            champions.add(Champion("Jax", Origin.IRONCLAD, Origin.SKIRMISHER, 4, R.drawable.jax))
            champions.add(Champion("Rell", Origin.IRONCLAD, Origin.CAVALIER, 4, R.drawable.rell))

            champions.add(Champion("Vladimir", Origin.NIGHTBRINGER, Origin.RENEWER, 1, R.drawable.vladimir))
            champions.add(Champion("Sejuani", Origin.NIGHTBRINGER, Origin.CAVALIER, 2, R.drawable.sejuani))
            champions.add(Champion("Lee Sin", Origin.NIGHTBRINGER, Origin.SKIRMISHER, 3, R.drawable.leesin))
            champions.add(Champion("Yasuo", Origin.NIGHTBRINGER, Origin.LEGIONNAIRE, 3, R.drawable.yasuo))
            champions.add(Champion("Aphelios", Origin.NIGHTBRINGER, Origin.RANGER, 4, R.drawable.aphelios))
            champions.add(Champion("Darius", Origin.NIGHTBRINGER, Origin.KNIGHT, 5, R.drawable.darius, Origin.GODKING))

            champions.add(Champion("Aatrox", Origin.REDEEMED, Origin.LEGIONNAIRE, 1, R.drawable.aatrox))
            champions.add(Champion("Leona", Origin.REDEEMED, Origin.KNIGHT, 1, R.drawable.leona))
            champions.add(Champion("Syndra", Origin.REDEEMED, Origin.INVOKER, 2, R.drawable.syndra))
            champions.add(Champion("Varus", Origin.REDEEMED, Origin.RANGER, 2, R.drawable.varus))
            champions.add(Champion("Lux", Origin.REDEEMED, Origin.MYSTIC, 3, R.drawable.lux))
            champions.add(Champion("Vel'koz", Origin.REDEEMED, Origin.SPELLWEAVER, 4, R.drawable.velkoz))
            champions.add(Champion("Kayle", Origin.REDEEMED, Origin.VERDANT, 5, R.drawable.kayle, Origin.LEGIONNAIRE))

            champions.add(Champion("Nocturne", Origin.REVENANT, Origin.ASSASSIN, 3, R.drawable.nocturne))
            champions.add(Champion("Ivern", Origin.REVENANT, Origin.INVOKER, 4, R.drawable.ivern, Origin.RENEWER))
            champions.add(Champion("Volibear", Origin.REVENANT, Origin.BRAWLER, 5, R.drawable.volibear))

            champions.add(Champion("Taric", Origin.VERDANT, Origin.KNIGHT, 4, R.drawable.taric))
        }

        private fun loadItems() {
            val sword = Item("B.F. Sword", R.drawable.bfsword)
            val bow = Item("Recurve Bow", R.drawable.bow)
            val chain = Item("Chain Vest", R.drawable.chainvest)
            val cloak = Item("Negatron Cloak", R.drawable.cloak)
            val rod = Item("Needlessly Large Rod", R.drawable.rod)
            val tear = Item("Tear of the Goddess", R.drawable.tear)
            val belt = Item("Giant's Belt", R.drawable.belt)
            val glove = Item("Sparring Gloves", R.drawable.glove)
            val spatula = Item("Spatula", R.drawable.spatula)

            itemTable[0][0] = null

            addItem(sword)
            addItem(bow)
            addItem(chain)
            addItem(cloak)
            addItem(rod)
            addItem(tear)
            addItem(belt)
            addItem(glove)
            addItem(spatula)

            addItem("Deathblade", R.drawable.deathblade, sword, sword)
            addItem("Giant Slayer", R.drawable.giantslayer, sword, bow)
            addItem("Guardian Angel", R.drawable.guardianangel, sword, chain, true)
            addItem("Bloodthirster", R.drawable.bloodthirster, sword, cloak)
            addItem("Hextech Gunblade", R.drawable.gunblade, sword, rod)
            addItem("Spear of Shojin", R.drawable.shojin, sword, tear)
            addItem("Zeke's Herald", R.drawable.zekes, sword, belt)
            addItem("Infinity Edge", R.drawable.infinityedge, sword, glove, true)
            addItem("Skirmisher Emblem", R.drawable.skirmisherspat, sword, spatula, origin = Origin.SKIRMISHER)

            addItem("Rapid Firecannon", R.drawable.rfc, bow, bow)
            addItem("Titan's Resolve", R.drawable.titans, bow, chain)
            addItem("Runaan's Hurricane", R.drawable.hurricane, bow, cloak)
            addItem("Guinsoo's Rageblade", R.drawable.guinsoo, bow, rod)
            addItem("Statikk Shiv", R.drawable.shiv, bow, tear)
            addItem("Zz'Rot Portal", R.drawable.zzrot, bow, belt)
            addItem("Last Whisper", R.drawable.lastwhisper, bow, glove, true)
            addItem("Legionnaire Emblem", R.drawable.legionnairespat, bow, spatula, origin = Origin.LEGIONNAIRE)

            addItem("Bramble Vest", R.drawable.bramble, chain, chain)
            addItem("Gargoyle Stoneplate", R.drawable.gargoyle, chain, cloak)
            addItem("Locket of the Iron Solari", R.drawable.locket, chain, rod)
            addItem("Frozen Heart", R.drawable.frozenheart, chain, tear, true)
            addItem("Sunfire Cape", R.drawable.sunfire, chain, belt, true)
            addItem("Shroud of Stillness", R.drawable.shroud, chain, glove, true)
            addItem("Ironclad Emblem", R.drawable.ironcladspat, chain, spatula, origin = Origin.IRONCLAD)

            addItem("Dragon's Claw", R.drawable.dclaw, cloak, cloak)
            addItem("Ionic Spark", R.drawable.spark, cloak, rod)
            addItem("Chalice of Power", R.drawable.chalice, cloak, tear)
            addItem("Zephyr", R.drawable.zephyr, cloak, belt, true)
            addItem("Quicksilver", R.drawable.qss, cloak, glove, true)
            addItem("Redeemed Emblem", R.drawable.redeemedspat, cloak, spatula, origin = Origin.REDEEMED)

            addItem("Rabadon's Deathcap", R.drawable.rabadons, rod, rod)
            addItem("Archangel's Staff", R.drawable.archangels, rod, tear)
            addItem("Morellonomicon", R.drawable.morello, rod, belt, true)
            addItem("Jeweled Gauntlet", R.drawable.jeweled, rod, glove)
            addItem("Spellweaver Emblem", R.drawable.spellweaverspat, rod, spatula, origin = Origin.SPELLWEAVER)

            addItem("Blue Buff", R.drawable.bluebuff, tear, tear, true)
            addItem("Redemption", R.drawable.redemption, tear, belt)
            addItem("Hand of Justice", R.drawable.hoj, tear, glove)
            addItem("Renewer Emblem", R.drawable.renewerspat, tear, spatula, origin = Origin.RENEWER)

            addItem("Warmog's Armor", R.drawable.warmog, belt, belt)
            addItem("Trap Claw", R.drawable.trapclaw, belt, glove, true)
            addItem("Dawnbringer Emblem", R.drawable.dawnbringerspat, belt, spatula, origin = Origin.DAWNBRINGER)

            addItem("Thief's Gloves", R.drawable.thiefs, glove, glove, true)
            addItem("Assassin Emblem", R.drawable.assassinspat, glove, spatula, origin = Origin.ASSASSIN)

            addItem("Force of Nature", R.drawable.fon, spatula, spatula)
        }

        private fun loadShadowItems() {
            val sword = Item("Shadow B.F. Sword", R.drawable.shadowbfsword)
            val bow = Item("Shadow Recurve Bow", R.drawable.shadowbow)
            val chain = Item("Shadow Chain Vest", R.drawable.shadowchainvest)
            val cloak = Item("Shadow Negatron Cloak", R.drawable.shadowcloak)
            val rod = Item("Shadow Needlessly Large Rod", R.drawable.shadowrod)
            val tear = Item("Shadow Tear of the Goddess", R.drawable.shadowtear)
            val belt = Item("Shadow Giant's Belt", R.drawable.shadowbelt)
            val glove = Item("Shadow Sparring Gloves", R.drawable.shadowglove)
            val shadowSpatula = Item("Shadow Spatula", R.drawable.shadowspat)

            itemNum = 1
            shadowItemTable[0][0] = null

            addShadowItem(sword)
            addShadowItem(bow)
            addShadowItem(chain)
            addShadowItem(cloak)
            addShadowItem(rod)
            addShadowItem(tear)
            addShadowItem(belt)
            addShadowItem(glove)
            addShadowItem(shadowSpatula)

            addShadowItem("Caustic Deathblade", R.drawable.shadowdeathblade, sword, sword)
            addShadowItem("Spectral Giant Slayer", R.drawable.shadowgiantslayer, sword, bow)
            addShadowItem("Guardian Fallen Angel", R.drawable.shadowguardianangel, sword, chain, true)
            addShadowItem("Riskthirster", R.drawable.shadowbloodthirster, sword, cloak)
            addShadowItem("Hextech Gunblade Of Immortality", R.drawable.shadowgunblade, sword, rod)
            addShadowItem("Spectral Spear of Shojin", R.drawable.shadowshojin, sword, tear)
            addShadowItem("Zeke's Bleak Herald", R.drawable.shadowzekes, sword, belt)
            addShadowItem("Sacrificial Infinity Edge", R.drawable.shadowinfinityedge, sword, glove, true)
            addShadowItem("Forgotten Emblem", R.drawable.forgottenspat, sword, shadowSpatula, origin = Origin.FORGOTTEN)

            addShadowItem("Rapid Deathcannon", R.drawable.shadowrfc, bow, bow)
            addShadowItem("Titan's Revenge", R.drawable.shadowtitans, bow, chain)
            addShadowItem("Runaan's Untamed Hurricane", R.drawable.shadowhurricane, bow, cloak)
            addShadowItem("Guinsoo's Sacrificial Rageblade", R.drawable.shadowguinsoo, bow, rod)
            addShadowItem("Statikk Stiletto", R.drawable.shadowshiv, bow, tear)
            addShadowItem("Unstable Zz'Rot Portal", R.drawable.shadowzzrot, bow, belt)
            addShadowItem("Final Whisper", R.drawable.shadowlastwhisper, bow, glove, true)
            addShadowItem("Hellion Emblem", R.drawable.hellionspat, bow, shadowSpatula, origin = Origin.HELLION)

            addShadowItem("Refracted Bramble Vest", R.drawable.shadowbramble, chain, chain)
            addShadowItem("Gargoyle Stoneplate Of Immortality", R.drawable.shadowgargoyle, chain, cloak)
            addShadowItem("Locket of the Silver Lunari", R.drawable.shadowlocket, chain, rod)
            addShadowItem("Frozen Dark Heart", R.drawable.shadowfrozenheart, chain, tear, true)
            addShadowItem("Eclipse Cape", R.drawable.shadowsunfire, chain, belt, true)
            addShadowItem("Dark Shroud of Stillness", R.drawable.shadowshroud, chain, glove, true)
            addShadowItem("Cavalier Emblem", R.drawable.cavalierspat, chain, shadowSpatula, origin = Origin.CAVALIER)

            addShadowItem("Refracted Dragon's Claw", R.drawable.shadowdclaw, cloak, cloak)
            addShadowItem("Ionic Dark Spark", R.drawable.shadowspark, cloak, rod)
            addShadowItem("Chalice of Malice", R.drawable.shadowchalice, cloak, tear)
            addShadowItem("Turbulent Zephyr", R.drawable.shadowzephyr, cloak, belt, true)
            addShadowItem("Caustic Quicksilver", R.drawable.shadowqss, cloak, glove, true)
            addShadowItem("Revenant Emblem", R.drawable.revenantspat, cloak, shadowSpatula, origin = Origin.REVENANT)

            addShadowItem("Rabadon's Caustic Deathcap", R.drawable.shadowrabadons, rod, rod)
            addShadowItem("Archangel's Staff Of Immortality", R.drawable.shadowarchangels, rod, tear)
            addShadowItem("Mor-evil-lonomicon", R.drawable.shadowmorello, rod, belt, true)
            addShadowItem("Sacrificial Gauntlet", R.drawable.shadowjeweled, rod, glove)
            addShadowItem("Dragonslayer Emblem", R.drawable.dragonslayerspat, rod, shadowSpatula, origin = Origin.DRAGONSLAYER)

            addShadowItem("Very Dark Blue Buff", R.drawable.shadowbluebuff, tear, tear, true)
            addShadowItem("Sacrificial Redemption", R.drawable.shadowredemption, tear, belt)
            addShadowItem("Hand of Vengeance", R.drawable.shadowhoj, tear, glove)
            addShadowItem("Coven Emblem", R.drawable.covenspat, tear, shadowSpatula, origin = Origin.COVEN)

            addShadowItem("Warmog's Sacrifical Armor", R.drawable.shadowwarmogs, belt, belt)
            addShadowItem("Vengeful Trap Claw", R.drawable.shadowtrapclaw, belt, glove, true)
            addShadowItem("Nightbringer Emblem", R.drawable.nightbringerspat, belt, shadowSpatula, origin = Origin.NIGHTBRINGER)

            addShadowItem("Trickster's Gloves", R.drawable.shadowthiefs, glove, glove, true)
            addShadowItem("Abomination Emblem", R.drawable.abominationspat, glove, shadowSpatula, origin = Origin.ABOMINATION)

            addShadowItem("Force of Darkness", R.drawable.fod, shadowSpatula, shadowSpatula)
        }

        private fun addItem(item: Item) {
            itemTable[0][itemNum] = item
            itemTable[itemNum][0] = item
            itemNum++
            itemIdMap[item.id] = item
        }

        private fun addItem(name: String, imagePath: Int, item1: Item, item2: Item, unique: Boolean = false, origin: Origin = Origin.NONE) {
            val item: Item = if (origin != Origin.NONE) {
                SpatItem(name, imagePath, item1, item2, origin)
            } else {
                FullItem(name, imagePath, item1, item2, unique)
            }
            itemIdMap[item.id] = item
            if (item.imagePath == R.drawable.thiefs) tgId = item.id
            if (item1.id == item2.id) {
                itemNum += itemNum / numRows
            }
            itemTable[itemNum / numRows][itemNum % numRows] = item
            if (item1.id == item2.id) {
                itemNum++
                return
            }
            itemTable[itemNum % numRows][itemNum / numRows] = item
            itemNum++
        }

        private fun addShadowItem(item: Item) {
            shadowItemTable[0][itemNum] = item
            shadowItemTable[itemNum][0] = item
            itemIdMap[item.id] = item
            itemNum++
        }

        private fun addShadowItem(name: String, imagePath: Int, item1: Item, item2: Item, unique: Boolean = false, origin: Origin = Origin.NONE) {
            val item: Item = if (origin != Origin.NONE) {
                SpatItem(name, imagePath, item1, item2, origin)
            } else {
                FullItem(name, imagePath, item1, item2, unique)
            }
            itemIdMap[item.id] = item
            if (item1.id == item2.id) {
                itemNum += itemNum / numRows
            }
            shadowItemTable[itemNum / numRows][itemNum % numRows] = item
            if (item.imagePath == R.drawable.shadowthiefs) shadowTgId = item.id
            if (item1.id == item2.id) {
                itemNum++
                return
            }
            shadowItemTable[itemNum % numRows][itemNum / numRows] = item
            itemNum++
        }

        private fun loadChampionsHashmap() {
            for (champ in champions) {
                championIdMap[champ.id] = champ
            }
        }

        private fun loadTraitsMap() {
            addTrait(Origin.ABOMINATION, arrayOf(3, 4, 5), R.drawable.abomination)
            addTrait(Origin.COVEN, arrayOf(3), R.drawable.coven)
            addTrait(Origin.DAWNBRINGER, arrayOf(2, 4, 6, 8), R.drawable.dawnbringer)
            addTrait(Origin.DRACONIC, arrayOf(3, 5), R.drawable.draconic)
            addTrait(Origin.DRAGONSLAYER, arrayOf(2, 4), R.drawable.dragonslayer)
            addTrait(Origin.ETERNAL, arrayOf(1), R.drawable.eternal)
            addTrait(Origin.FORGOTTEN, arrayOf(3, 6, 9), R.drawable.forgotten)
            addTrait(Origin.HELLION, arrayOf(3, 5, 7), R.drawable.hellion)
            addTrait(Origin.IRONCLAD, arrayOf(2, 4), R.drawable.ironclad)
            addTrait(Origin.NIGHTBRINGER, arrayOf(2, 4, 6, 8), R.drawable.nightbringer)
            addTrait(Origin.REDEEMED, arrayOf(3, 6, 9), R.drawable.redeemed)
            addTrait(Origin.REVENANT, arrayOf(2, 3), R.drawable.revenant)
            addTrait(Origin.VERDANT, arrayOf(2, 3), R.drawable.verdant)
            addTrait(Origin.ASSASSIN, arrayOf(2, 4, 6), R.drawable.assassin)
            addTrait(Origin.BRAWLER, arrayOf(2, 4), R.drawable.brawler)
            addTrait(Origin.CARETAKER, arrayOf(1), R.drawable.caretaker)
            addTrait(Origin.CAVALIER, arrayOf(2, 3, 4), R.drawable.cavalier)
            addTrait(Origin.CRUEL, arrayOf(1), R.drawable.cruel)
            addTrait(Origin.GODKING, arrayOf(1), R.drawable.godking)
            addTrait(Origin.INVOKER, arrayOf(2, 4), R.drawable.invoker)
            addTrait(Origin.KNIGHT, arrayOf(2, 4, 6), R.drawable.knight)
            addTrait(Origin.LEGIONNAIRE, arrayOf(2, 4, 6, 8), R.drawable.legionnaire)
            addTrait(Origin.MYSTIC, arrayOf(2, 3, 4), R.drawable.mystic)
            addTrait(Origin.RANGER, arrayOf(2, 4), R.drawable.ranger)
            addTrait(Origin.RENEWER, arrayOf(2, 4), R.drawable.renewer)
            addTrait(Origin.SKIRMISHER, arrayOf(3, 6), R.drawable.skirmisher)
            addTrait(Origin.SPELLWEAVER, arrayOf(2, 4), R.drawable.spellweaver)
        }

        private fun addTrait(origin: Origin, levels: Array<Int>, imagePath: Int = 0) {
            traitMap[origin] = Trait(origin, levels, imagePath)
        }

        fun isInitialized() : Boolean {
            return initialized
        }

        fun getChampion(id: Int) : Champion {
            return championIdMap[id]!!
        }

        fun getChampionsById(champIds: List<Int>): List<Champion> {
            val filteredMap = championIdMap.filterKeys { champIds.contains(it) }
            return filteredMap.values.toList()
        }

        fun getChampions() : List<Champion> {
            return champions
        }

        fun getAllItems() : List<Item> {
            return itemIdMap.values.toList()
        }

        fun getItem(itemId: Int): Item {
            return itemIdMap[itemId]!!
        }

        fun getItemsById(itemIds: List<Int>): List<Item> {
            val filteredMap = itemIdMap.filterKeys { itemIds.contains(it) }
            return filteredMap.values.toList()
        }

        fun getTraits() : List<Trait> {
            return traitMap.values.toList()
        }

        fun getTrait(origin: Origin) : Trait {
            return traitMap[origin]!!
        }

        fun calculateTeamsTraits(teamComp: List<Team>) : Map<Origin, Int> {
            val distinctChamps = teamComp.groupingBy { it.champId }.aggregate {key, accumulator: StringBuilder?, element, first ->
                if (first)
                    StringBuilder().append(element.items)
                else
                    accumulator!!.append(",").append(element.items)
            }

            val traitMap = HashMap<Origin, Int>() // Trait, count
            for (team in distinctChamps) {
                val champ = getChampion(team.key)
                champ.origins.forEach {
                    val count = traitMap[it]
                    if (count == null)
                        traitMap[it] = 1
                    else
                        traitMap[it] = count + 1
                }
                // Spat item check
                val items = team.value!!.split(",").map { if (it.toInt() == -1) null else getItem(it.toInt()) }
                    .filter { item -> item != null && item is SpatItem }
                items.forEach {
                    if (it != null && it is SpatItem) {
                        val count = traitMap[it.origin]
                        if (count == null)
                            traitMap[it.origin] = 1
                        else
                            traitMap[it.origin] = count + 1
                    }
                }
            }
            return traitMap
        }

        /** UI helpers */
        fun createRow(context: Context?, margin: Int = 0) : TableRow {
            val row = TableRow(context)
            val layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
            if (margin > 0) {
                layoutParams.bottomMargin = margin
                layoutParams.marginEnd = margin
            }
            row.layoutParams = layoutParams
            row.gravity = Gravity.CENTER_VERTICAL
            return row
        }

        fun createImageView(context: Context?, src: Int, layoutParams: ViewGroup.LayoutParams, tooltip: String = "") : ImageView {
            val image = ImageView(context)
            image.layoutParams = layoutParams
            image.adjustViewBounds = true
            image.scaleType = ImageView.ScaleType.FIT_CENTER
            image.setImageResource(src)
            image.tag = src
            image.tooltipText = tooltip
            return image
        }

        // Line chart
        fun createDataSet(dataPoints: List<Entry>, label: String, color: Int) : LineDataSet{
            val dataSet = LineDataSet(dataPoints, label)
            dataSet.setDrawCircles(true)
            dataSet.circleRadius = 4f
            dataSet.setDrawValues(false)
            dataSet.lineWidth = 3f
            dataSet.color = color
            dataSet.setCircleColor(color)
            return dataSet
        }

        fun designLineChart(lineChart: LineChart) {
            // X Axis
            val xAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            // Limit number to 8
            xAxis.axisMaximum = 8f
            xAxis.axisMinimum = 0f

            // Y Axis minimum
            val leftAxis = lineChart.axisLeft
            leftAxis.axisMinimum = 0f
            val rightAxis = lineChart.axisRight
            rightAxis.axisMinimum = 0f

            // No zoom
            lineChart.setScaleEnabled(false)
        }

        fun setLineChartData(lineChart: LineChart, data: List<ILineDataSet>) {
            // Create linechart
            val lineData = LineData(data)
            lineChart.data = lineData
            lineChart.invalidate()
        }

        fun setupAxis(axis: YAxis, max: Float, min: Float, labelCount: Int, inverted: Boolean = false) {
            axis.axisMaximum = max
            axis.axisMinimum = min
            axis.labelCount = labelCount
            axis.isInverted = inverted
        }

        // Error dialog
        fun showErrorDialog(message: String, context: Context?) {
            AlertDialog.Builder(context)
                .setTitle("Errors")
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        fun originName(origin: Origin) : String {
            return origin.name.toLowerCase().capitalize()
        }

        // Measurements
        fun dpToPx(dp: Float, context: Context) : Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
        }

        // Visibility
        fun setVisible(view: View, visible: Boolean) {
            view.visibility = if (visible) View.VISIBLE else View.GONE
        }

        // Placement color
        fun getPlacement(placement: Int, resources: Resources, tv: TextView? = null) : String {
            var s = placement.toString()
            when {
                placement % 10 == 1 -> {
                    s += "st"
                    tv?.setTextColor(resources.getColor(R.color.gold, null))
                }
                placement % 10 == 2 -> {
                    s += "nd"
                    tv?.setTextColor(resources.getColor(R.color.silver, null))
                }
                placement % 10 == 3 -> {
                    s += "rd"
                    tv?.setTextColor(resources.getColor(R.color.bronze, null))
                }
                else -> {
                    s += "th"
                    tv?.setTextColor(resources.getColor(R.color.gray, null))
                }
            }
            return s
        }

        // Trait image based on level
        // 8, 6, 4, 2 -> index = 0, 1, 2, 3
        // 6, 4, 2 -> 0, 1, 2 -> 1, 2, 3
        // 4, 2 -> 0, 1 -> 1, 2
        // 1 -> 0 -> 1
        fun getTraitTint(index: Int, numLevels: Int) : Int {
            val colors = arrayOf(R.color.trait_plat, R.color.trait_gold, R.color.trait_silver, R.color.trait_bronze)
            return when {
                (numLevels == 4) -> colors[index]
                else -> colors[index + 1]
            }
        }

        // Levels is in decreasing order
        fun getTraitTint(desiredLevel: Int, levels: Array<Int>) : Int {
            for ((i, level) in levels.withIndex()) {
                if (desiredLevel >= level) {
                    return getTraitTint(i, levels.size)
                }
            }
            return getTraitTint(0, levels.size)
        }

        /** Math helpers */
        fun Double.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return kotlin.math.round(this * multiplier) / multiplier
        }

        fun <T : Any?> sortAndJoin(map: HashMap<Int, T>) : String {
            return map.toSortedMap().values.joinToString(",")
        }

        fun formatStat(stat: Double) : String {
            val rounded = stat.round(2)
            val intVal = rounded.roundToInt()
            if (rounded - intVal == 0.0) {
                return intVal.toString()
            }
            return rounded.toString()
        }

        fun getLevelXp(level: Int, xp: Int) : Double {
            val maxXp = xpTable[level - 1]
            return level + if (maxXp > 0) (xp.toDouble() / maxXp) else 0.0
        }

        fun measureDistance(view1: View, view2: View) : Int {
            view1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val location1 = IntArray(2)
            view1.getLocationInWindow(location1)
            val location2 = IntArray(2)
            view2.getLocationInWindow(location2)
            return abs(location1[1] - location2[1])
        }
    }
}