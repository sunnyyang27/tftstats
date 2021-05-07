package com.e.tftstats.model

import androidx.room.*
import androidx.room.ColumnInfo

/** Status at end of each stage */

@Entity(
    foreignKeys = [
        (ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ))]
)

data class Stage (
    @PrimaryKey(autoGenerate = true) var id: Long,               // unique, generate w/ DB
    @ColumnInfo(name = "gameId", index = true) var gameId: Int?,
    @ColumnInfo(name = "stageNumber") var stageNumber: Int,
    @ColumnInfo(name = "gold") var gold: Int,
    @ColumnInfo(name = "health") var health: Int,
    @ColumnInfo(name = "placement") var placement: Int,
    @ColumnInfo(name = "level") var level: Int = 0,
    @ColumnInfo(name = "xp") var xp: Int = 0,
    @ColumnInfo(name = "armoryItem") var armoryItem: Int = -1,          // null if died before
    @ColumnInfo(name = "carouselItem") var carouselItem: Int = -1,      // null if died before
    @ColumnInfo(name = "pveItems") var pveItems: String = "",        // empty if died before
    @Ignore
    var pveItemsMap: HashMap<Int, Int> = hashMapOf(),              // view id, item id
    @Ignore
    var pveItemCounter: Int = 0

) {
    constructor() : this(0, null, 0, 0, 0, 0, 0, 0, -1, -1, "")
}

class LevelTuple {
    @ColumnInfo(name = "level")
    var level: Int = 0

    @ColumnInfo(name = "xp")
    var xp: Int = 0
}

class ItemPlacementTuple {
    @ColumnInfo(name = "item")
    var item: Int = 0

    @ColumnInfo(name = "avgPlacement")
    var avgPlacement: Double = 0.0
}

class PvePlacementTuple {
    @ColumnInfo(name = "pveItems")
    var pveItems: String? = null

    @ColumnInfo(name = "placement")
    var placement: Int = 0

    @ColumnInfo(name = "stageNumber")
    var stageNumber: Int = 0
}

@Dao
interface StageDao {
    @Query("SELECT * FROM Stage")
    fun getAll(): List<Stage>

    @Query("SELECT * FROM Stage WHERE gameId = :gameId")
    fun getByGame(gameId: Int): List<Stage>

    // Regular Stats pages
    @Query("SELECT COUNT(*) FROM Stage WHERE stageNumber = :stageNumber and gameId in (SELECT id from Game where placement = :placement)")
    fun hasStatPerPlacementAndStage(placement: Int, stageNumber: Int): Int

    @Query("SELECT AVG(health) FROM Stage WHERE stageNumber = :stageNumber and gameId in (SELECT id from Game where placement = :placement)")
    fun getAvgHealthPerPlacementAndStage(placement: Int, stageNumber: Int): Double

    @Query("SELECT AVG(gold) FROM Stage WHERE stageNumber = :stageNumber and gameId in (SELECT id from Game where placement = :placement)")
    fun getAvgGoldPerPlacementAndStage(placement: Int, stageNumber: Int): Double

    @Query("SELECT level, xp FROM Stage WHERE stageNumber = :stageNumber and gameId in (SELECT id from Game where placement = :placement)")
    fun getLevelAndXpPerPlacementAndStage(placement: Int, stageNumber: Int): List<LevelTuple>

    @Query("SELECT AVG(placement) FROM Stage WHERE stageNumber = :stageNumber and gameId in (SELECT id from Game where placement = :placement)")
    fun getAvgPlacementPerPlacementAndStage(placement: Int, stageNumber: Int): Double

    @Query("SELECT COUNT(*) FROM Stage WHERE stageNumber = :stageNumber")
    fun hasStatPerStage(stageNumber: Int): Int

    @Query("SELECT AVG(health) FROM Stage WHERE stageNumber = :stageNumber")
    fun getAvgHealthPerStage(stageNumber: Int): Double

    @Query("SELECT AVG(gold) FROM Stage WHERE stageNumber = :stageNumber")
    fun getAvgGoldPerStage(stageNumber: Int): Double

    @Query("SELECT level, xp FROM Stage WHERE stageNumber = :stageNumber")
    fun getLevelAndXpPerStage(stageNumber: Int): List<LevelTuple>

    @Query("SELECT AVG(placement) FROM Stage WHERE stageNumber = :stageNumber")
    fun getAvgPlacementPerStage(stageNumber: Int): Double

    @Query("SELECT MAX(stageNumber) FROM Stage")
    fun getMaxStage(): Int

    /** Item Stats page */
    @Query("SELECT MAX(stageNumber) FROM Stage WHERE carouselItem not null or armoryItem not null")
    fun getMaxStageWithItem(): Int

    // Section 1 - not used anymore
    @Query("SELECT armoryItem FROM Stage WHERE armoryItem not null UNION SELECT carouselItem from Stage where carouselItem not null")
    fun getItems() : List<Int>

    @Query("SELECT AVG(G.placement) FROM Stage S JOIN Game G on S.gameId = G.id where armoryItem = :armoryItem and stageNumber = :stageNumber")
    fun getAvgPlacementPerArmoryAndStage(armoryItem: Int, stageNumber: Int): Double
    @Query("SELECT AVG(G.placement) FROM Stage S JOIN Game G on S.gameId = G.id where carouselItem = :carouselItem and stageNumber = :stageNumber")
    fun getAvgPlacementPerCarouselAndStage(carouselItem: Int, stageNumber: Int): Double

    @Query("SELECT stageNumber stageNumber, G.placement placement, pveItems FROM Stage S JOIN Game G on S.gameId = G.id")
    fun getPveItemsWithFinalPlacement() : List<PvePlacementTuple>

    // Section 2
    @Query("SELECT armoryItem item, avg(G.placement) avgPlacement FROM Stage S JOIN Game G on S.gameId = G.id where stageNumber = :stageNumber GROUP BY armoryItem ORDER BY avgPlacement LIMIT 1")
    fun getArmoryItemWithMaxPlacementPerStage(stageNumber: Int): ItemPlacementTuple?

    @Query("SELECT carouselItem item, avg(G.placement) avgPlacement FROM Stage S JOIN Game G on S.gameId = G.id where stageNumber = :stageNumber GROUP BY carouselItem ORDER BY avgPlacement LIMIT 1")
    fun getCarouselItemWithMaxPlacementPerStage(stageNumber: Int): ItemPlacementTuple?

    @Update
    fun updateStages(vararg stages: Stage)

    @Insert
    fun insert(vararg stages: Stage)

    @Delete
    fun deleteStages(vararg stages: Stage)

    @Query("DELETE FROM Stage")
    fun deleteAll()
}
