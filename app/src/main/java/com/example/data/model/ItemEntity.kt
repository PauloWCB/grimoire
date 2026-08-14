package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val category: String, // "Equipado", "Mochila", "Consumíveis"
    val weightKg: Float,
    val quantity: Int = 1,
    val valueGp: Int = 0,
    val rarity: String = "Comum", // "Comum", "Raro", "Artefato"
    val requiresAttunement: Boolean = false,
    val isAttuned: Boolean = false,
    val attunedTo: String = "",
    val properties: String = "", // "Versatile (1d10)", "Slashing • Versatile", "2d4+2 HP"
    val description: String = "",
    val lore: String = "",
    val iconType: String = "default", // "swords", "shield", "bed", "potion", "fire_sword"
    val imageUrl: String = ""
)
