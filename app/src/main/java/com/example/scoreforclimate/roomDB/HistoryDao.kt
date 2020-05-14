package com.example.scoreforclimate.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(vararg histories: History)


    //fun insertHistory(histories: List<History>) : List<Long>

    @Query("SELECT * FROM history")
    fun loadAllHistories(): Array<History>

    @Query("SELECT * FROM history WHERE historyId = :id")
    fun getHistoryById(id: Long): History
}