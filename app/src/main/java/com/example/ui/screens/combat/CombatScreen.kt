package com.example.ui.screens.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CharacterEntity
import com.example.ui.components.RollingDiceOverlay
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.HealGreen
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.Combatant
import com.example.ui.viewmodel.GrimoireViewModel

@Composable
fun CombatScreen(
    viewModel: GrimoireViewModel,
    onOpenDiceTray: (String, Int, String) -> Unit
) {
    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()

    val currentRole by viewModel.currentRole.collectAsState()
    val combatants by viewModel.combatants.collectAsState()
    val currentTurnIndex by viewModel.currentTurnIndex.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val isCombatActive by viewModel.isCombatActive.collectAsState()
    val isRollingDiceAnimation by viewModel.isRollingDiceAnimation.collectAsState()
    val campaignNpcs by viewModel.campaignNpcs.collectAsState()
    val allCharacters by viewModel.allCharacters.collectAsState()

    var successSaves by remember { mutableIntStateOf(0) }
    var failSaves by remember { mutableIntStateOf(0) }

    var showAddCombatantDialog by remember { mutableStateOf(false) }
    var showEditInitiativeDialog by remember { mutableStateOf<Combatant?>(null) }
    var editingCombatant by remember { mutableStateOf<Combatant?>(null) }

    val isDm = currentRole == "DM"

    // If rolling dice animation is active, show the overlay
    if (isRollingDiceAnimation) {
        RollingDiceOverlay()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        if (!isCombatActive) {
            // COMBAT SETUP SCREEN
            CombatSetupScreen(
                viewModel = viewModel,
                isDm = isDm,
                campaignNpcs = campaignNpcs,
                allCharacters = allCharacters,
                onStartCombat = { selectedNpcs, pcInits ->
                    viewModel.startCombatWithAnimatedRolls(selectedNpcs, pcInits)
                }
            )
        } else {
            // ACTIVE COMBAT TRACKER SCREEN
            val activeCombatant = combatants.getOrNull(currentTurnIndex)
            val nextTurnIndex = if (combatants.isNotEmpty()) (currentTurnIndex + 1) % combatants.size else 0
            val nextCombatant = combatants.getOrNull(nextTurnIndex)

            val canUserAdvanceTurn = when {
                combatants.isEmpty() -> false
                isDm -> true
                activeCombatant != null && activeCombatant.isPlayer -> true
                else -> false
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header / Combat Controller Bar
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Rodada $currentRound",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isDm) PrimaryGold else HealGreen)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (isDm) "VISÃO DO MESTRE" else "VISÃO DO JOGADOR",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    if (activeCombatant != null) {
                                        Text(
                                            text = "Vez de: ${activeCombatant.name}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                        if (nextCombatant != null && combatants.size > 1) {
                                            Text(
                                                text = "Próximo: ${nextCombatant.name}",
                                                fontSize = 11.sp,
                                                color = OnSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (isDm) {
                                        IconButton(
                                            onClick = { showAddCombatantDialog = true },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceContainer)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Adicionar Combatente", tint = PrimaryGold, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.endCombatTracker() },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceContainer)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Encerrar / Novo Combate", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Advance Turn Main Action
                            Button(
                                onClick = { viewModel.nextCombatTurn() },
                                enabled = canUserAdvanceTurn,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGold,
                                    contentColor = Color(0xFF3D2E00),
                                    disabledContainerColor = SurfaceContainer,
                                    disabledContentColor = OnSurfaceVariant
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        canUserAdvanceTurn && isDm -> "Avançar Turno (Mestre)"
                                        canUserAdvanceTurn -> "Encerrar Meu Turno"
                                        else -> "Aguardando ação do Mestre / Inimigo..."
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Death Saves Tracker (Shown if character is at 0 HP or user wants it)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DamageRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = DamageRed, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TESTES DE RESISTÊNCIA CONTRA A MORTE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DamageRed,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Button(
                                    onClick = { onOpenDiceTray("D20", 0, "Teste de Morte") },
                                    colors = ButtonDefaults.buttonColors(containerColor = DamageRed, contentColor = Color.White),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rolar D20", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SUCESSOS (10+)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HealGreen)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        for (i in 1..3) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (i <= successSaves) HealGreen else SurfaceContainer)
                                                    .clickable { successSaves = if (successSaves >= i) i - 1 else i },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (i <= successSaves) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("FALHAS (< 10)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DamageRed)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        for (i in 1..3) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (i <= failSaves) DamageRed else SurfaceContainer)
                                                    .clickable { failSaves = if (failSaves >= i) i - 1 else i },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (i <= failSaves) {
                                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Initiative Order List
                item {
                    Text(
                        text = "ORDEM DE INICIATIVA DA RODADA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold,
                        letterSpacing = 1.sp
                    )
                }

                itemsIndexed(combatants) { index, combatant ->
                    val isActive = index == currentTurnIndex
                    val isNext = index == nextTurnIndex && combatants.size > 1

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isActive -> SurfaceRaised
                                isNext -> SurfaceContainer
                                else -> SurfaceBase
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isActive) 2.dp else 1.dp,
                            color = when {
                                isActive -> PrimaryGold
                                isNext -> PrimaryGold.copy(alpha = 0.5f)
                                else -> DividerColor
                            }
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingCombatant = combatant }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    // Initiative Badge (Clickable for DM/User to override)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isActive) PrimaryGold else SurfaceContainer)
                                            .clickable {
                                                if (isDm || combatant.isPlayer) {
                                                    showEditInitiativeDialog = combatant
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${combatant.initiative}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isActive) Color(0xFF3D2E00) else PrimaryGold
                                        )
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = combatant.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = OnSurface
                                            )
                                            if (isActive) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(PrimaryGold)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("SUA VEZ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                }
                                            } else if (isNext) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(PrimaryGold.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("PRÓXIMO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                                                }
                                            }
                                        }

                                        Text(
                                            text = if (combatant.isPlayer) "Jogador" else "NPC / Monstro",
                                            fontSize = 11.sp,
                                            color = OnSurfaceVariant
                                        )

                                        if (combatant.conditions.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                combatant.conditions.forEach { cond ->
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(DamageRed.copy(alpha = 0.2f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(cond, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DamageRed)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Health Display according to DM vs Player perspective
                                Column(horizontalAlignment = Alignment.End) {
                                    when {
                                        isDm || combatant.name == character.name -> { // DM sees exact numbers
                                            Text(
                                                text = "${combatant.currentHp}/${combatant.maxHp} HP",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (combatant.currentHp <= 0) DamageRed else HealGreen
                                            )
                                            Text("CA ${combatant.ac}", fontSize = 11.sp, color = PrimaryGold)
                                        }
                                        combatant.isPlayer -> { // Player sees % for other players
                                            val pct = ((combatant.currentHp.toFloat() / combatant.maxHp.coerceAtLeast(1)) * 100).toInt()
                                            Text(
                                                text = "$pct% HP",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HealGreen
                                            )
                                            Text("Aliado", fontSize = 11.sp, color = OnSurfaceVariant)
                                        }
                                        else -> { // Enemy NPC visibility for Players (Status only)
                                            val pct = combatant.currentHp.toFloat() / combatant.maxHp.coerceAtLeast(1)
                                            val (statusText, statusColor) = when {
                                                combatant.currentHp <= 0 -> "Inconsciente / Derrotado" to DamageRed
                                                pct < 0.25f -> "MUITO FERIDO" to DamageRed
                                                pct < 0.75f -> "Ferido" to Color(0xFFFF9800)
                                                else -> "Saudável" to HealGreen
                                            }
                                            Text(
                                                text = statusText,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                            Text("Inimigo", fontSize = 11.sp, color = OnSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddCombatantDialog) {
            AddCombatantDialog(
                onDismiss = { showAddCombatantDialog = false },
                onAdd = { name, init, hp, isPlayer, ac ->
                    viewModel.addCombatant(name, init, hp, isPlayer, ac)
                    showAddCombatantDialog = false
                }
            )
        }

        if (showEditInitiativeDialog != null) {
            val c = showEditInitiativeDialog!!
            var newInitStr by remember { mutableStateOf("${c.initiative}") }

            AlertDialog(
                onDismissRequest = { showEditInitiativeDialog = null },
                title = { Text("Alterar Iniciativa (${c.name})", color = PrimaryGold, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = newInitStr,
                        onValueChange = { newInitStr = it },
                        label = { Text("Nova Iniciativa (Dado Físico ou Modificador)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newInit = newInitStr.toIntOrNull() ?: c.initiative
                            viewModel.overrideInitiative(c.id, newInit)
                            showEditInitiativeDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
                    ) {
                        Text("Atualizar Ordem")
                    }
                },
                dismissButton = { TextButton(onClick = { showEditInitiativeDialog = null }) { Text("Cancelar") } }
            )
        }

        if (editingCombatant != null) {
            val c = editingCombatant!!
            EditCombatantHpAndConditionsDialog(
                combatant = c,
                onDismiss = { editingCombatant = null },
                onUpdateHp = { newHp -> viewModel.updateCombatantHp(c.id, newHp) },
                onToggleCondition = { cond -> viewModel.toggleCombatantCondition(c.id, cond) },
                onRemove = {
                    viewModel.removeCombatant(c.id)
                    editingCombatant = null
                }
            )
        }
    }
}

@Composable
fun CombatSetupScreen(
    viewModel: GrimoireViewModel,
    isDm: Boolean,
    campaignNpcs: List<CampaignNpcEntity>,
    allCharacters: List<CharacterEntity>,
    onStartCombat: (List<CampaignNpcEntity>, Map<String, Int>) -> Unit
) {
    val selectedNpcs = remember { mutableStateMapOf<Long, Boolean>() }
    val pcInits = remember { mutableStateMapOf<String, String>() }

    // Initialize all NPCs as selected by default
    remember(campaignNpcs) {
        campaignNpcs.forEach { npc -> selectedNpcs[npc.id] = true }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = " PREPARAÇÃO DE COMBATE E INICIATIVA",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Selecione os NPCs/Monstros que entrarão em combate e insira ou role as iniciativas dos Jogadores.",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        // Section: Jogadores (PCs)
        item {
            Text(
                text = "INICIATIVAS DOS JOGADORES (PCS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HealGreen,
                letterSpacing = 1.sp
            )
        }

        items(allCharacters) { pc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
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
                    Column {
                        Text(pc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("${pc.characterClass} Nvl ${pc.level}", fontSize = 12.sp, color = OnSurfaceVariant)
                    }

                    OutlinedTextField(
                        value = pcInits[pc.name] ?: "",
                        onValueChange = { pcInits[pc.name] = it },
                        placeholder = { Text("1d20", fontSize = 12.sp, color = OnSurfaceVariant) },
                        label = { Text("Iniciativa", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGold,
                            unfocusedBorderColor = DividerColor
                        ),
                        singleLine = true,
                        modifier = Modifier.width(100.dp)
                    )
                }
            }
        }

        // Section: NPCs e Inimigos da Campanha
        item {
            Text(
                text = "NPCS E MONSTROS DISPONÍVEIS NA CAMPANHA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGold,
                letterSpacing = 1.sp
            )
        }

        items(campaignNpcs) { npc ->
            val isChecked = selectedNpcs[npc.id] ?: true

            Card(
                colors = CardDefaults.cardColors(containerColor = if (isChecked) SurfaceRaised else SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isChecked) (if (npc.isHostile) DamageRed else HealGreen) else DividerColor
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { selectedNpcs[npc.id] = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryGold,
                                checkmarkColor = Color.Black
                            )
                        )

                        Column {
                            Text(npc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("${npc.roleOrTitle} • ND ${npc.challengeRating} • HP ${npc.hp}", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (npc.isHostile) DamageRed.copy(alpha = 0.2f) else HealGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (npc.isHostile) "INIMIGO" else "ALIADO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (npc.isHostile) DamageRed else HealGreen
                        )
                    }
                }
            }
        }

        // Launch Button
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val activeNpcList = campaignNpcs.filter { selectedNpcs[it.id] == true }
                    val pcInitMap = pcInits.mapValues { it.value.toIntOrNull() ?: 10 }
                    onStartCombat(activeNpcList, pcInitMap)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ROLAR INICIATIVAS & INICIAR COMBATE", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AddCombatantDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Int, Int, Boolean, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var initStr by remember { mutableStateOf("15") }
    var hpStr by remember { mutableStateOf("30") }
    var acStr by remember { mutableStateOf("14") }
    var isPlayer by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Novo Combatente", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Combatente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = initStr,
                        onValueChange = { initStr = it },
                        label = { Text("Iniciativa") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = hpStr,
                        onValueChange = { hpStr = it },
                        label = { Text("HP Máx") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                OutlinedTextField(
                    value = acStr,
                    onValueChange = { acStr = it },
                    label = { Text("Classe de Armadura (CA)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPlayer = !isPlayer },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tipo: ${if (isPlayer) "Jogador (PC)" else "NPC / Inimigo"}", fontSize = 13.sp, color = OnSurface)
                    Checkbox(
                        checked = isPlayer,
                        onCheckedChange = { isPlayer = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryGold)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val init = initStr.toIntOrNull() ?: 10
                    val hp = hpStr.toIntOrNull() ?: 20
                    val ac = acStr.toIntOrNull() ?: 14
                    onAdd(name.ifBlank { "Combatente" }, init, hp, isPlayer, ac)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
            ) {
                Text("Adicionar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditCombatantHpAndConditionsDialog(
    combatant: Combatant,
    onDismiss: () -> Unit,
    onUpdateHp: (Int) -> Unit,
    onToggleCondition: (String) -> Unit,
    onRemove: () -> Unit
) {
    var hpText by remember(combatant) { mutableStateOf(combatant.currentHp.toString()) }
    var customDeltaText by remember { mutableStateOf("") }

    val currentHpVal = hpText.toIntOrNull() ?: combatant.currentHp
    val maxHpVal = combatant.maxHp.coerceAtLeast(1)
    val hpRatio = (currentHpVal.toFloat() / maxHpVal).coerceIn(0f, 1f)
    val hpColor = when {
        currentHpVal <= 0 -> DamageRed
        hpRatio < 0.3f -> Color(0xFFFF9800)
        else -> HealGreen
    }

    val dndConditions = listOf(
        "Cego", "Charmado", "Envenenado", "Caído", "Atordoado", "Agarrado",
        "Incapacitado", "Invisível", "Paralisado", "Petrificado", "Amedrontado", "Inconsciente", "Exaustão"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = combatant.name,
                        color = PrimaryGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CA ${combatant.ac}",
                        color = PrimaryGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = if (combatant.isPlayer) "Personagem Jogador" else "NPC / Inimigo em Combate",
                    fontSize = 11.sp,
                    color = OnSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // HP Health Bar Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PONTOS DE VIDA (HP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                            Text("$currentHpVal / ${combatant.maxHp} HP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = hpColor)
                        }

                        LinearProgressIndicator(
                            progress = { hpRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = hpColor,
                            trackColor = SurfaceContainer
                        )
                    }
                }

                // Quick HP Modifiers (Row of Dano and Row of Cura)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("AJUSTE RÁPIDO DE DANO / CURA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Quick Damage
                        listOf(-10, -5, -1).forEach { delta ->
                            OutlinedButton(
                                onClick = {
                                    val newHp = (currentHpVal + delta).coerceAtLeast(0)
                                    hpText = newHp.toString()
                                    onUpdateHp(newHp)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DamageRed.copy(alpha = 0.6f))
                            ) {
                                Text("$delta", color = DamageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Quick Heal
                        listOf(+1, +5, +10).forEach { delta ->
                            OutlinedButton(
                                onClick = {
                                    val newHp = currentHpVal + delta
                                    hpText = newHp.toString()
                                    onUpdateHp(newHp)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HealGreen.copy(alpha = 0.6f))
                            ) {
                                Text("+$delta", color = HealGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Direct HP Input or Custom Delta Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customDeltaText,
                        onValueChange = { customDeltaText = it },
                        label = { Text("Valor") },
                        placeholder = { Text("Ex: 12") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGold,
                            unfocusedBorderColor = DividerColor
                        )
                    )

                    Button(
                        onClick = {
                            val amount = customDeltaText.toIntOrNull() ?: 0
                            if (amount > 0) {
                                val newHp = (currentHpVal - amount).coerceAtLeast(0)
                                hpText = newHp.toString()
                                onUpdateHp(newHp)
                                customDeltaText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DamageRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dano", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val amount = customDeltaText.toIntOrNull() ?: 0
                            if (amount > 0) {
                                val newHp = currentHpVal + amount
                                hpText = newHp.toString()
                                onUpdateHp(newHp)
                                customDeltaText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HealGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cura", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Direct HP field in case they want to set explicit HP value
                OutlinedTextField(
                    value = hpText,
                    onValueChange = { newValue ->
                        hpText = newValue
                        newValue.toIntOrNull()?.let { num -> onUpdateHp(num) }
                    },
                    label = { Text("Definir HP Atual Direto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = DividerColor
                    )
                )

                // Conditions Section
                Text("CONDIÇÕES DE COMBATE (D&D 5E)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dndConditions.forEach { cond ->
                        val isApplied = combatant.conditions.contains(cond)
                        FilterChip(
                            selected = isApplied,
                            onClick = { onToggleCondition(cond) },
                            label = { Text(cond, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DamageRed,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceRaised,
                                labelColor = OnSurface
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
            ) {
                Text("Concluído", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onRemove) {
                Text("Remover do Combate", color = DamageRed)
            }
        }
    )
}
