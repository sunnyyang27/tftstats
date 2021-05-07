package com.e.tftstats.model

import androidx.room.*

@Entity
data class Game (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,                 // unique, generate w/ DB
    @ColumnInfo(name = "stageDied") var stageDied: Int,
    @ColumnInfo(name = "roundDied") var roundDied: Int,
    @ColumnInfo(name = "placement") var placement: Int
)

@Dao
interface GameDao {
    @Query("SELECT * FROM Game ORDER BY id desc")
    fun getAll(): List<Game>

    @Query("SELECT AVG(placement) from Game")
    fun getAvgPlacement(): Double

    @Update
    fun updateGames(vararg games: Game)

    @Insert
    fun insert(game: Game): Long

    @Delete
    fun deleteGames(vararg games: Game)

    @Query("DELETE FROM Game")
    fun deleteAll()
}