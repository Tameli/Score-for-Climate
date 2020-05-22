package com.example.scoreforclimate.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(vararg histories: History)

    @Query("SELECT * FROM history")
    fun loadAllHistories(): Array<History>

    @Query("SELECT * FROM history WHERE historyId = :id")
    fun getHistoryById(id: Long): History

    @Query("SELECT * FROM history WHERE historyId=(SELECT MAX(historyId) from history)")
    fun getLatestId(): History

    @Query("SELECT * FROM history WHERE historyId=(SELECT MAX(historyId) from history)")
    fun getLatestHistory(): History
}