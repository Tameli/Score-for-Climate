package com.example.scoreforclimate.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Score::class), version = 1)
abstract class ScoreDatabase: RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var INSTANCE: ScoreDatabase? = null

        fun getScoreDatabase(context: Context): ScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE
                return if (instance != null) {
                    instance
                } else {
                    Room.databaseBuilder(context.applicationContext, ScoreDatabase::class.java, "scores-database")
                        .allowMainThreadQueries()
                        .build()
                }
            }
        }
    }
}