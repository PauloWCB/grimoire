package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1L,
    val name: String = "Thalric Ironfoot",
    val characterClass: String = "Guerreiro",
    val subClass: String = "Mago",
    val level: Int = 5,
    val subClassLevel: Int = 2,
    val race: String = "Anão",
    val background: String = "Soldado",
    val currentHp: Int = 45,
    val maxHp: Int = 58,
    val tempHp: Int = 0,
    val ac: Int = 16,
    val initiative: Int = 2,
    val speed: String = "9m",
    val str: Int = 18,
    val dex: Int = 14,
    val con: Int = 16,
    val intScore: Int = 18,
    val wis: Int = 10,
    val cha: Int = 8,
    val conditionsJson: String = "[\"Cego (1 rd)\", \"Concentração\"]",
    val slot1Current: Int = 2,
    val slot1Max: Int = 4,
    val slot2Current: Int = 1,
    val slot2Max: Int = 2,
    val slot3Current: Int = 2,
    val slot3Max: Int = 3,
    val slot4Current: Int = 1,
    val slot4Max: Int = 2,
    val activeConcentration: String = "Invocar Elemental",
    val cp: Int = 120,
    val sp: Int = 45,
    val ep: Int = 0,
    val gp: Int = 250,
    val pp: Int = 10,
    val currentWeightKg: Float = 85.0f,
    val maxWeightKg: Float = 150.0f,
    val avatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuADJtKIsX35i2xZN29Ei8aMAV2liDMQ2ugya-WtLWCEtATX1DRQbjwVIBzKjj4dD7MLWF_181v-BejNLzfdJiBeCaiV2TvIIC_Yp8d2gxOxeEJNCt4K-ftHSw_KdocEXZ6VMBiZojxWP3Mgd8BRRl9OFEZuh_WbvR5gk8JAhu9VrXV2bn0LQFkS4VR2lxx46NZQlC37alfVg-5hNXgi-sSVvi4t003P19Vi0nGMPX81b6N--8w9ow",
    val reviewedFieldsCount: Int = 12,
    val totalFieldsToReview: Int = 15
)
