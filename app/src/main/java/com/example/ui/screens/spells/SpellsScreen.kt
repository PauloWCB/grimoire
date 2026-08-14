package com.example.ui.screens.spells

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterEntity
import com.example.data.model.SpellEntity
import com.example.domain.rules.DndFeatsDatabase
import com.example.domain.rules.DndSpellsDatabase
import com.example.domain.rules.PresetSpell
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGold
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceRaised
import com.example.ui.theme.TertiaryPurple
import com.example.ui.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellsScreen(
    viewModel: GrimoireViewModel,
    onOpenDiceTray: (String, Int, String) -> Unit
) {
    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()
    val spells by viewModel.spells.collectAsState()
    val activeFilter by viewModel.spellFilter.collectAsState()
    val selectedSpell by viewModel.selectedSpell.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddSpellDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filterOptions = listOf("Todas", "Magias", "Habilidades", "Traços/Feats", "Preparadas", "Truques", "Nível 1", "Nível 2", "Nível 3", "Concentração", "Ritual")

    val filteredSpells = spells.filter { spell ->
        val matchesSearch = spell.name.contains(searchQuery, ignoreCase = true) || spell.school.contains(searchQuery, ignoreCase = true) || spell.description.contains(searchQuery, ignoreCase = true)
        val isAbilityOrFeat = spell.level < 0 || spell.school.contains("Habilidade", true) || spell.school.contains("Feat", true) || spell.school.contains("Classe", true) || spell.school.contains("Raça", true) || spell.school.contains("Passiva", true) || spell.school.contains("Traço", true)
        val matchesFilter = when (activeFilter) {
            "Magias" -> !isAbilityOrFeat
            "Habilidades" -> isAbilityOrFeat && (spell.school.contains("Habilidade", true) || spell.school.contains("Classe", true) || spell.level == -1)
            "Traços/Feats" -> isAbilityOrFeat && (spell.school.contains("Raça", true) || spell.school.contains("Feat", true) || spell.school.contains("Passiva", true) || spell.school.contains("Traço", true) || spell.level == -2)
            "Preparadas" -> spell.isPrepared
            "Truques" -> spell.level == 0
            "Nível 1" -> spell.level == 1
            "Nível 2" -> spell.level == 2
            "Nível 3" -> spell.level == 3
            "Concentração" -> spell.isConcentration
            "Ritual" -> spell.isRitual
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // 1. Spell Slots Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceRaised)
                    .drawBehind {
                        drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // N1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("N1:", fontSize = 12.sp, color = OnSurfaceVariant, modifier = Modifier.padding(end = 4.dp))
                    repeat(character.slot1Max) { idx ->
                        val isAvailable = idx < character.slot1Current
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isAvailable) PrimaryGold else Color.Transparent)
                                .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, CircleShape)
                                .padding(end = 2.dp)
                                .clickable { viewModel.useSpellSlot(1) }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                // N2
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("N2:", fontSize = 12.sp, color = OnSurfaceVariant, modifier = Modifier.padding(end = 4.dp))
                    repeat(character.slot2Max) { idx ->
                        val isAvailable = idx < character.slot2Current
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isAvailable) PrimaryGold else Color.Transparent)
                                .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, CircleShape)
                                .clickable { viewModel.useSpellSlot(2) }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                // N3
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("N3:", fontSize = 12.sp, color = OnSurfaceVariant, modifier = Modifier.padding(end = 4.dp))
                    repeat(character.slot3Max) { idx ->
                        val isAvailable = idx < character.slot3Current
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isAvailable) PrimaryGold else Color.Transparent)
                                .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, CircleShape)
                                .clickable { viewModel.useSpellSlot(3) }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                // N4
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("N4:", fontSize = 12.sp, color = OnSurfaceVariant, modifier = Modifier.padding(end = 4.dp))
                    repeat(character.slot4Max) { idx ->
                        val isAvailable = idx < character.slot4Current
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isAvailable) PrimaryGold else Color.Transparent)
                                .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, CircleShape)
                                .clickable { viewModel.useSpellSlot(4) }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
            }

            // 2. Active Concentration Banner
            if (character.activeConcentration.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TertiaryPurple.copy(alpha = 0.2f))
                        .drawBehind {
                            drawLine(TertiaryPurple, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TertiaryPurple, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Concentração Ativa: ${character.activeConcentration}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                    }
                    IconButton(onClick = { viewModel.clearConcentration() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Encerrar", tint = TertiaryPurple, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // 3. Search Bar & Filter Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar magia...", color = OnSurfaceVariant, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = DividerColor,
                        focusedContainerColor = SurfaceRaised,
                        unfocusedContainerColor = SurfaceRaised,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { filter ->
                        val isSelected = filter == activeFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSpellFilter(filter) },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGold,
                                selectedLabelColor = Color(0xFF3D2E00),
                                containerColor = SurfaceRaised,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DividerColor,
                                selectedBorderColor = PrimaryGold
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }

            // 4. Spells List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredSpells) { spell ->
                    SpellCard(
                        spell = spell,
                        onClick = { viewModel.selectSpellForDetail(spell) },
                        onCast = {
                            viewModel.castSpell(spell, spell.level.coerceAtLeast(1))
                            if (spell.damageOrEffect.contains("d", ignoreCase = true)) {
                                onOpenDiceTray("D20", 4, "Ataque - ${spell.name}")
                            }
                        }
                    )
                }
            }
        }

        // FAB Nova Magia
        androidx.compose.material3.FloatingActionButton(
            onClick = { showAddSpellDialog = true },
            containerColor = PrimaryGold,
            contentColor = Color(0xFF3D2E00),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Nova Magia", fontWeight = FontWeight.Bold)
            }
        }

        // Spell Detail Bottom Sheet Modal
        if (selectedSpell != null) {
            SpellDetailBottomSheet(
                spell = selectedSpell!!,
                sheetState = sheetState,
                onDismiss = { viewModel.selectSpellForDetail(null) },
                onDelete = {
                    viewModel.deleteSpell(selectedSpell!!.id)
                    viewModel.selectSpellForDetail(null)
                },
                onCast = { level ->
                    viewModel.castSpell(selectedSpell!!, level)
                    viewModel.selectSpellForDetail(null)
                    if (selectedSpell!!.damageOrEffect.contains("d", ignoreCase = true)) {
                        onOpenDiceTray("D6", 0, "Dano - ${selectedSpell!!.name}")
                    }
                }
            )
        }

        if (showAddSpellDialog) {
            CreateSpellDialog(
                onDismiss = { showAddSpellDialog = false },
                onSave = { newSpell ->
                    viewModel.addCustomSpell(newSpell)
                    showAddSpellDialog = false
                }
            )
        }
    }
}

