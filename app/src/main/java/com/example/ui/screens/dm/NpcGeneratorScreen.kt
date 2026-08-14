package com.example.ui.screens.dm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignNpcEntity
import com.example.domain.rules.DndRulesEngine
import com.example.domain.rules.NpcGeneratorEngine
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.HealGreen
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcGeneratorScreen(
    campaignId: Long,
    onClose: () -> Unit,
    onConfirmSave: (CampaignNpcEntity) -> Unit
) {
    val presets = listOf("Cidadão", "Guarda/Mercenário", "Monstro", "Chefe", "Mago/Alquimista")
    var selectedPreset by remember { mutableStateOf("Guarda/Mercenário") }

    // Draft NPC state
    var draftNpc by remember {
        mutableStateOf(NpcGeneratorEngine.generateNpc(campaignId, "Guarda/Mercenário"))
    }

    // Editable form states initialized from draftNpc
    var nameInput by remember(draftNpc) { mutableStateOf(draftNpc.name) }
    var roleInput by remember(draftNpc) { mutableStateOf(draftNpc.roleOrTitle) }
    var crInput by remember(draftNpc) { mutableStateOf(draftNpc.challengeRating) }
    var hpInput by remember(draftNpc) { mutableStateOf(draftNpc.hp.toString()) }
    var acInput by remember(draftNpc) { mutableStateOf(draftNpc.ac.toString()) }
    var speedInput by remember(draftNpc) { mutableStateOf(draftNpc.speed) }
    var isHostileInput by remember(draftNpc) { mutableStateOf(draftNpc.isHostile) }
    var descInput by remember(draftNpc) { mutableStateOf(draftNpc.description) }
    var actionsInput by remember(draftNpc) { mutableStateOf(draftNpc.actionsJson) }
    var spellsInput by remember(draftNpc) { mutableStateOf(draftNpc.spellsJson) }

    fun rerollNpc() {
        draftNpc = NpcGeneratorEngine.generateNpc(campaignId, selectedPreset)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gerador de NPC da Campanha", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryGold)
                        Text("Ajuste as características antes de cadastrar no Grimório", fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = PrimaryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Archetype Preset Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. SELECIONE O ARQUÉTIPO / PRESET", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presets) { preset ->
                            FilterChip(
                                selected = selectedPreset == preset,
                                onClick = {
                                    selectedPreset = preset
                                    draftNpc = NpcGeneratorEngine.generateNpc(campaignId, preset)
                                },
                                label = { Text(preset, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGold,
                                    selectedLabelColor = Color(0xFF3D2E00),
                                    containerColor = SurfaceRaised,
                                    labelColor = OnSurface
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { rerollNpc() },
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gerar Novo Rascunho", fontSize = 12.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHostileInput) "Inimigo Hostil" else "Aliado/Neutro",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHostileInput) DamageRed else HealGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isHostileInput,
                                onCheckedChange = { isHostileInput = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DamageRed,
                                    checkedTrackColor = DamageRed.copy(alpha = 0.3f),
                                    uncheckedThumbColor = HealGreen,
                                    uncheckedTrackColor = HealGreen.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }

            // Preview Sheet Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PREVIEW DA FICHA DO NPC", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        }

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nome do NPC") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = roleInput,
                                onValueChange = { roleInput = it },
                                label = { Text("Título / Papel") },
                                modifier = Modifier.weight(1.5f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                            )
                            OutlinedTextField(
                                value = crInput,
                                onValueChange = { crInput = it },
                                label = { Text("ND") },
                                modifier = Modifier.weight(0.8f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = hpInput,
                                onValueChange = { hpInput = it },
                                label = { Text("HP Máx") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                            )
                            OutlinedTextField(
                                value = acInput,
                                onValueChange = { acInput = it },
                                label = { Text("CA") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                            )
                            OutlinedTextField(
                                value = speedInput,
                                onValueChange = { speedInput = it },
                                label = { Text("Deslocamento") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                            )
                        }

                        // Attributes Row
                        Text("ATRIBUTOS ROLADOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                "FOR" to draftNpc.str,
                                "DES" to draftNpc.dex,
                                "CON" to draftNpc.con,
                                "INT" to draftNpc.intScore,
                                "SAB" to draftNpc.wis,
                                "CAR" to draftNpc.cha
                            ).forEach { (lbl, valNum) ->
                                val mod = DndRulesEngine.getAbilityModifier(valNum)
                                val modStr = if (mod >= 0) "+$mod" else "$mod"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainer)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(lbl, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                                        Text(valNum.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                        Text(modStr, fontSize = 10.sp, color = HealGreen)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Descrição e Personalidade") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                        )

                        OutlinedTextField(
                            value = actionsInput,
                            onValueChange = { actionsInput = it },
                            label = { Text("Ataques e Ações (JSON/Texto)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                        )

                        OutlinedTextField(
                            value = spellsInput,
                            onValueChange = { spellsInput = it },
                            label = { Text("Magias / Habilidades Especiais") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                        )
                    }
                }
            }

            // Save Confirmation Button
            item {
                Button(
                    onClick = {
                        val finalNpc = draftNpc.copy(
                            name = nameInput.ifBlank { "NPC Sem Nome" },
                            roleOrTitle = roleInput.ifBlank { "Habitante" },
                            challengeRating = crInput.ifBlank { "1" },
                            hp = hpInput.toIntOrNull() ?: draftNpc.hp,
                            maxHp = hpInput.toIntOrNull() ?: draftNpc.maxHp,
                            ac = acInput.toIntOrNull() ?: draftNpc.ac,
                            speed = speedInput.ifBlank { "9m" },
                            isHostile = isHostileInput,
                            description = descInput,
                            actionsJson = actionsInput,
                            spellsJson = spellsInput
                        )
                        onConfirmSave(finalNpc)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar e Cadastrar NPC na Campanha", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
