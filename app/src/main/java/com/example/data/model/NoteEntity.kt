package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val content: String,
    val date: String,
    val tagsJson: String = "[\"Pista\"]",
    val isHandout: Boolean = false, // false = Minhas Notas, true = Da Mesa (Handout/Map)
    val imageUrl: String = ""
)
