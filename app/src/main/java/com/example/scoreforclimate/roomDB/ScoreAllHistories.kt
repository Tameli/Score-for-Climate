package com.example.scoreforclimate.roomDB

import androidx.room.Embedded
import androidx.room.Relation

class ScoreAllHistories {
    @Embedded
    var score: Score? = null

    @Relation(parentColumn = "id", entityColumn = "id")
    var histories: List<String>? = null
}