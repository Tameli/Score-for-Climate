package com.example.scoreforclimate.roomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(foreignKeys = arrayOf(ForeignKey(entity = Score::class, parentColumns = arrayOf("id"), childColumns = arrayOf("idParent"), onDelete = ForeignKey.CASCADE)))
data class History(
    @PrimaryKey(autoGenerate = true)
    var historyId: Long,
    var idParent: Long,
    var modificationDate: Date?,
    var listActions: MutableList<String?>
)