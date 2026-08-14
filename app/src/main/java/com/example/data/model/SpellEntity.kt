package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spells")
data class SpellEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val level: Int, // 0 = Truque, 1, 2, 3, etc.
    val school: String, // Evocação, Abjuração, Encantamento, etc.
    val castingTime: String, // "1 Ação", "1 Reação"
    val range: String, // "45m", "Pessoal", "18m", "9m", "Toque"
    val damageOrEffect: String, // "8d6 Fogo", "+5 CA", "1d8 Frio", "1d8 + Mod"
    val saveOrAttack: String = "", // "DES CD 15"
    val components: String, // "V, S, M (uma bola minúscula...)"
    val description: String,
    val higherLevels: String = "",
    val isPrepared: Boolean = true,
    val isConcentration: Boolean = false,
    val isRitual: Boolean = false
)
