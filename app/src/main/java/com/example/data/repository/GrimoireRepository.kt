package com.example.data.repository

import com.example.data.dao.GrimoireDao
import com.example.data.model.CampaignEntity
import com.example.data.model.CampaignSessionEntity
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CampaignMemberEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.ItemEntity
import com.example.data.model.RollHistoryEntity
import com.example.data.model.SpellEntity
import com.example.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

class GrimoireRepository(private val dao: GrimoireDao) {

    fun getCharacter(id: Long): Flow<CharacterEntity?> = dao.getCharacter(id)
    val allCharacters: Flow<List<CharacterEntity>> = dao.getAllCharacters()
    val spells: Flow<List<SpellEntity>> = dao.getAllSpells()
    val items: Flow<List<ItemEntity>> = dao.getAllItems()
    val recentRolls: Flow<List<RollHistoryEntity>> = dao.getRecentRolls()
    val notes: Flow<List<NoteEntity>> = dao.getAllNotes()

    // Campaigns
    val allCampaigns: Flow<List<CampaignEntity>> = dao.getAllCampaigns()
    fun getCampaignById(id: Long): Flow<CampaignEntity?> = dao.getCampaignById(id)
    fun getSessionsForCampaign(campaignId: Long): Flow<List<CampaignSessionEntity>> = dao.getSessionsForCampaign(campaignId)
    fun getNpcsForCampaign(campaignId: Long): Flow<List<CampaignNpcEntity>> = dao.getNpcsForCampaign(campaignId)
    fun getMembersForCampaign(campaignId: Long): Flow<List<CampaignMemberEntity>> = dao.getMembersForCampaign(campaignId)

    suspend fun saveCampaign(campaign: CampaignEntity): Long = dao.insertCampaign(campaign)
    suspend fun updateCampaign(campaign: CampaignEntity) = dao.updateCampaign(campaign)
    suspend fun deleteCampaign(id: Long) = dao.deleteCampaign(id)

    suspend fun saveSession(session: CampaignSessionEntity): Long = dao.insertSession(session)

    suspend fun saveNpc(npc: CampaignNpcEntity): Long = dao.insertNpc(npc)
    suspend fun updateNpc(npc: CampaignNpcEntity) = dao.updateNpc(npc)
    suspend fun deleteNpc(id: Long) = dao.deleteNpc(id)

    suspend fun saveMember(member: CampaignMemberEntity): Long = dao.insertMember(member)
    suspend fun updateMember(member: CampaignMemberEntity) = dao.updateMember(member)
    suspend fun deleteMember(id: Long) = dao.deleteMember(id)

    suspend fun saveCharacter(character: CharacterEntity): Long {
        return dao.insertCharacter(character)
    }

    suspend fun updateCharacter(character: CharacterEntity) {
        dao.updateCharacter(character)
    }

    suspend fun deleteCharacter(id: Long) {
        dao.deleteCharacter(id)
    }

    suspend fun addSpell(spell: SpellEntity) {
        dao.insertSpell(spell)
    }

    suspend fun updateSpell(spell: SpellEntity) {
        dao.updateSpell(spell)
    }

    suspend fun deleteSpell(id: Long) {
        dao.deleteSpell(id)
    }

    suspend fun addItem(item: ItemEntity) {
        dao.insertItem(item)
    }

    suspend fun updateItem(item: ItemEntity) {
        dao.updateItem(item)
    }

    suspend fun deleteItem(id: Long) {
        dao.deleteItem(id)
    }

    suspend fun recordRoll(roll: RollHistoryEntity) {
        dao.insertRoll(roll)
    }

    suspend fun addNote(note: NoteEntity) {
        dao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        dao.updateNote(note)
    }

    suspend fun deleteNote(id: Long) {
        dao.deleteNote(id)
    }
}
