package com.example.ui.screens.dm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CharacterEntity
import com.example.domain.rules.DndRulesEngine
import com.example.ui.screens.campaign.NpcGenDialog
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.HealGreen
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel

sealed class CharacterListItem {
    data class PcItem(val character: CharacterEntity) : CharacterListItem()
    data class NpcItem(val npc: CampaignNpcEntity) : CharacterListItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmCharactersScreen(
    viewModel: GrimoireViewModel,
    onNavigateToCombat: () -> Unit
) {
    val allCharacters by viewModel.allCharacters.collectAsState()
    val campaignNpcs by viewModel.campaignNpcs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("Todos") } // "Todos", "Jogadores", "NPCs Neutros", "Inimigos"
    var selectedItemForDetail by remember { mutableStateOf<CharacterListItem?>(null) }
    var showNpcGenDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf("Todos", "Jogadores", "NPCs Neutros", "Inimigos")

    // Combine PCs and NPCs
    val allItems: List<CharacterListItem> = remember(allCharacters, campaignNpcs) {
        val pcs = allCharacters.map { CharacterListItem.PcItem(it) }
        val npcs = campaignNpcs.map { CharacterListItem.NpcItem(it) }
        pcs + npcs
    }

    val filteredItems = allItems.filter { item ->
        when (item) {
            is CharacterListItem.PcItem -> {
                val matchesSearch = item.character.name.contains(searchQuery, ignoreCase = true) ||
                        item.character.characterClass.contains(searchQuery, ignoreCase = true) ||
                        item.character.race.contains(searchQuery, ignoreCase = true)
                val matchesFilter = activeFilter == "Todos" || activeFilter == "Jogadores"
                matchesSearch && matchesFilter
            }
            is CharacterListItem.NpcItem -> {
                val matchesSearch = item.npc.name.contains(searchQuery, ignoreCase = true) ||
                        item.npc.roleOrTitle.contains(searchQuery, ignoreCase = true) ||
                        item.npc.description.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when (activeFilter) {
                    "NPCs Neutros" -> !item.npc.isHostile
                    "Inimigos" -> item.npc.isHostile
                    "Todos" -> true
                    else -> false
                }
                matchesSearch && matchesFilter
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DIRETÓRIO DE PERSONAGENS",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                    Text(
                        text = "Visão do Mestre • Fichas de Jogadores e NPCs",
                        fontSize = 11.sp,
                        color = OnSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.openNpcGenerator() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gerar NPC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar personagem, classe ou raça...", color = OnSurfaceVariant, fontSize = 13.sp) },
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
                    .height(50.dp)
            )

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = filter == activeFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeFilter = filter },
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

            // Characters List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems) { item ->
                    when (item) {
                        is CharacterListItem.PcItem -> PcCard(
                            pc = item.character,
                            onClick = { viewModel.openCharacterDetailPc(item.character) }
                        )
                        is CharacterListItem.NpcItem -> NpcCard(
                            npc = item.npc,
                            onClick = { viewModel.openCharacterDetailNpc(item.npc) },
                            onLaunchToCombat = {
                                viewModel.addNpcToCombat(item.npc)
                                onNavigateToCombat()
                            }
                        )
                    }
                }
            }
        }

        // Modal Bottom Sheet for Character Details
        if (selectedItemForDetail != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedItemForDetail = null },
                sheetState = sheetState,
                containerColor = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (val current = selectedItemForDetail!!) {
                        is CharacterListItem.PcItem -> PcDetailSheetView(
                            character = current.character,
                            onDismiss = { selectedItemForDetail = null }
                        )
                        is CharacterListItem.NpcItem -> NpcDetailSheetView(
                            npc = current.npc,
                            onDismiss = { selectedItemForDetail = null },
                            onLaunchToCombat = {
                                viewModel.addNpcToCombat(current.npc)
                                selectedItemForDetail = null
                                onNavigateToCombat()
                            }
                        )
                    }
                }
            }
        }

        if (showNpcGenDialog) {
            NpcGenDialog(
                onDismiss = { showNpcGenDialog = false },
                onGenerate = { presetType ->
                    viewModel.generateRandomNpc(presetType)
                    showNpcGenDialog = false
                }
            )
        }
    }
}

@Composable
fun PcCard(
    pc: CharacterEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (pc.avatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = pc.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        Text(pc.name.take(1), fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(pc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("JOGADOR (PC)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Text("${pc.characterClass} Nvl ${pc.level} • ${pc.race}", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("HP ${pc.currentHp}/${pc.maxHp}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HealGreen)
                Text("CA ${pc.ac}", fontSize = 11.sp, color = PrimaryGold)
            }
        }
    }
}

@Composable
fun NpcCard(
    npc: CampaignNpcEntity,
    onClick: () -> Unit,
    onLaunchToCombat: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (npc.isHostile) DamageRed.copy(alpha = 0.5f) else HealGreen.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (npc.avatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = npc.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        Text(npc.name.take(1), fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(npc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (npc.isHostile) DamageRed.copy(alpha = 0.2f) else HealGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (npc.isHostile) "INIMIGO" else "NPC NEUTRO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (npc.isHostile) DamageRed else HealGreen
                            )
                        }
                    }
                    Text("${npc.roleOrTitle} • ND ${npc.challengeRating}", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("HP ${npc.hp}/${npc.maxHp}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Text("CA ${npc.ac}", fontSize = 11.sp, color = PrimaryGold)
            }
        }
    }
}

@Composable
fun PcDetailSheetView(
    character: CharacterEntity,
    onDismiss: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(character.name, fontFamily = FontFamily.Serif, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("${character.characterClass} Nvl ${character.level} • ${character.race}", fontSize = 12.sp, color = OnSurfaceVariant)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
            }
        }

        // Attributes Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf<Pair<String, Int>>(
                "FOR" to character.str,
                "DES" to character.dex,
                "CON" to character.con,
                "INT" to character.intScore,
                "SAB" to character.wis,
                "CAR" to character.cha
            ).forEach { (scoreLabel, valNum) ->
                val mod = DndRulesEngine.getAbilityModifier(valNum)
                val modStr = if (mod >= 0) "+$mod" else "$mod"

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceRaised)
                        .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(scoreLabel, fontSize = 10.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(modStr, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        Text("$valNum", fontSize = 10.sp, color = OnSurfaceVariant)
                    }
                }
            }
        }

        // Defense Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceRaised)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("HP: ${character.currentHp}/${character.maxHp}", fontWeight = FontWeight.Bold, color = HealGreen)
            Text("CA: ${character.ac}", fontWeight = FontWeight.Bold, color = PrimaryGold)
            val dexMod = DndRulesEngine.getAbilityModifier(character.dex)
            Text("Iniciativa: ${if (dexMod >= 0) "+$dexMod" else "$dexMod"}", color = OnSurface)
            Text("Desloc: ${character.speed}", color = OnSurfaceVariant)
        }
    }
}

@Composable
fun NpcDetailSheetView(
    npc: CampaignNpcEntity,
    onDismiss: () -> Unit,
    onLaunchToCombat: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(npc.name, fontFamily = FontFamily.Serif, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("${npc.roleOrTitle} • ND ${npc.challengeRating}", fontSize = 12.sp, color = OnSurfaceVariant)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
            }
        }

        Text(npc.description, fontSize = 13.sp, color = OnSurface)

        Button(
            onClick = onLaunchToCombat,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Lançar no Combate Ativo", fontWeight = FontWeight.Bold)
        }
    }
}
