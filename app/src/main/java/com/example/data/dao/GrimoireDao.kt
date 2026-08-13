package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CampaignEntity
import com.example.data.model.CampaignSessionEntity
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CampaignMemberEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.ItemEntity
import com.example.data.model.RollHistoryEntity
import com.example.data.model.SpellEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrimoireDao {
    // Character
    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    fun getCharacter(id: Long): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacter(id: Long)

    // Campaigns
    @Query("SELECT * FROM campaigns ORDER BY id DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id LIMIT 1")
    fun getCampaignById(id: Long): Flow<CampaignEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity): Long

    @Update
    suspend fun updateCampaign(campaign: CampaignEntity)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaign(id: Long)

    // Campaign Sessions
    @Query("SELECT * FROM campaign_sessions WHERE campaignId = :campaignId ORDER BY sessionNumber DESC")
    fun getSessionsForCampaign(campaignId: Long): Flow<List<CampaignSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CampaignSessionEntity): Long

    // Campaign NPCs
    @Query("SELECT * FROM campaign_npcs WHERE campaignId = :campaignId ORDER BY name ASC")
    fun getNpcsForCampaign(campaignId: Long): Flow<List<CampaignNpcEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNpc(npc: CampaignNpcEntity): Long

    @Update
    suspend fun updateNpc(npc: CampaignNpcEntity)

    @Query("DELETE FROM campaign_npcs WHERE id = :id")
    suspend fun deleteNpc(id: Long)

    // Campaign Members
    @Query("SELECT * FROM campaign_members WHERE campaignId = :campaignId ORDER BY id ASC")
    fun getMembersForCampaign(campaignId: Long): Flow<List<CampaignMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: CampaignMemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<CampaignMemberEntity>)

    @Update
    suspend fun updateMember(member: CampaignMemberEntity)

    @Query("DELETE FROM campaign_members WHERE id = :id")
    suspend fun deleteMember(id: Long)

    // Spells
    @Query("SELECT * FROM spells ORDER BY level ASC, name ASC")
    fun getAllSpells(): Flow<List<SpellEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpells(spells: List<SpellEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpell(spell: SpellEntity)

    @Update
    suspend fun updateSpell(spell: SpellEntity)

    @Query("DELETE FROM spells WHERE id = :id")
    suspend fun deleteSpell(id: Long)

    // Items
    @Query("SELECT * FROM items ORDER BY category ASC, name ASC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    // Roll History
    @Query("SELECT * FROM roll_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentRolls(): Flow<List<RollHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoll(roll: RollHistoryEntity)

    // Notes
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<com.example.data.model.NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: com.example.data.model.NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<com.example.data.model.NoteEntity>)

    @Update
    suspend fun updateNote(note: com.example.data.model.NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}
