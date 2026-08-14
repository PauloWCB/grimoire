package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.GrimoireDatabase
import com.example.data.model.CharacterEntity
import com.example.data.model.ItemEntity
import com.example.data.model.NoteEntity
import com.example.data.model.RollHistoryEntity
import com.example.data.model.SpellEntity
import com.example.data.repository.GrimoireRepository
import com.example.domain.rules.ActiveCondition
import com.example.domain.rules.DndFeatsDatabase
import com.example.domain.rules.DndRulesEngine
import com.example.domain.rules.PresetFeat
import com.example.domain.rules.PresetSpell
import com.example.ui.importexport.ImportExportUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.random.Random

import com.example.data.model.CampaignEntity
import com.example.data.model.CampaignSessionEntity
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CampaignMemberEntity
import com.example.domain.rules.NpcGeneratorEngine

enum class ImportStep {
    IDLE, READING, COMPLETED, REVIEWING, FINISHED
}

data class Combatant(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1, 1000),
    val name: String,
    val initiative: Int,
    val currentHp: Int,
    val maxHp: Int,
    val isPlayer: Boolean = false,
    val ac: Int = 14,
    val conditions: List<String> = emptyList(),
    val actionsText: String = "",
    val spellsText: String = "",
    val avatarUrl: String = ""
)

sealed class OverlayScreen {
    object None : OverlayScreen()
    object NpcGenerator : OverlayScreen()
    data class CharacterDetailPc(val character: CharacterEntity) : OverlayScreen()
    data class CharacterDetailNpc(val npc: CampaignNpcEntity) : OverlayScreen()
    object CreateCampaignItem : OverlayScreen()
    object GiveItemPlayer : OverlayScreen()
    object SendHandout : OverlayScreen()
}

class GrimoireViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GrimoireRepository

    val allCharacters: StateFlow<List<CharacterEntity>>
    private val _selectedCharacterId = MutableStateFlow(1L)
    val selectedCharacterId: StateFlow<Long> = _selectedCharacterId.asStateFlow()

    val character: StateFlow<CharacterEntity?>
    val spells: StateFlow<List<SpellEntity>>
    val items: StateFlow<List<ItemEntity>>
    val recentRolls: StateFlow<List<RollHistoryEntity>>
    val notes: StateFlow<List<NoteEntity>>

    // Overlay Screen Navigation State
    private val _activeOverlayScreen = MutableStateFlow<OverlayScreen>(OverlayScreen.None)
    val activeOverlayScreen: StateFlow<OverlayScreen> = _activeOverlayScreen.asStateFlow()

    fun openNpcGenerator() { _activeOverlayScreen.value = OverlayScreen.NpcGenerator }
    fun openCharacterDetailPc(pc: CharacterEntity) { _activeOverlayScreen.value = OverlayScreen.CharacterDetailPc(pc) }
    fun openCharacterDetailNpc(npc: CampaignNpcEntity) { _activeOverlayScreen.value = OverlayScreen.CharacterDetailNpc(npc) }
    fun openCreateCampaignItem() { _activeOverlayScreen.value = OverlayScreen.CreateCampaignItem }
    fun openGiveItemPlayer() { _activeOverlayScreen.value = OverlayScreen.GiveItemPlayer }
    fun openSendHandout() { _activeOverlayScreen.value = OverlayScreen.SendHandout }
    fun closeOverlayScreen() { _activeOverlayScreen.value = OverlayScreen.None }

    // Role Switcher State ("DM" vs "PLAYER")
    private val _currentRole = MutableStateFlow("DM") // "DM" or "PLAYER"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _isPortalActive = MutableStateFlow(false)
    val isPortalActive: StateFlow<Boolean> = _isPortalActive.asStateFlow()

    fun openPortal() { _isPortalActive.value = true }
    fun closePortal() { _isPortalActive.value = false }

    private val _showRoleSwitcherSheet = MutableStateFlow(false)
    val showRoleSwitcherSheet: StateFlow<Boolean> = _showRoleSwitcherSheet.asStateFlow()

    fun setCurrentRole(role: String) {
        _currentRole.value = role
    }

    // Combat Setup & Active Tracker State
    private val _isCombatActive = MutableStateFlow(false)
    val isCombatActive: StateFlow<Boolean> = _isCombatActive.asStateFlow()

    private val _isRollingDiceAnimation = MutableStateFlow(false)
    val isRollingDiceAnimation: StateFlow<Boolean> = _isRollingDiceAnimation.asStateFlow()

    fun startCombatWithAnimatedRolls(
        selectedNpcs: List<CampaignNpcEntity>,
        pcInitiativeMap: Map<String, Int>
    ) {
        viewModelScope.launch {
            _isRollingDiceAnimation.value = true
            delay(2200) // Animated dice roll duration

            val list = mutableListOf<Combatant>()

            // Add selected PCs
            val currentChars = allCharacters.value
            currentChars.forEach { pc ->
                val init = pcInitiativeMap[pc.name] ?: (Random.nextInt(1, 21) + ((pc.dex - 10) / 2))
                list.add(
                    Combatant(
                        id = pc.id,
                        name = pc.name,
                        initiative = init,
                        currentHp = pc.currentHp,
                        maxHp = pc.maxHp,
                        isPlayer = true,
                        ac = pc.ac,
                        avatarUrl = pc.avatarUrl
                    )
                )
            }

            // Add selected NPCs with d20 rolls
            selectedNpcs.forEach { npc ->
                val dexMod = (npc.dex - 10) / 2
                val roll = Random.nextInt(1, 21) + dexMod
                list.add(
                    Combatant(
                        name = npc.name,
                        initiative = roll,
                        currentHp = npc.hp,
                        maxHp = npc.maxHp,
                        isPlayer = false,
                        ac = npc.ac,
                        actionsText = npc.actionsJson,
                        spellsText = npc.spellsJson,
                        avatarUrl = npc.avatarUrl
                    )
                )
            }

            _combatants.value = list.sortedByDescending { it.initiative }
            _currentTurnIndex.value = 0
            _currentRound.value = 1
            _isCombatActive.value = true
            _isRollingDiceAnimation.value = false
        }
    }

    fun overrideInitiative(combatantId: Long, newInit: Int) {
        val updated = _combatants.value.map {
            if (it.id == combatantId) it.copy(initiative = newInit) else it
        }
        _combatants.value = updated.sortedByDescending { it.initiative }
    }

    fun updateCombatantHp(combatantId: Long, newHp: Int) {
        _combatants.value = _combatants.value.map { c ->
            if (c.id == combatantId) {
                c.copy(currentHp = newHp.coerceIn(0, c.maxHp * 2))
            } else c
        }
    }

    fun toggleCombatantCondition(combatantId: Long, condition: String) {
        _combatants.value = _combatants.value.map { c ->
            if (c.id == combatantId) {
                val list = c.conditions.toMutableList()
                if (list.contains(condition)) list.remove(condition) else list.add(condition)
                c.copy(conditions = list)
            } else c
        }
    }

    fun updateCombatantAc(combatantId: Long, newAc: Int) {
        _combatants.value = _combatants.value.map { c ->
            if (c.id == combatantId) c.copy(ac = newAc) else c
        }
    }

    fun endCombatTracker() {
        _isCombatActive.value = false
    }

    fun openRoleSwitcherSheet() {
        _showRoleSwitcherSheet.value = true
    }

    fun closeRoleSwitcherSheet() {
        _showRoleSwitcherSheet.value = false
    }

    // Campaigns State
    val allCampaigns: StateFlow<List<CampaignEntity>>
    private val _activeCampaignId = MutableStateFlow(1L)
    val activeCampaignId: StateFlow<Long> = _activeCampaignId.asStateFlow()

    val activeCampaign: StateFlow<CampaignEntity?>
    val campaignSessions: StateFlow<List<CampaignSessionEntity>>
    val campaignNpcs: StateFlow<List<CampaignNpcEntity>>
    val campaignMembers: StateFlow<List<CampaignMemberEntity>>

    // Search filters
    private val _spellSearchQuery = MutableStateFlow("")
    val spellSearchQuery: StateFlow<String> = _spellSearchQuery.asStateFlow()

    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery: StateFlow<String> = _noteSearchQuery.asStateFlow()

    // App Language & Settings
    private val _appLanguage = MutableStateFlow("pt") // "pt" or "en"
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _showSettingsModal = MutableStateFlow(false)
    val showSettingsModal: StateFlow<Boolean> = _showSettingsModal.asStateFlow()

    fun setLanguage(lang: String) {
        _appLanguage.value = lang
    }

    fun openSettingsModal() {
        _showSettingsModal.value = true
    }

    fun closeSettingsModal() {
        _showSettingsModal.value = false
    }

    // Combat Initiative Tracker
    private val _combatants = MutableStateFlow<List<Combatant>>(
        listOf(
            Combatant(1L, "Thalric Ironfoot", 14, 45, 58, isPlayer = true),
            Combatant(2L, "Lorde Orc Korgath", 18, 62, 62, isPlayer = false),
            Combatant(3L, "Cultista das Sombras", 9, 22, 22, isPlayer = false)
        )
    )
    val combatants: StateFlow<List<Combatant>> = _combatants.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    val currentTurnIndex: StateFlow<Int> = _currentTurnIndex.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    init {
        val database = GrimoireDatabase.getDatabase(application, viewModelScope)
        repository = GrimoireRepository(database.grimoireDao())

        allCharacters = repository.allCharacters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        character = combine(allCharacters, selectedCharacterId) { chars, id ->
            chars.find { it.id == id } ?: chars.firstOrNull()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        spells = repository.spells.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        items = repository.items.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        recentRolls = repository.recentRolls.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        notes = repository.notes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allCampaigns = repository.allCampaigns.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeCampaign = combine(allCampaigns, activeCampaignId) { list, id ->
            list.find { it.id == id } ?: list.firstOrNull()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        campaignSessions = activeCampaignId.combine(allCampaigns) { id, _ -> id }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1L
        ).let { idFlow ->
            repository.getSessionsForCampaign(1L).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
        }

        campaignNpcs = repository.getNpcsForCampaign(1L).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        campaignMembers = repository.getMembersForCampaign(1L).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Campaign Actions
    fun selectActiveCampaign(id: Long) {
        _activeCampaignId.value = id
    }

    fun createCampaign(title: String, synopsis: String, dmName: String) {
        viewModelScope.launch {
            val newC = CampaignEntity(
                title = title,
                synopsis = synopsis,
                dmName = dmName,
                inviteCode = "GRIM-" + Random.nextInt(1000, 9999)
            )
            val newId = repository.saveCampaign(newC)
            _activeCampaignId.value = newId
        }
    }

    fun addSessionToCampaign(title: String, summary: String, dmNotes: String, publicNotes: String) {
        val campId = activeCampaignId.value
        val currentCount = campaignSessions.value.size + 1
        viewModelScope.launch {
            repository.saveSession(
                CampaignSessionEntity(
                    campaignId = campId,
                    sessionNumber = currentCount,
                    title = title,
                    dateText = "Hoje",
                    summary = summary,
                    dmNotes = dmNotes,
                    publicNotes = publicNotes
                )
            )
        }
    }

    fun generateRandomNpc(presetType: String) {
        val campId = activeCampaignId.value
        val newNpc = NpcGeneratorEngine.generateNpc(campId, presetType)
        viewModelScope.launch {
            repository.saveNpc(newNpc)
        }
    }

    fun saveCustomNpc(npc: CampaignNpcEntity) {
        val campId = activeCampaignId.value
        viewModelScope.launch {
            repository.saveNpc(npc.copy(campaignId = campId))
        }
    }

    fun addNpcToCombat(npc: CampaignNpcEntity) {
        val current = _combatants.value.toMutableList()
        val initRoll = Random.nextInt(1, 21) + ((npc.dex - 10) / 2)
        current.add(
            Combatant(
                name = npc.name,
                initiative = initRoll,
                currentHp = npc.hp,
                maxHp = npc.maxHp,
                ac = npc.ac,
                isPlayer = false,
                actionsText = npc.actionsJson,
                spellsText = npc.spellsJson,
                avatarUrl = npc.avatarUrl
            )
        )
        // Sort by initiative descending
        _combatants.value = current.sortedByDescending { it.initiative }
    }

    fun addCombatant(name: String, initiative: Int, hp: Int, isPlayer: Boolean = false, ac: Int = 14) {
        val current = _combatants.value.toMutableList()
        current.add(
            Combatant(
                name = name,
                initiative = initiative,
                currentHp = hp,
                maxHp = hp,
                ac = ac,
                isPlayer = isPlayer
            )
        )
        _combatants.value = current.sortedByDescending { it.initiative }
    }

    // DM Admin Actions: Direct player inventory item injection & handout note injection
    fun dmGiveItemToPlayer(characterName: String, itemName: String, qty: Int, gpValue: Int, properties: String) {
        viewModelScope.launch {
            repository.addItem(
                ItemEntity(
                    name = itemName,
                    category = "Mochila",
                    weightKg = 0.5f,
                    quantity = qty,
                    valueGp = gpValue,
                    properties = properties,
                    description = "Concedido pelo Mestre da Mesa ($characterName)"
                )
            )
            repository.addNote(
                NoteEntity(
                    title = "Recompensa do Mestre: $itemName",
                    content = "O Mestre adicionou $qty x $itemName ao seu inventário!",
                    date = "Recebido do Mestre",
                    tagsJson = "[\"Mestre\", \"Recompensa\"]",
                    isHandout = true
                )
            )
        }
    }

    fun dmSendHandoutNote(title: String, content: String, imageUrl: String = "") {
        viewModelScope.launch {
            repository.addNote(
                NoteEntity(
                    title = title,
                    content = content,
                    date = "Nota do Mestre",
                    tagsJson = "[\"Mestre\", \"Handout\"]",
                    isHandout = true,
                    imageUrl = imageUrl
                )
            )
        }
    }

    // Multiple Characters Management
    fun switchCharacter(id: Long) {
        _selectedCharacterId.value = id
    }

    fun createNewCharacter(name: String, characterClass: String, race: String) {
        viewModelScope.launch {
            val autoInic = DndRulesEngine.calculateInitiative(10)
            val newChar = CharacterEntity(
                id = 0L,
                name = name.ifBlank { "Novo Aventureiro" },
                characterClass = characterClass.ifBlank { "Guerreiro" },
                race = race.ifBlank { "Humano" },
                level = 1,
                currentHp = 10,
                maxHp = 10,
                ac = 10,
                initiative = autoInic,
                str = 10, dex = 10, con = 10, intScore = 10, wis = 10, cha = 10
            )
            val insertedId = repository.saveCharacter(newChar)
            _selectedCharacterId.value = insertedId
        }
    }

    fun deleteCharacter(id: Long) {
        viewModelScope.launch {
            repository.deleteCharacter(id)
            val remaining = allCharacters.value.filter { it.id != id }
            if (remaining.isNotEmpty()) {
                _selectedCharacterId.value = remaining.first().id
            }
        }
    }

    fun updateCurrentCharacter(updated: CharacterEntity) {
        val autoInic = DndRulesEngine.calculateInitiative(updated.dex)
        val autoChar = updated.copy(initiative = autoInic)
        viewModelScope.launch {
            repository.updateCharacter(autoChar)
        }
    }

    // JSON Import & Export
    fun exportCharacterJson(): String {
        val char = character.value ?: return "{}"
        val spList = spells.value
        val ntList = notes.value
        return ImportExportUtils.toJson(char, spList, ntList)
    }

    fun importCharacterFromJson(jsonStr: String): Boolean {
        val data = ImportExportUtils.fromJson(jsonStr) ?: return false
        viewModelScope.launch {
            val newChar = data.character.copy(id = 0L)
            val newId = repository.saveCharacter(newChar)
            _selectedCharacterId.value = newId

            for (s in data.spells) {
                repository.addSpell(s.copy(id = 0L))
            }
            for (n in data.notes) {
                repository.addNote(n.copy(id = 0L))
            }
        }
        return true
    }

    // Rests Modal State
    private val _showShortRestModal = MutableStateFlow(false)
    val showShortRestModal: StateFlow<Boolean> = _showShortRestModal.asStateFlow()

    private val _showLongRestModal = MutableStateFlow(false)
    val showLongRestModal: StateFlow<Boolean> = _showLongRestModal.asStateFlow()

    private val _showLongRestCompletedModal = MutableStateFlow(false)
    val showLongRestCompletedModal: StateFlow<Boolean> = _showLongRestCompletedModal.asStateFlow()

    fun openShortRestModal() { _showShortRestModal.value = true }
    fun closeShortRestModal() { _showShortRestModal.value = false }

    fun openLongRestModal() { _showLongRestModal.value = true }
    fun closeLongRestModal() { _showLongRestModal.value = false }

    fun closeLongRestCompletedModal() { _showLongRestCompletedModal.value = false }

    // Ascension / Level Up State
    private val _showAscensionFlow = MutableStateFlow(false)
    val showAscensionFlow: StateFlow<Boolean> = _showAscensionFlow.asStateFlow()

    private val _ascensionStep = MutableStateFlow(1)
    val ascensionStep: StateFlow<Int> = _ascensionStep.asStateFlow()

    private val _ascensionClass = MutableStateFlow("Guerreiro")
    val ascensionClass: StateFlow<String> = _ascensionClass.asStateFlow()

    private val _ascensionHpMode = MutableStateFlow("Average") // "Roll" vs "Average"
    val ascensionHpMode: StateFlow<String> = _ascensionHpMode.asStateFlow()

    private val _ascensionChoiceType = MutableStateFlow("ASI") // "ASI" vs "Feat"
    val ascensionChoiceType: StateFlow<String> = _ascensionChoiceType.asStateFlow()

    private val _ascensionSelectedFeat = MutableStateFlow<PresetFeat?>(null)
    val ascensionSelectedFeat: StateFlow<PresetFeat?> = _ascensionSelectedFeat.asStateFlow()

    private val _ascensionSelectedAttribute = MutableStateFlow("FOR")
    val ascensionSelectedAttribute: StateFlow<String> = _ascensionSelectedAttribute.asStateFlow()

    private val _ascensionSelectedSpells = MutableStateFlow<List<PresetSpell>>(emptyList())
    val ascensionSelectedSpells: StateFlow<List<PresetSpell>> = _ascensionSelectedSpells.asStateFlow()

    fun startAscensionFlow() {
        val currentChar = character.value
        _ascensionClass.value = currentChar?.characterClass ?: "Guerreiro"
        _ascensionChoiceType.value = "ASI"
        _ascensionSelectedFeat.value = DndFeatsDatabase.ALL_PRESET_FEATS.firstOrNull()
        _ascensionSelectedAttribute.value = "FOR"
        _ascensionSelectedSpells.value = emptyList()
        _ascensionStep.value = 1
        _showAscensionFlow.value = true
    }

    fun closeAscensionFlow() {
        _showAscensionFlow.value = false
    }

    fun setAscensionStep(step: Int) {
        _ascensionStep.value = step.coerceIn(1, 5)
    }

    fun setAscensionClass(cls: String) {
        _ascensionClass.value = cls
    }

    fun setAscensionHpMode(mode: String) {
        _ascensionHpMode.value = mode
    }

    fun setAscensionChoiceType(type: String) {
        _ascensionChoiceType.value = type
    }

    fun setAscensionSelectedFeat(feat: PresetFeat?) {
        _ascensionSelectedFeat.value = feat
    }

    fun setAscensionSelectedAttribute(attr: String) {
        _ascensionSelectedAttribute.value = attr
    }

    fun toggleAscensionSelectedSpell(spell: PresetSpell) {
        val current = _ascensionSelectedSpells.value.toMutableList()
        if (current.any { it.name == spell.name }) {
            current.removeAll { it.name == spell.name }
        } else {
            current.add(spell)
        }
        _ascensionSelectedSpells.value = current
    }

    fun finishAscension() {
        val currentChar = character.value ?: return
        val cls = _ascensionClass.value.ifBlank { currentChar.characterClass }

        val hitDie = when {
            cls.contains("Bárbaro", ignoreCase = true) -> 12
            cls.contains("Guerreiro", ignoreCase = true) || cls.contains("Paladino", ignoreCase = true) || cls.contains("Patrulheiro", ignoreCase = true) -> 10
            cls.contains("Mago", ignoreCase = true) || cls.contains("Feiticeiro", ignoreCase = true) -> 6
            else -> 8 // Clérigo, Bardo, Druida, Ladino, Monge, Bruxo
        }
        val conMod = (currentChar.con - 10) / 2
        val hpGain = if (_ascensionHpMode.value == "Average") {
            (hitDie / 2 + 1 + conMod).coerceAtLeast(1)
        } else {
            (Random.nextInt(1, hitDie + 1) + conMod).coerceAtLeast(1)
        }

        val isPrimary = cls.equals(currentChar.characterClass, ignoreCase = true)
        val newLevel = if (isPrimary) currentChar.level + 1 else currentChar.level
        val newSubClassLevel = if (!isPrimary) (currentChar.subClassLevel + 1) else currentChar.subClassLevel
        val newSubClass = if (!isPrimary && currentChar.subClass.isBlank()) cls else currentChar.subClass

        val totalLevel = newLevel + (if (newSubClass.isNotBlank() && !isPrimary) newSubClassLevel else 0)
        val newMaxHp = currentChar.maxHp + hpGain
        val newCurrentHp = currentChar.currentHp + hpGain

        var newStr = currentChar.str
        var newDex = currentChar.dex
        var newCon = currentChar.con
        var newInt = currentChar.intScore
        var newWis = currentChar.wis
        var newCha = currentChar.cha

        if (_ascensionChoiceType.value == "ASI") {
            when (_ascensionSelectedAttribute.value) {
                "FOR" -> newStr += 2
                "DES" -> newDex += 2
                "CON" -> newCon += 2
                "INT" -> newInt += 2
                "SAB" -> newWis += 2
                "CAR" -> newCha += 2
            }
        }

        viewModelScope.launch {
            repository.updateCharacter(
                currentChar.copy(
                    level = newLevel,
                    subClass = newSubClass,
                    subClassLevel = newSubClassLevel,
                    maxHp = newMaxHp,
                    currentHp = newCurrentHp,
                    str = newStr,
                    dex = newDex,
                    con = newCon,
                    intScore = newInt,
                    wis = newWis,
                    cha = newCha,
                    slot1Max = (currentChar.slot1Max + if (totalLevel in listOf(2, 3)) 1 else 0).coerceAtMost(4),
                    slot1Current = (currentChar.slot1Max + if (totalLevel in listOf(2, 3)) 1 else 0).coerceAtMost(4),
                    slot2Max = (currentChar.slot2Max + if (totalLevel in listOf(3, 4)) 1 else 0).coerceAtMost(3),
                    slot2Current = (currentChar.slot2Max + if (totalLevel in listOf(3, 4)) 1 else 0).coerceAtMost(3)
                )
            )

            // Add Feat if selected
            if (_ascensionChoiceType.value == "Feat") {
                _ascensionSelectedFeat.value?.let { feat ->
                    repository.addSpell(
                        SpellEntity(
                            name = "Talento: ${feat.name}",
                            level = -2,
                            school = "Talento / Feat",
                            castingTime = "Passiva",
                            range = "Pessoal",
                            damageOrEffect = feat.summary,
                            components = "Pré-requisito: ${feat.prerequisite}",
                            description = feat.description
                        )
                    )
                }
            }

            // Add selected Spells
            _ascensionSelectedSpells.value.forEach { sp ->
                repository.addSpell(
                    SpellEntity(
                        name = sp.name,
                        level = sp.level,
                        school = sp.school,
                        castingTime = sp.castingTime,
                        range = sp.range,
                        damageOrEffect = sp.damageOrEffect,
                        components = sp.components,
                        description = sp.description,
                        isPrepared = true
                    )
                )
            }

            _showAscensionFlow.value = false
        }
    }

    // Spells Custom Actions
    fun addCustomSpell(spell: SpellEntity) {
        viewModelScope.launch {
            repository.addSpell(spell)
        }
    }

    fun updateSpell(spell: SpellEntity) {
        viewModelScope.launch {
            repository.updateSpell(spell)
        }
    }

    fun deleteSpell(id: Long) {
        viewModelScope.launch {
            repository.deleteSpell(id)
            if (_selectedSpell.value?.id == id) {
                _selectedSpell.value = null
            }
        }
    }

    fun setSpellSearchQuery(query: String) {
        _spellSearchQuery.value = query
    }

    // Notes Actions
    fun createPersonalNote(title: String, content: String, tags: List<String>, imageUrl: String = "", isHandout: Boolean = false) {
        val tagsJson = if (tags.isEmpty()) "[\"Geral\"]" else "[\"${tags.joinToString("\", \"")}\"]"
        val newNote = NoteEntity(
            title = title,
            content = content,
            date = "Hoje",
            tagsJson = tagsJson,
            isHandout = isHandout,
            imageUrl = imageUrl
        )
        viewModelScope.launch {
            repository.addNote(newNote)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    fun setNoteSearchQuery(query: String) {
        _noteSearchQuery.value = query
    }

    // Combat Initiative Tracker Actions
    fun addCombatant(name: String, initiative: Int, hp: Int, isPlayer: Boolean = false) {
        val newC = Combatant(
            name = name.ifBlank { "Combatente" },
            initiative = initiative,
            currentHp = hp,
            maxHp = hp,
            isPlayer = isPlayer
        )
        val updated = (_combatants.value + newC).sortedByDescending { it.initiative }
        _combatants.value = updated
    }

    fun removeCombatant(id: Long) {
        val updated = _combatants.value.filter { it.id != id }
        _combatants.value = updated
        if (_currentTurnIndex.value >= updated.size && updated.isNotEmpty()) {
            _currentTurnIndex.value = 0
        }
    }

    // Conditions & Expired Message State
    private val _expiredConditionMessage = MutableStateFlow<String?>(null)
    val expiredConditionMessage: StateFlow<String?> = _expiredConditionMessage.asStateFlow()

    fun clearExpiredConditionMessage() {
        _expiredConditionMessage.value = null
    }

    fun addConditionToCharacter(condition: ActiveCondition) {
        val currentChar = character.value ?: return
        val currentList = DndRulesEngine.parseConditionsJson(currentChar.conditionsJson).toMutableList()
        currentList.removeAll { it.name.equals(condition.name, ignoreCase = true) }
        currentList.add(condition)

        val newJson = DndRulesEngine.serializeConditionsJson(currentList)
        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(conditionsJson = newJson))
        }
    }

    fun updateConditionInCharacter(condition: ActiveCondition) {
        addConditionToCharacter(condition)
    }

    fun removeConditionFromCharacter(conditionName: String) {
        val currentChar = character.value ?: return
        val currentList = DndRulesEngine.parseConditionsJson(currentChar.conditionsJson).toMutableList()
        currentList.removeAll { it.name.equals(conditionName, ignoreCase = true) }

        val newJson = DndRulesEngine.serializeConditionsJson(currentList)
        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(conditionsJson = newJson))
        }
    }

    fun toggleCondition(conditionName: String) {
        val currentChar = character.value ?: return
        val currentList = DndRulesEngine.parseConditionsJson(currentChar.conditionsJson)
        val exists = currentList.any { it.name.equals(conditionName, ignoreCase = true) }
        if (exists) {
            removeConditionFromCharacter(conditionName)
        } else {
            addConditionToCharacter(ActiveCondition(name = conditionName, remainingTurns = 1, isPermanent = false))
        }
    }

    fun advanceCharacterTurn() {
        val currentChar = character.value ?: return
        val currentList = DndRulesEngine.parseConditionsJson(currentChar.conditionsJson)
        if (currentList.isEmpty()) return

        val (updatedList, expiredNames) = DndRulesEngine.tickConditions(currentList)
        val newJson = DndRulesEngine.serializeConditionsJson(updatedList)

        if (expiredNames.isNotEmpty()) {
            _expiredConditionMessage.value = "Condição(ões) expirada(s): ${expiredNames.joinToString(", ")}"
        }

        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(conditionsJson = newJson))
        }
    }

    fun nextCombatTurn() {
        if (_combatants.value.isEmpty()) return
        val nextIndex = (_currentTurnIndex.value + 1) % _combatants.value.size
        _currentTurnIndex.value = nextIndex
        if (nextIndex == 0) {
            _currentRound.value = _currentRound.value + 1
        }
        val activeCombatant = _combatants.value.getOrNull(nextIndex)
        if (activeCombatant != null && activeCombatant.isPlayer) {
            advanceCharacterTurn()
        }
    }

    fun resetCombatTracker() {
        val player = character.value
        val playerCombatant = if (player != null) {
            Combatant(1L, player.name, player.initiative, player.currentHp, player.maxHp, isPlayer = true)
        } else {
            Combatant(1L, "Jogador", 14, 45, 58, isPlayer = true)
        }
        _combatants.value = listOf(playerCombatant)
        _currentTurnIndex.value = 0
        _currentRound.value = 1
    }

    // Import Flow State
    private val _importStep = MutableStateFlow(ImportStep.IDLE)
    val importStep: StateFlow<ImportStep> = _importStep.asStateFlow()

    private val _importProgressStep1 = MutableStateFlow(false)
    val importProgressStep1: StateFlow<Boolean> = _importProgressStep1.asStateFlow()

    private val _importProgressStep2 = MutableStateFlow(false)
    val importProgressStep2: StateFlow<Boolean> = _importProgressStep2.asStateFlow()

    private val _importProgressStep3 = MutableStateFlow(false)
    val importProgressStep3: StateFlow<Boolean> = _importProgressStep3.asStateFlow()

    // Navigation & Selections
    private val _selectedSpell = MutableStateFlow<SpellEntity?>(null)
    val selectedSpell: StateFlow<SpellEntity?> = _selectedSpell.asStateFlow()

    private val _selectedItem = MutableStateFlow<ItemEntity?>(null)
    val selectedItem: StateFlow<ItemEntity?> = _selectedItem.asStateFlow()

    private val _spellFilter = MutableStateFlow("Todas")
    val spellFilter: StateFlow<String> = _spellFilter.asStateFlow()

    // Dice Roller State
    private val _showDiceTray = MutableStateFlow(false)
    val showDiceTray: StateFlow<Boolean> = _showDiceTray.asStateFlow()

    private val _selectedDie = MutableStateFlow("D20")
    val selectedDie: StateFlow<String> = _selectedDie.asStateFlow()

    private val _diceQuantity = MutableStateFlow(1)
    val diceQuantity: StateFlow<Int> = _diceQuantity.asStateFlow()

    private val _diceModifier = MutableStateFlow(4)
    val diceModifier: StateFlow<Int> = _diceModifier.asStateFlow()

    private val _diceMode = MutableStateFlow("Normal") // "Normal", "Desv", "Vant"
    val diceMode: StateFlow<String> = _diceMode.asStateFlow()

    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

    private val _lastRollResult = MutableStateFlow<Pair<Int, String>?>(null)
    val lastRollResult: StateFlow<Pair<Int, String>?> = _lastRollResult.asStateFlow()

    // Import Flow Simulation
    fun startPdfImport() {
        viewModelScope.launch {
            _importStep.value = ImportStep.READING
            _importProgressStep1.value = false
            _importProgressStep2.value = false
            _importProgressStep3.value = false

            delay(800)
            _importProgressStep1.value = true // Atributos done

            delay(900)
            _importProgressStep2.value = true // Magias done

            delay(1000)
            _importProgressStep3.value = true // Inventário done

            delay(500)
            _importStep.value = ImportStep.COMPLETED
        }
    }

    fun cancelImport() {
        _importStep.value = ImportStep.IDLE
    }

    fun proceedToReview() {
        _importStep.value = ImportStep.REVIEWING
    }

    fun saveImportedCharacter(updatedChar: CharacterEntity) {
        viewModelScope.launch {
            repository.saveCharacter(updatedChar)
            _importStep.value = ImportStep.FINISHED
        }
    }

    fun resetToImportScreen() {
        _importStep.value = ImportStep.IDLE
    }

    // Character Vitals Actions
    fun adjustHp(delta: Int) {
        val currentChar = character.value ?: return
        val newHp = (currentChar.currentHp + delta).coerceIn(0, currentChar.maxHp)
        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(currentHp = newHp))
        }
    }

    fun adjustTempHp(delta: Int) {
        val currentChar = character.value ?: return
        val newTemp = (currentChar.tempHp + delta).coerceAtLeast(0)
        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(tempHp = newTemp))
        }
    }

    fun useSpellSlot(level: Int) {
        val currentChar = character.value ?: return
        val updated = when (level) {
            1 -> if (currentChar.slot1Current > 0) currentChar.copy(slot1Current = currentChar.slot1Current - 1) else currentChar
            2 -> if (currentChar.slot2Current > 0) currentChar.copy(slot2Current = currentChar.slot2Current - 1) else currentChar
            3 -> if (currentChar.slot3Current > 0) currentChar.copy(slot3Current = currentChar.slot3Current - 1) else currentChar
            4 -> if (currentChar.slot4Current > 0) currentChar.copy(slot4Current = currentChar.slot4Current - 1) else currentChar
            else -> currentChar
        }
        viewModelScope.launch {
            repository.updateCharacter(updated)
        }
    }

    fun restoreSpellSlots() {
        val currentChar = character.value ?: return
        viewModelScope.launch {
            repository.updateCharacter(
                currentChar.copy(
                    slot1Current = currentChar.slot1Max,
                    slot2Current = currentChar.slot2Max,
                    slot3Current = currentChar.slot3Max,
                    slot4Current = currentChar.slot4Max
                )
            )
        }
    }

    fun performShortRest() {
        val currentChar = character.value ?: return
        val healedHp = (currentChar.currentHp + 12).coerceAtMost(currentChar.maxHp)
        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(currentHp = healedHp))
        }
    }

    fun performLongRest() {
        val currentChar = character.value ?: return
        viewModelScope.launch {
            repository.updateCharacter(
                currentChar.copy(
                    currentHp = currentChar.maxHp,
                    tempHp = 0,
                    slot1Current = currentChar.slot1Max,
                    slot2Current = currentChar.slot2Max,
                    slot3Current = currentChar.slot3Max,
                    slot4Current = currentChar.slot4Max
                )
            )
        }
    }

    fun clearConcentration() {
        val currentChar = character.value ?: return
        viewModelScope.launch {
            repository.updateCharacter(currentChar.copy(activeConcentration = ""))
        }
    }

    fun updateWisdomScore(newWis: Int) {
        val currentChar = character.value ?: return
        viewModelScope.launch {
            repository.updateCharacter(
                currentChar.copy(
                    wis = newWis,
                    reviewedFieldsCount = (currentChar.reviewedFieldsCount + 1).coerceAtMost(currentChar.totalFieldsToReview)
                )
            )
        }
    }

    // Currency Actions
    fun adjustCurrency(type: String, delta: Int) {
        val currentChar = character.value ?: return
        val updated = when (type) {
            "CP" -> currentChar.copy(cp = (currentChar.cp + delta).coerceAtLeast(0))
            "SP" -> currentChar.copy(sp = (currentChar.sp + delta).coerceAtLeast(0))
            "EP" -> currentChar.copy(ep = (currentChar.ep + delta).coerceAtLeast(0))
            "GP" -> currentChar.copy(gp = (currentChar.gp + delta).coerceAtLeast(0))
            "PP" -> currentChar.copy(pp = (currentChar.pp + delta).coerceAtLeast(0))
            else -> currentChar
        }
        viewModelScope.launch {
            repository.updateCharacter(updated)
        }
    }

    fun setCurrency(type: String, amount: Int) {
        val currentChar = character.value ?: return
        val updated = when (type) {
            "CP" -> currentChar.copy(cp = amount.coerceAtLeast(0))
            "SP" -> currentChar.copy(sp = amount.coerceAtLeast(0))
            "EP" -> currentChar.copy(ep = amount.coerceAtLeast(0))
            "GP" -> currentChar.copy(gp = amount.coerceAtLeast(0))
            "PP" -> currentChar.copy(pp = amount.coerceAtLeast(0))
            else -> currentChar
        }
        viewModelScope.launch {
            repository.updateCharacter(updated)
        }
    }

    // Inventory Item Actions
    fun adjustItemQuantity(item: ItemEntity, delta: Int) {
        val newQty = item.quantity + delta
        viewModelScope.launch {
            if (newQty <= 0) {
                repository.deleteItem(item.id)
            } else {
                repository.updateItem(item.copy(quantity = newQty))
            }
        }
    }

    fun toggleItemEquipped(item: ItemEntity) {
        val newCategory = if (item.category == "Equipado") "Mochila" else "Equipado"
        viewModelScope.launch {
            repository.updateItem(item.copy(category = newCategory))
        }
    }

    fun addItem(name: String, category: String, weight: Float, qty: Int, properties: String, description: String = "") {
        viewModelScope.launch {
            repository.addItem(
                ItemEntity(
                    name = name,
                    category = category,
                    weightKg = weight,
                    quantity = qty,
                    properties = properties,
                    description = description
                )
            )
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
            if (_selectedItem.value?.id == itemId) {
                _selectedItem.value = null
            }
        }
    }

    // Spellbook Actions
    fun setSpellFilter(filter: String) {
        _spellFilter.value = filter
    }

    fun selectSpellForDetail(spell: SpellEntity?) {
        _selectedSpell.value = spell
    }

    fun selectItemForDetail(item: ItemEntity?) {
        _selectedItem.value = item
    }

    fun castSpell(spell: SpellEntity, levelUsed: Int) {
        useSpellSlot(levelUsed)
        if (spell.isConcentration) {
            val currentChar = character.value
            if (currentChar != null) {
                viewModelScope.launch {
                    repository.updateCharacter(currentChar.copy(activeConcentration = spell.name))
                }
            }
        }
    }

    // Dice Tray Actions
    fun openDiceTray(die: String = "D20", mod: Int = 4, label: String = "Rolar Dados") {
        _selectedDie.value = die
        _diceModifier.value = mod
        _showDiceTray.value = true
    }

    fun closeDiceTray() {
        _showDiceTray.value = false
    }

    fun setDieType(die: String) {
        _selectedDie.value = die
    }

    fun setDiceQuantity(qty: Int) {
        _diceQuantity.value = qty.coerceIn(1, 10)
    }

    fun setDiceModifier(mod: Int) {
        _diceModifier.value = mod
    }

    fun setDiceMode(mode: String) {
        _diceMode.value = mode
    }

    fun rollDice(label: String = "Rolagem") {
        viewModelScope.launch {
            _isRolling.value = true
            delay(1200) // Simulated roll animation duration

            val sides = when (_selectedDie.value) {
                "D4" -> 4
                "D6" -> 6
                "D8" -> 8
                "D10" -> 10
                "D12" -> 12
                "D100" -> 100
                else -> 20
            }

            var die1 = Random.nextInt(1, sides + 1)
            var die2 = Random.nextInt(1, sides + 1)

            val finalDie = when (_diceMode.value) {
                "Vant" -> maxOf(die1, die2)
                "Desv" -> minOf(die1, die2)
                else -> die1
            }

            val mod = _diceModifier.value
            val qty = _diceQuantity.value
            val diceTotal = (1..qty).sumOf { Random.nextInt(1, sides + 1) }
            val total = diceTotal + mod

            val breakdown = "$diceTotal (${_selectedDie.value.lowercase()}) + $mod (mod)"
            _lastRollResult.value = Pair(total, breakdown)
            _isRolling.value = false

            repository.recordRoll(
                RollHistoryEntity(
                    label = label,
                    totalResult = total,
                    dieRoll = diceTotal,
                    modifier = mod,
                    dieType = _selectedDie.value
                )
            )
        }
    }
}
