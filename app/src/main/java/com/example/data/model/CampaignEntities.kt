package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val synopsis: String,
    val system: String = "D&D 5e",
    val inviteCode: String = "GRIM-1001",
    val bannerUrl: String = "",
    val dmName: String = "Mestre do Grimório",
    val isLive: Boolean = false,
    val currentSessionTitle: String = "Sessão 1",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "campaign_sessions")
data class CampaignSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val campaignId: Long,
    val sessionNumber: Int,
    val title: String,
    val dateText: String,
    val summary: String,
    val dmNotes: String = "",
    val publicNotes: String = ""
)

@Entity(tableName = "campaign_npcs")
data class CampaignNpcEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val roleOrTitle: String,
    val challengeRating: String = "1/2",
    val hp: Int = 30,
    val maxHp: Int = 30,
    val ac: Int = 13,
    val speed: String = "9m",
    val str: Int = 12,
    val dex: Int = 14,
    val con: Int = 12,
    val intScore: Int = 10,
    val wis: Int = 11,
    val cha: Int = 10,
    val actionsJson: String = "[]",
    val spellsJson: String = "[]",
    val description: String = "",
    val avatarUrl: String = "",
    val isHostile: Boolean = true
)

@Entity(tableName = "campaign_members")
data class CampaignMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val campaignId: Long,
    val playerName: String,
    val characterName: String,
    val characterClass: String,
    val level: Int = 1,
    val currentHp: Int = 20,
    val maxHp: Int = 20,
    val avatarUrl: String = "",
    val role: String = "PLAYER" // "DM" or "PLAYER"
)