@Composable
fun SpellCard(
    spell: SpellEntity,
    onClick: () -> Unit,
    onCast: () -> Unit
) {
    val isAbilityOrFeat = spell.level < 0 ||
            spell.school.contains("Habilidade", true) ||
            spell.school.contains("Feat", true) ||
            spell.school.contains("Classe", true) ||
            spell.school.contains("Raça", true) ||
            spell.school.contains("Passiva", true) ||
            spell.school.contains("Traço", true)

    val labelText = when {
        isAbilityOrFeat -> "Habilidade / Traço • ${spell.school}"
        spell.level == 0 -> "Truque • ${spell.school}"
        else -> "Nível ${spell.level} • ${spell.school}"
    }

    val buttonText = when {
        isAbilityOrFeat -> "Ativar"
        spell.level == 0 -> "Conjurar"
        else -> "Conjurar"
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAbilityOrFeat) PrimaryGold.copy(alpha = 0.5f) else DividerColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spell.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = labelText,
                        fontSize = 12.sp,
                        color = if (isAbilityOrFeat) PrimaryGold else OnSurfaceVariant
                    )
                }

                Button(
                    onClick = onCast,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(buttonText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (spell.castingTime.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(spell.castingTime, fontSize = 11.sp, color = SecondaryParchment)
                    }
                }

                if (spell.range.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(spell.range, fontSize = 11.sp, color = SecondaryParchment)
                    }
                }

                if (spell.damageOrEffect.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryContainerGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(spell.damageOrEffect, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellDetailBottomSheet(
    spell: SpellEntity,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onCast: (Int) -> Unit
) {
    var selectedLevel by remember(spell) { mutableIntStateOf(spell.level.coerceAtLeast(1)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = spell.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGold
                    )
                    Text(
                        text = if (spell.level == 0) "TRUQUE • ${spell.school.uppercase()}" else "NÍVEL ${spell.level} • ${spell.school.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }
                Row {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir Magia", tint = com.example.ui.theme.DamageRed)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tempo de Conjuração:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text(spell.castingTime, fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Alcance:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text(spell.range, fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Componentes:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text(spell.components, fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = spell.description,
                fontSize = 14.sp,
                color = OnSurface,
                lineHeight = 20.sp
            )

            if (spell.higherLevels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Em níveis superiores:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PrimaryGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = spell.higherLevels,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Level Selector Bar & Cast Button
            if (spell.level > 0) {
                Text(
                    text = "SELECIONE O NÍVEL DO ESPAÇO DE MAGIA:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 3, 4).filter { it >= spell.level }.forEach { lvl ->
                        val isSelected = selectedLevel == lvl
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimaryGold else SurfaceRaised)
                                .border(1.dp, if (isSelected) PrimaryGold else DividerColor, RoundedCornerShape(6.dp))
                                .clickable { selectedLevel = lvl },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nível $lvl",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF3D2E00) else OnSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = { onCast(selectedLevel) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (spell.level == 0) "Conjurar Truque" else "Conjurar no Nível $selectedLevel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CreateSpellDialog(
    onDismiss: () -> Unit,
    onSave: (SpellEntity) -> Unit
) {
    var category by remember { mutableStateOf("Magia") } // "Magia", "Habilidade", "Traço Racial"
    var name by remember { mutableStateOf("") }
    var levelStr by remember { mutableStateOf("1") }
    var school by remember { mutableStateOf("Evocação") }
    var castingTime by remember { mutableStateOf("1 Ação") }
    var range by remember { mutableStateOf("18m") }
    var damageOrEffect by remember { mutableStateOf("") }
    var components by remember { mutableStateOf("V, S") }
    var description by remember { mutableStateOf("") }

    var showSuggestions by remember { mutableStateOf(false) }
    val suggestedSpells = remember(name, category) {
        val query = name.trim()
        if (query.isNotEmpty()) {
            val spellsList: List<PresetSpell> = DndSpellsDatabase.ALL_PRESET_SPELLS.filter {
                it.name.contains(query, ignoreCase = true)
            }

            val featsList: List<PresetSpell> = DndFeatsDatabase.ALL_PRESET_FEATS.filter {
                it.name.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true)
            }.map { feat ->
                PresetSpell(
                    name = "Talento: ${feat.name}",
                    level = -2,
                    school = "Talento / Feat",
                    castingTime = "Passiva",
                    range = "Pessoal",
                    damageOrEffect = feat.summary,
                    components = "Pré-req: ${feat.prerequisite}",
                    description = feat.description
                )
            }

            spellsList + featsList
        } else {
            emptyList()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Magia ou Habilidade", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Helper Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryContainerGold.copy(alpha = 0.2f))
                        .border(1.dp, PrimaryGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 Digite o nome para autocompletar magias oficiais D&D 5e ou crie um efeito Homebrew personalizado.",
                        fontSize = 11.sp,
                        color = SecondaryParchment
                    )
                }

                // Category FilterChips
                Text("Tipo de Habilidade:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Magia", "Habilidade", "Traço Racial").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                when (cat) {
                                    "Habilidade" -> {
                                        levelStr = "-1"
                                        school = "Guerreiro / Classe"
                                        castingTime = "1/Descanso Curto"
                                        components = "Ativo"
                                    }
                                    "Traço Racial" -> {
                                        levelStr = "-2"
                                        school = "Raça (Ex: Elfo)"
                                        castingTime = "Passivo"
                                        components = "Permanente"
                                    }
                                    else -> {
                                        levelStr = "1"
                                        school = "Evocação"
                                        castingTime = "1 Ação"
                                        components = "V, S"
                                    }
                                }
                            },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Name Input with Autocomplete
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            showSuggestions = true
                        },
                        label = { Text("Nome (Ex: Fireball, Visão no Escuro)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGold,
                            unfocusedBorderColor = DividerColor
                        )
                    )

                    // Autocomplete Dropdown List
                    if (showSuggestions && suggestedSpells.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "SUGESTÕES D&D 5E OFICIAIS:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                suggestedSpells.take(5).forEach { preset ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SurfaceBase)
                                            .clickable {
                                                name = preset.name
                                                levelStr = preset.level.toString()
                                                school = preset.school
                                                castingTime = preset.castingTime
                                                range = preset.range
                                                damageOrEffect = preset.damageOrEffect
                                                components = preset.components
                                                description = preset.description
                                                showSuggestions = false
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = preset.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryGold
                                            )
                                            Text(
                                                text = "Nível ${preset.level} • ${preset.school} • ${preset.range}",
                                                fontSize = 11.sp,
                                                color = OnSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (category == "Magia") {
                    OutlinedTextField(value = levelStr, onValueChange = { levelStr = it }, label = { Text("Nível (0 para Truque)") })
                    OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("Escola de Magia") })
                    OutlinedTextField(value = castingTime, onValueChange = { castingTime = it }, label = { Text("Tempo de Conjuração") })
                    OutlinedTextField(value = components, onValueChange = { components = it }, label = { Text("Componentes (V, S, M)") })
                } else {
                    OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("Origem / Classe / Feat") })
                    OutlinedTextField(value = castingTime, onValueChange = { castingTime = it }, label = { Text("Uso / Frequência (Ex: Passivo, 1/Descanso)") })
                }

                OutlinedTextField(value = range, onValueChange = { range = it }, label = { Text("Alcance / Raio") })
                OutlinedTextField(value = damageOrEffect, onValueChange = { damageOrEffect = it }, label = { Text("Dano / Efeito / Bônus") })

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição Detalhada / Homebrew") },
                    modifier = Modifier.height(110.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val lvl = levelStr.toIntOrNull() ?: if (category == "Habilidade") -1 else if (category == "Traço Racial") -2 else 1
                        val spell = SpellEntity(
                            name = name,
                            level = lvl,
                            school = school,
                            castingTime = castingTime,
                            range = range,
                            damageOrEffect = damageOrEffect,
                            components = components,
                            description = description,
                            isPrepared = true
                        )
                        onSave(spell)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Adicionar à Ficha", color = Color(0xFF3D2E00), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
