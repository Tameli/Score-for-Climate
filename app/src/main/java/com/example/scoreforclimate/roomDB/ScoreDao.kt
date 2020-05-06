package com.example.scoreforclimate.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    fun insertScore(vararg notes: Score)

    @Query("SELECT * FROM scores WHERE id = :id")
    fun getScoreById(id: Long): Score

    @Query("SELECT * FROM scores")
    fun loadAllNotes(): Array<Score>
}
