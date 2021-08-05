package com.e.tftstats.model

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        const val radiantRows = 9
        val itemTable: Array<Array<Item?>> = Array(numRows) { arrayOfNulls<Item>(numRows) }
        val radiantItemTable: Array<Array<Item?>> = Array(radiantRows) { arrayOfNulls<Item>(radiantRows) }
        val consumableItems: Array<Array<Item?>> = Array(1) { arrayOfNulls<Item>(4) }
        val emblemItems: Array<Array<Item?>> = Array(2) { arrayOfNulls<Item>(14) }

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
        var radiantTgId: Int = -1

        // Used in Home, ItemStats, FinalComp
        val traitColors = arrayOf(R.color.trait_bronze, R.color.trait_silver, R.color.trait_gold, R.color.trait_plat)

        private var itemNum: Int = 1

        fun loadAssets() {
            loadChampions()
            loadItems()
            loadRadiantItems()
            loadConsumables()
            loadEmblems()
            loadChampionsHashmap()
            loadTraitsMap()
            initialized = true
        }

        private fun loadChampions() {
            champions.add(Champion("Kalista", Origin.ABOMINATION, Origin.LEGIONNAIRE, 1, R.drawable.kalista))
            champions.add(Champion("Brand", Origin.ABOMINATION, Origin.SPELLWEAVER, 2, R.drawable.brand))
            champions.add(Champion("Nunu & Willump", Origin.ABOMINATION, Origin.BRAWLER, 3, R.drawable.nunu))
            champions.add(Champion("Fiddlesticks", Origin.ABOMINATION, Origin.REVENANT, 4, R.drawable.fiddlesticks, Origin.MYSTIC))

            champions.add(Champion("Gragas", Origin.DAWNBRINGER, Origin.BRAWLER, 1, R.drawable.gragas))
            champions.add(Champion("Khazix", Origin.DAWNBRINGER, Origin.ASSASSIN, 1, R.drawable.khazix))
            champions.add(Champion("Soraka", Origin.DAWNBRINGER, Origin.RENEWER, 2, R.drawable.soraka))
            champions.add(Champion("Nidalee", Origin.DAWNBRINGER, Origin.SKIRMISHER, 3, R.drawable.nidalee))
            champions.add(Champion("Riven", Origin.DAWNBRINGER, Origin.LEGIONNAIRE, 3, R.drawable.riven))
            champions.add(Champion("Karma", Origin.DAWNBRINGER, Origin.INVOKER, 4, R.drawable.karma))
            champions.add(Champion("Garen", Origin.DAWNBRINGER, Origin.KNIGHT, 5, R.drawable.garen, Origin.VICTORIOUS))

            champions.add(Champion("Udyr", Origin.DRACONIC, Origin.SKIRMISHER, 1, R.drawable.udyr))
            champions.add(Champion("Sett", Origin.DRACONIC, Origin.BRAWLER, 2, R.drawable.sett))
            champions.add(Champion("Ashe", Origin.DRACONIC, Origin.RANGER, 3, R.drawable.ashe))
            champions.add(Champion("Zyra", Origin.DRACONIC, Origin.SPELLWEAVER, 3, R.drawable.zyra))
            champions.add(Champion("Galio", Origin.DRACONIC, Origin.SENTINEL, 4, R.drawable.galio, Origin.KNIGHT))
            champions.add(Champion("Heimerdinger", Origin.DRACONIC, Origin.CARETAKER, 5, R.drawable.heimerdinger, Origin.RENEWER))

            champions.add(Champion("Vayne", Origin.FORGOTTEN, Origin.RANGER, 1, R.drawable.vayne))
            champions.add(Champion("Hecarim", Origin.FORGOTTEN, Origin.CAVALIER, 2, R.drawable.hecarim))
            champions.add(Champion("Thresh", Origin.FORGOTTEN, Origin.KNIGHT, 2, R.drawable.thresh))
            champions.add(Champion("Miss Fortune", Origin.FORGOTTEN, Origin.CANNONEER, 3, R.drawable.missfortune))
            champions.add(Champion("Draven", Origin.FORGOTTEN, Origin.LEGIONNAIRE, 4, R.drawable.draven))
            champions.add(Champion("Viego", Origin.FORGOTTEN, Origin.ASSASSIN, 5, R.drawable.viego, Origin.SKIRMISHER))

            champions.add(Champion("Kled", Origin.HELLION, Origin.CAVALIER, 1, R.drawable.kled))
            champions.add(Champion("Poppy", Origin.HELLION, Origin.KNIGHT, 1, R.drawable.poppy))
            champions.add(Champion("Ziggs", Origin.HELLION, Origin.SPELLWEAVER, 1, R.drawable.ziggs))
            champions.add(Champion("Kennen", Origin.HELLION, Origin.SKIRMISHER, 2, R.drawable.kennen))
            champions.add(Champion("Tristana", Origin.HELLION, Origin.CANNONEER, 2, R.drawable.tristana))
            champions.add(Champion("Lulu", Origin.HELLION, Origin.MYSTIC, 3, R.drawable.lulu))
            champions.add(Champion("Teemo", Origin.HELLION, Origin.CRUEL, 5, R.drawable.teemo, Origin.INVOKER))

            champions.add(Champion("Gwen", Origin.INANIMATE, Origin.MYSTIC, 5, R.drawable.gwen))

            champions.add(Champion("Nautilus", Origin.IRONCLAD, Origin.KNIGHT, 2, R.drawable.nautilus))
            champions.add(Champion("Jax", Origin.IRONCLAD, Origin.SKIRMISHER, 4, R.drawable.jax))
            champions.add(Champion("Rell", Origin.REDEEMED, Origin.IRONCLAD, 4, R.drawable.rell, Origin.CAVALIER))

            champions.add(Champion("Vladimir", Origin.NIGHTBRINGER, Origin.RENEWER, 1, R.drawable.vladimir))
            champions.add(Champion("Sejuani", Origin.NIGHTBRINGER, Origin.CAVALIER, 2, R.drawable.sejuani))
            champions.add(Champion("Lee Sin", Origin.NIGHTBRINGER, Origin.SKIRMISHER, 3, R.drawable.leesin))
            champions.add(Champion("Yasuo", Origin.NIGHTBRINGER, Origin.LEGIONNAIRE, 3, R.drawable.yasuo))
            champions.add(Champion("Aphelios", Origin.NIGHTBRINGER, Origin.RANGER, 4, R.drawable.aphelios))
            champions.add(Champion("Diana", Origin.NIGHTBRINGER, Origin.ASSASSIN, 4, R.drawable.diana))

            champions.add(Champion("Aatrox", Origin.REDEEMED, Origin.LEGIONNAIRE, 1, R.drawable.aatrox))
            champions.add(Champion("Leona", Origin.REDEEMED, Origin.KNIGHT, 1, R.drawable.leona))
            champions.add(Champion("Syndra", Origin.REDEEMED, Origin.INVOKER, 2, R.drawable.syndra))
            champions.add(Champion("Varus", Origin.REDEEMED, Origin.RANGER, 2, R.drawable.varus))
            champions.add(Champion("Lux", Origin.REDEEMED, Origin.MYSTIC, 3, R.drawable.lux))
            champions.add(Champion("Vel'koz", Origin.REDEEMED, Origin.SPELLWEAVER, 4, R.drawable.velkoz))
            champions.add(Champion("Kayle", Origin.REDEEMED, Origin.LEGIONNAIRE, 5, R.drawable.kayle))

            champions.add(Champion("Nocturne", Origin.REVENANT, Origin.ASSASSIN, 3, R.drawable.nocturne))
            champions.add(Champion("Ivern", Origin.REVENANT, Origin.INVOKER, 4, R.drawable.ivern, Origin.RENEWER))
            champions.add(Champion("Volibear", Origin.REVENANT, Origin.BRAWLER, 5, R.drawable.volibear))

            champions.add(Champion("Senna", Origin.SENTINEL, Origin.CANNONEER, 1, R.drawable.senna))
            champions.add(Champion("Olaf", Origin.SENTINEL, Origin.SKIRMISHER, 1, R.drawable.olaf))
            champions.add(Champion("Pyke", Origin.SENTINEL, Origin.ASSASSIN, 2, R.drawable.pyke))
            champions.add(Champion("Irelia", Origin.SENTINEL, Origin.LEGIONNAIRE, 2, R.drawable.irelia, Origin.SKIRMISHER))
            champions.add(Champion("Rakan", Origin.SENTINEL, Origin.RENEWER, 3, R.drawable.rakan))
            champions.add(Champion("Lucian", Origin.SENTINEL, Origin.CANNONEER, 4, R.drawable.lucian))
            champions.add(Champion("Akshan", Origin.SENTINEL, Origin.RANGER, 5, R.drawable.akshan))
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

            addItem("Deathblade", R.drawable.deathblade, true)
            addItem("Giant Slayer", R.drawable.giantslayer)
            addItem("Guardian Angel", R.drawable.guardianangel, unique = true)
            addItem("Bloodthirster", R.drawable.bloodthirster)
            addItem("Hextech Gunblade", R.drawable.gunblade)
            addItem("Spear of Shojin", R.drawable.shojin)
            addItem("Zeke's Herald", R.drawable.zekes)
            addItem("Infinity Edge", R.drawable.infinityedge, unique = true)
            addItem("Skirmisher Emblem", R.drawable.skirmisherspat, origin = Origin.SKIRMISHER)

            addItem("Rapid Firecannon", R.drawable.rfc,true)
            addItem("Titan's Resolve", R.drawable.titans)
            addItem("Runaan's Hurricane", R.drawable.hurricane)
            addItem("Guinsoo's Rageblade", R.drawable.guinsoo)
            addItem("Statikk Shiv", R.drawable.shiv)
            addItem("Zz'Rot Portal", R.drawable.zzrot)
            addItem("Last Whisper", R.drawable.lastwhisper, unique = true)
            addItem("Hellion Emblem", R.drawable.hellionspat, origin = Origin.HELLION)

            addItem("Bramble Vest", R.drawable.bramble, true)
            addItem("Gargoyle Stoneplate", R.drawable.gargoyle)
            addItem("Locket of the Iron Solari", R.drawable.locket)
            addItem("Frozen Heart", R.drawable.frozenheart, unique = true)
            addItem("Sunfire Cape", R.drawable.sunfire, unique = true)
            addItem("Shroud of Stillness", R.drawable.shroud, unique = true)
            addItem("Cavalier Emblem", R.drawable.cavalierspat, origin = Origin.CAVALIER)

            addItem("Dragon's Claw", R.drawable.dclaw, true)
            addItem("Ionic Spark", R.drawable.spark)
            addItem("Chalice of Power", R.drawable.chalice)
            addItem("Zephyr", R.drawable.zephyr, unique = true)
            addItem("Quicksilver", R.drawable.qss, unique = true)
            addItem("Redeemed Emblem", R.drawable.redeemedspat, origin = Origin.REDEEMED)

            addItem("Rabadon's Deathcap", R.drawable.rabadons, true)
            addItem("Archangel's Staff", R.drawable.archangels)
            addItem("Morellonomicon", R.drawable.morello, unique = true)
            addItem("Jeweled Gauntlet", R.drawable.jeweled)
            addItem("Spellweaver Emblem", R.drawable.spellweaverspat, origin = Origin.SPELLWEAVER)

            addItem("Blue Buff", R.drawable.bluebuff,true, unique = true)
            addItem("Redemption", R.drawable.redemption)
            addItem("Hand of Justice", R.drawable.hoj)
            addItem("Renewer Emblem", R.drawable.renewerspat, origin = Origin.RENEWER)

            addItem("Warmog's Armor", R.drawable.warmog, true)
            addItem("Trap Claw", R.drawable.trapclaw, unique = true)
            addItem("Dawnbringer Emblem", R.drawable.dawnbringerspat,  origin = Origin.DAWNBRINGER)

            addItem("Thief's Gloves", R.drawable.thiefs, true, unique = true)
            addItem("Assassin Emblem", R.drawable.assassinspat, origin = Origin.ASSASSIN)

            addItem("Force of Nature", R.drawable.fon, true)
        }

        private fun loadRadiantItems() {
            val sword = Item("Radiant B.F. Sword", R.drawable.radiantbfsword)
            val bow = Item("Radiant Recurve Bow", R.drawable.radiantbow)
            val chain = Item("Radiant Chain Vest", R.drawable.radiantchainvest)
            val cloak = Item("Radiant Negatron Cloak", R.drawable.radiantcloak)
            val rod = Item("Radiant Needlessly Large Rod", R.drawable.radiantrod)
            val tear = Item("Radiant Tear of the Goddess", R.drawable.radianttear)
            val belt = Item("Radiant Giant's Belt", R.drawable.radiantbelt)
            val glove = Item("Radiant Sparring Gloves", R.drawable.radiantglove)

            itemNum = 1
            radiantItemTable[0][0] = null

            addRadiantItem(sword)
            addRadiantItem(bow)
            addRadiantItem(chain)
            addRadiantItem(cloak)
            addRadiantItem(rod)
            addRadiantItem(tear)
            addRadiantItem(belt)
            addRadiantItem(glove)

            addRadiantItem("Luminous Deathblade", R.drawable.radiantdeathblade, true)
            addRadiantItem("Demon Slayer", R.drawable.radiantgiantslayer)
            addRadiantItem("Guardian Archangel", R.drawable.radiantguardianangel, unique = true)
            addRadiantItem("Blessed Bloodthirster", R.drawable.radiantbloodthirster)
            addRadiantItem("Hextech Lifeblade", R.drawable.radiantgunblade)
            addRadiantItem("Spear of Hirana", R.drawable.radiantshojin)
            addRadiantItem("Zeke's Harmony", R.drawable.radiantzekes)
            addRadiantItem("Zenith Edge", R.drawable.radiantinfinityedge, unique = true)

            addRadiantItem("Rapid Lightcannon", R.drawable.radiantrfc, true)
            addRadiantItem("Titan's Vow", R.drawable.radianttitans)
            addRadiantItem("Runaan's Tempest", R.drawable.radianthurricane)
            addRadiantItem("Guinsoo's Reckoning", R.drawable.radiantguinsoo)
            addRadiantItem("Statikk Favor", R.drawable.radiantshiv)
            addRadiantItem("Zz'Rot's Invitation", R.drawable.radiantzzrot)
            addRadiantItem("Eternal Whisper", R.drawable.radiantlastwhisper, unique = true)

            addRadiantItem("Rosethorn Vest", R.drawable.radiantbramble, true)
            addRadiantItem("Dvrapala Stoneplate", R.drawable.radiantgargoyle)
            addRadiantItem("Locket of Targon Prime", R.drawable.radiantlocket)
            addRadiantItem("Frozen Heart Of Gold", R.drawable.radiantfrozenheart, unique = true)
            addRadiantItem("Sunlight Cape", R.drawable.radiantsunfire, unique = true)
            addRadiantItem("Shroud of Reverance", R.drawable.radiantshroud, unique = true)

            addRadiantItem("Dragon's Will", R.drawable.radiantdclaw, true)
            addRadiantItem("Covalent Spark", R.drawable.radiantspark)
            addRadiantItem("Chalice of Charity", R.drawable.radiantchalice)
            addRadiantItem("Mistral", R.drawable.radiantzephyr, unique = true)
            addRadiantItem("Quickestsilver", R.drawable.radiantqss, unique = true)

            addRadiantItem("Rabadon's Ascended Deathcap", R.drawable.radiantrabadons, true)
            addRadiantItem("Urf-Angel's Staff", R.drawable.radiantarchangels)
            addRadiantItem("More More-ellonomicon", R.drawable.radiantmorello, unique = true)
            addRadiantItem("Glamorous Gauntlet", R.drawable.radiantjeweled)

            addRadiantItem("Blue Blessing", R.drawable.radiantbluebuff, true, unique = true)
            addRadiantItem("Radiant Redemption", R.drawable.radiantredemption)
            addRadiantItem("Fist of Fairness", R.drawable.radianthoj)

            addRadiantItem("Warmog's Pride", R.drawable.radiantwarmogs, true)
            addRadiantItem("Banshee's Silence", R.drawable.radianttrapclaw, unique = true)

            addRadiantItem("Rascal's Gloves", R.drawable.radiantthiefs, true, unique = true)
        }

        private fun loadConsumables() {
            consumableItems[0][0] = Item("Neeko's Help", R.drawable.neekohelp, true)
            consumableItems[0][1] = Item("Reforger", R.drawable.reforger, true)
            consumableItems[0][2] = Item("Magnetic Remover", R.drawable.remover, true)
            consumableItems[0][3] = Item("Loaded Dice", R.drawable.loadeddice, true)
            itemIdMap[consumableItems[0][0]!!.id] = consumableItems[0][0]!!
            itemIdMap[consumableItems[0][1]!!.id] = consumableItems[0][1]!!
            itemIdMap[consumableItems[0][2]!!.id] = consumableItems[0][2]!!
            itemIdMap[consumableItems[0][3]!!.id] = consumableItems[0][3]!!
        }

        private fun loadEmblems() {
            itemNum = 0
            addEmblem("Abomination Emblem", R.drawable.abomspat, Origin.ABOMINATION)
            addEmblem("Brawler Emblem", R.drawable.brawlerspat, Origin.BRAWLER)
            addEmblem("Cannoneer Emblem", R.drawable.cannoneerspat, Origin.CANNONEER)
            addEmblem("Draconic Emblem", R.drawable.draconicspat, Origin.DRACONIC)
            addEmblem("Forgotten Emblem", R.drawable.forgottenspat, Origin.FORGOTTEN)
            addEmblem("Invoker Emblem", R.drawable.invokerspat, Origin.INVOKER)
            addEmblem("Ironclad Emblem", R.drawable.ironcladspat, Origin.IRONCLAD)
            addEmblem("Knight Emblem", R.drawable.knightspat, Origin.KNIGHT)
            addEmblem("Legionnaire Emblem", R.drawable.legionnairespat, Origin.LEGIONNAIRE)
            addEmblem("Mystic Emblem", R.drawable.mysticspat, Origin.MYSTIC)
            addEmblem("Nightbringer Emblem", R.drawable.nightbringerspat, Origin.NIGHTBRINGER)
            addEmblem("Ranger Emblem", R.drawable.rangerspat, Origin.RANGER)
            addEmblem("Revenant Emblem", R.drawable.revenantspat, Origin.REVENANT)
            addEmblem("Sentinel Emblem", R.drawable.sentinelspat, Origin.SENTINEL)
        }

        private fun addItem(item: Item) {
            itemTable[0][itemNum] = item
            itemTable[itemNum][0] = item
            itemNum++
            itemIdMap[item.id] = item
        }

        private fun addItem(name: String, imagePath: Int, double: Boolean = false, unique: Boolean = false, origin: Origin = Origin.NONE) {
            val item: Item = if (origin != Origin.NONE) {
                SpatItem(name, imagePath, origin)
            } else {
                Item(name, imagePath, unique)
            }
            itemIdMap[item.id] = item
            if (item.imagePath == R.drawable.thiefs) tgId = item.id
            if (double) {
                itemNum += itemNum / numRows
            }
            itemTable[itemNum / numRows][itemNum % numRows] = item
            if (double) {
                itemNum++
                return
            }
            itemTable[itemNum % numRows][itemNum / numRows] = item
            itemNum++
        }

        private fun addRadiantItem(item: Item) {
            radiantItemTable[0][itemNum] = item
            radiantItemTable[itemNum][0] = item
            itemIdMap[item.id] = item
            itemNum++
        }

        private fun addRadiantItem(name: String, imagePath: Int, double: Boolean = false, unique: Boolean = false, origin: Origin = Origin.NONE) {
            val item: Item = if (origin != Origin.NONE) {
                SpatItem(name, imagePath, origin)
            } else {
                Item(name, imagePath, unique)
            }
            itemIdMap[item.id] = item
            if (double) {
                itemNum += itemNum / radiantRows
            }
            radiantItemTable[itemNum / radiantRows][itemNum % radiantRows] = item
            if (item.imagePath == R.drawable.radiantthiefs) radiantTgId = item.id
            if (double) {
                itemNum++
                return
            }
            radiantItemTable[itemNum % radiantRows][itemNum / radiantRows] = item
            itemNum++
        }

        private fun addEmblem(name: String, imagePath: Int, origin: Origin) {
            val item = SpatItem(name, imagePath, origin)
            emblemItems[itemNum / numRows][itemNum % numRows] = item
            itemIdMap[emblemItems[itemNum / numRows][itemNum % numRows]!!.id] = emblemItems[itemNum / numRows][itemNum % numRows]!!
            itemNum++
        }

        private fun loadChampionsHashmap() {
            for (champ in champions) {
                championIdMap[champ.id] = champ
            }
        }

        private fun loadTraitsMap() {
            // arrayOf(R.color.trait_bronze, R.color.trait_silver, R.color.trait_gold, R.color.trait_plat)
            addTrait(Origin.ABOMINATION, arrayOf(3, 4, 5), arrayOf(1, 2, 3), R.drawable.abomination)
            addTrait(Origin.DAWNBRINGER, arrayOf(2, 4, 6, 8), arrayOf(0, 1, 2, 3), R.drawable.dawnbringer)
            addTrait(Origin.DRACONIC, arrayOf(3, 5), arrayOf(0, 2), R.drawable.draconic)
            addTrait(Origin.FORGOTTEN, arrayOf(2, 4, 6, 8), arrayOf(0, 1, 2, 3), R.drawable.forgotten)
            addTrait(Origin.HELLION, arrayOf(2, 4, 6,  8), arrayOf(0, 1, 2, 3), R.drawable.hellion)
            addTrait(Origin.INANIMATE, arrayOf(1), arrayOf(2), R.drawable.inanimate)
            addTrait(Origin.IRONCLAD, arrayOf(2, 3, 4), arrayOf(0, 2, 3), R.drawable.ironclad)
            addTrait(Origin.NIGHTBRINGER, arrayOf(2, 4, 6, 8), arrayOf(0, 1, 2, 3), R.drawable.nightbringer)
            addTrait(Origin.REDEEMED, arrayOf(3, 6, 9), arrayOf(0, 2, 3), R.drawable.redeemed)
            addTrait(Origin.REVENANT, arrayOf(2, 3, 4, 5), arrayOf(0, 1, 2, 3), R.drawable.revenant)
            addTrait(Origin.SENTINEL, arrayOf(3, 6, 9), arrayOf(0, 1, 2), R.drawable.sentinel)
            addTrait(Origin.VICTORIOUS, arrayOf(1), arrayOf(2), R.drawable.victorious)
            addTrait(Origin.ASSASSIN, arrayOf(2, 4, 6), arrayOf(0, 1, 2), R.drawable.assassin)
            addTrait(Origin.BRAWLER, arrayOf(2, 4, 6), arrayOf(0, 2, 3), R.drawable.brawler)
            addTrait(Origin.CANNONEER, arrayOf(2, 4, 6), arrayOf(0, 2, 3), R.drawable.cannoneer)
            addTrait(Origin.CARETAKER, arrayOf(1), arrayOf(2), R.drawable.caretaker)
            addTrait(Origin.CAVALIER, arrayOf(2, 3, 4), arrayOf(0, 1, 2), R.drawable.cavalier)
            addTrait(Origin.CRUEL, arrayOf(1), arrayOf(2), R.drawable.cruel)
            addTrait(Origin.INVOKER, arrayOf(2, 4), arrayOf(0, 2), R.drawable.invoker)
            addTrait(Origin.KNIGHT, arrayOf(2, 4, 6), arrayOf(0, 1, 2), R.drawable.knight)
            addTrait(Origin.LEGIONNAIRE, arrayOf(2, 4, 6, 8), arrayOf(0, 1, 2, 3), R.drawable.legionnaire)
            addTrait(Origin.MYSTIC, arrayOf(2, 3, 4, 5), arrayOf(0, 1, 2, 3), R.drawable.mystic)
            addTrait(Origin.RANGER, arrayOf(2, 4, 6), arrayOf(0, 2, 3), R.drawable.ranger)
            addTrait(Origin.RENEWER, arrayOf(2, 4, 6), arrayOf(0, 2, 3), R.drawable.renewer)
            addTrait(Origin.SKIRMISHER, arrayOf(3, 6, 9), arrayOf(0, 2, 3), R.drawable.skirmisher)
            addTrait(Origin.SPELLWEAVER, arrayOf(2, 4, 6), arrayOf(0, 2, 3), R.drawable.spellweaver)
        }

        private fun addTrait(origin: Origin, levels: Array<Int>, colors: Array<Int>, imagePath: Int = 0) {
            traitMap[origin] = Trait(origin, levels, colors, imagePath)
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

        fun createSmallButton(context: Context?, text: String) : Button {
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
            return origin.name.toLowerCase(Locale.getDefault()).capitalize()
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