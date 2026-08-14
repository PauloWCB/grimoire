package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roll_history")
data class RollHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val label: String,
    val totalResult: Int,
    val dieRoll: Int,
    val modifier: Int,
    val dieType: String = "d20",
    val timestamp: Long = System.currentTimeMillis()
)
