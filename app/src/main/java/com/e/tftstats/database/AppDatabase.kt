package com.e.tftstats.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.e.tftstats.model.*

@Database(entities = arrayOf(Game::class, Team::class, Stage::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun stageDao(): StageDao
    abstract fun teamDao(): TeamDao
}