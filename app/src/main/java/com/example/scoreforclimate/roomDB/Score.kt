package com.example.scoreforclimate.roomDB

import java.sql.Timestamp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
class Score {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(index = true)
    var value: Int? = null
    //var timestamp: Timestamp? = null
}