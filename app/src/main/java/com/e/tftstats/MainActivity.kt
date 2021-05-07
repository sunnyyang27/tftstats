package com.e.tftstats

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.e.tftstats.database.AppDatabase
import com.e.tftstats.model.Game
import com.e.tftstats.model.Helper
import com.e.tftstats.ui.game.GameModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object {
        var currentGame: GameModel = GameModel()
        var db: AppDatabase? = null
        var screenWidth: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Migrations
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Stage ADD COLUMN carouselItem INTEGER")
//                database.execSQL("ALTER TABLE TEAM ADD COLUMN chosenTrait INTEGER NOT NULL DEFAULT 0")
//            }
//        }
//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Stage ADD COLUMN armoryItem INTEGER")
//                // Delete chosenTrait
//                database.execSQL("CREATE TABLE team_backup (id INTEGER NOT NULL,gameId INTEGER NOT NULL references Game(id) ON DELETE CASCADE,champId INTEGER NOT NULL,items TEXT,starLevel INTEGER NOT NULL,isCarry INTEGER NOT NULL, PRIMARY KEY(id));")
//                database.execSQL("INSERT INTO team_backup SELECT id,gameId,champId,items,starLevel,isCarry FROM Team;")
//                database.execSQL("DROP TABLE Team;")
//                database.execSQL("ALTER TABLE team_backup RENAME TO Team;")
//            }
//        }
//        val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Stage ADD COLUMN pveItems TEXT NOT NULL DEFAULT \'\'")
//            }
//        }

        db = Room.databaseBuilder(this, AppDatabase::class.java, "tft-set-4")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
//            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

        if (!Helper.isInitialized()) {
            Helper.loadAssets()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gold, R.id.nav_health, R.id.nav_placement, R.id.nav_level, R.id.nav_item_stats, R.id.nav_final_comp_stats), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        //val height = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds baseItemToComponentItems to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun deleteGames(view: View) {
//        db!!.gameDao().deleteAll()
    }

    fun deleteStages(view: View) {
//        db!!.stageDao().deleteAll()
    }

    fun deleteTeams(view: View) {
//        db!!.teamDao().deleteAll()
    }

    /** FINISH GAME */
    fun onFinishGameClicked(view: View) {
        // Create and insert game
        val numStages = currentGame.stages.size
        val game = Game(
            stageDied = numStages,
            roundDied = currentGame.roundDied,
            placement = currentGame.stages[numStages-1].placement)
        val gameDao = db!!.gameDao()
        val gameId = gameDao.insert(game).toInt()

        // Update stages with gameId
        val stageDao = db!!.stageDao()
        for (stage in currentGame.stages) {
            stage.gameId = gameId
            stageDao.insert(stage)
        }

        // Create and insert final comp
        val teamDao = db!!.teamDao()
        for (team in currentGame.teamComp.values) {
            team.gameId = gameId
            teamDao.insert(team)
        }

        // Reset currentGame
        currentGame = GameModel()

        // Go back to main page
        val navController = findNavController(R.id.nav_host_fragment)
        val homeId = R.id.nav_home
        while (navController.popBackStack(homeId, false)) {}
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
