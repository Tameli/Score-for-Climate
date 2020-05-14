package com.example.scoreforclimate.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(Score::class, History::class), version = 1)
@TypeConverters(Converters::class)
abstract class ScoreDatabase: RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
    abstract fun historyDao():HistoryDao

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