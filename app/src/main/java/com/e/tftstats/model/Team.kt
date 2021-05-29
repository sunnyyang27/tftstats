package com.e.tftstats.model

import androidx.room.*

/** Final Team Comp
 *  Each object represents a champion and their items */
@Entity(
    foreignKeys = [
        (ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ))]
)
data class Team (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,                                // unique
    @ColumnInfo(name = "gameId", index = true) var gameId: Int,
    @ColumnInfo(name = "champId") var champId: Int,         // store id
    @ColumnInfo(name = "items") var items: String = "",         // store ids as csv
    @ColumnInfo(name = "starLevel") var starLevel: Int,     // 1 2 3
    @ColumnInfo(name = "isCarry") var isCarry: Boolean = false
)

class ChampStatsTuple {
    @ColumnInfo(name = "count")
    var count: Int = 0

    @ColumnInfo(name = "avgPlacement")
    var avgPlacement: Double = 0.0

    @ColumnInfo(name = "avgStarLevel")
    var avgStarLevel: Double = 0.0

    @ColumnInfo(name = "carryCount")
    var carryCount: Int = 0
}

class ChampIdStatsTuple {
    @ColumnInfo(name = "champId")
    var champId: Int = 0

    @ColumnInfo(name = "count")
    var count: Int = 0

    @ColumnInfo(name = "avgPlacement")
    var avgPlacement: Double = 0.0

    @ColumnInfo(name = "avgStarLevel")
    var avgStarLevel: Double = 0.0

    @ColumnInfo(name = "carryCount")
    var carryCount: Int = 0
}

class ChampCostAndPlacementTuple {
    @ColumnInfo(name = "champId")
    var champId: Int = 0

    @ColumnInfo(name = "gameId")
    var gameId: Int = 0

    @ColumnInfo(name = "placement")
    var placement: Int = 0

    @ColumnInfo(name = "starLevel")
    var starLevel: Int = 0
}

class ItemsAndPlacementTuple {
    @ColumnInfo(name = "items")
    var items: String? = null

    @ColumnInfo(name = "placement")
    var placement: Int = 0
}

@Dao
interface TeamDao {
    @Query("SELECT * FROM Team")
    fun getAll(): List<Team>

    @Query("SELECT * FROM Team where champId = :champId")
    fun getTeamById(champId: Int) : List<Team>

    // Used in FinalCompStatsFragment sections 1 and 2, HomeFragment
    @Query("SELECT * FROM Team where gameId = :gameId")
    fun getTeamByGame(gameId: Int): List<Team>

    // Used in FinalCompStatsFragment sections 3 and 4
    @Query("SELECT DISTINCT champId FROM Team")
    fun getUniqueChampIds(): List<Int>

    @Query("SELECT COUNT(*) count, AVG(G.placement) avgPlacement, AVG(T.starLevel) avgStarLevel, " +
            "COUNT(CASE WHEN T.isCarry = 1 THEN 1 END) carryCount " +
            "FROM Game G JOIN Team T on T.gameId = G.id WHERE T.champId = :champId")
    fun getAvgStatsByChamp(champId: Int): ChampStatsTuple

    @Query("SELECT champId, COUNT(*) count, AVG(G.placement) avgPlacement, AVG(T.starLevel) avgStarLevel, " +
            "COUNT(CASE WHEN T.isCarry = 1 THEN 1 END) carryCount, items FROM Game G JOIN Team T on T.gameId = G.id " +
            "GROUP BY T.champId ORDER BY avgPlacement, count desc, avgStarLevel desc, carryCount desc LIMIT 3")
    fun getAvgChampStatsTopThree(): List<ChampIdStatsTuple>

    @Query("SELECT items, placement FROM Team T JOIN Game G on T.gameId = G.id WHERE champId = :champId")
    fun getItemsAndPlacementByChamp(champId: Int) : List<ItemsAndPlacementTuple>

    // Used in FinalCompStatsFragment section 5
    @Query("SELECT champId, starLevel, gameId, placement FROM Team JOIN Game on Team.gameId = Game.id")
    fun getChampCostAndPlacement() : List<ChampCostAndPlacementTuple>

    @Update
    fun updateTeams(teams: List<Team>)

    @Insert
    fun insert(vararg teams: Team)

    @Insert
    fun insertTeams(teams: List<Team>)

    @Delete
    fun deleteTeams(teams: List<Team>)

    @Query("DELETE FROM Team")
    fun deleteAll()
}