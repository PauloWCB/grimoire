package com.example.ui.screens.character

import com.example.domain.rules.DndExpansionRules
import com.example.ui.components.AutocompleteOutlinedTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CharacterEntity
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnPrimary
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableIntStateOf
import com.example.domain.rules.ActiveCondition
import com.example.domain.rules.DndRulesEngine
import com.example.ui.theme.HealGreen
import com.example.ui.viewmodel.GrimoireViewModel

// Hexagon Shape for Combat Tokens
val HexagonShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.5f, 0f)
    lineTo(w, h * 0.25f)
    lineTo(w, h * 0.75f)
    lineTo(w * 0.5f, h)
    lineTo(0f, h * 0.75f)
    lineTo(0f, h * 0.25f)
    close()
}

// Shield Shape for AC
val ShieldShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(0f, 0f)
    lineTo(w, 0f)
    lineTo(w, h * 0.6f)
    lineTo(w * 0.5f, h)
    lineTo(0f, h * 0.6f)
    close()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    viewModel: GrimoireViewModel,
    onOpenDiceTray: (String, Int, String) -> Unit
) {
    val characterState by viewModel.character.collectAsState()
    val allCharacters by viewModel.allCharacters.collectAsState()
    val character = characterState ?: CharacterEntity()

    val activeConditions = remember(character.conditionsJson) {
        DndRulesEngine.parseConditionsJson(character.conditionsJson)
    }
    var selectedConditionForDetail by remember { mutableStateOf<ActiveCondition?>(null) }
    var showAddConditionModal by remember { mutableStateOf(false) }

    var skillsExpanded by remember { mutableStateOf(true) }
    var highlightedAttribute by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showHpModal by remember { mutableStateOf(false) }
    var showCharacterSwitcher by remember { mutableStateOf(false) }
    var showNewCharDialog by remember { mutableStateOf(false) }
    var showJsonDialog by remember { mutableStateOf(false) }
    val expiredMsg by viewModel.expiredConditionMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Character Switcher & Quick Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showCharacterSwitcher = true },
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold)
                ) {
                    Text(
                        text = "📜 ${character.name} (Trocar)",
                        color = PrimaryGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showJsonDialog = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
                    ) {
                        Text("💾 Backup / JSON", fontSize = 11.sp, color = OnSurface)
                    }
                }
            }

            // 1. Header: Compact Character Identity
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = character.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" },
                    contentDescription = character.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, PrimaryGold, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = character.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryGold.copy(alpha = 0.1f))
                            .border(1.dp, PrimaryGold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${character.characterClass.uppercase()} ${character.level} / ${character.subClass.uppercase()} ${character.subClassLevel}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Ficha",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Vitals (HP)
            Card(
                onClick = { showHpModal = true },
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top Gold Accent Rule
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(PrimaryGold.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PONTOS DE VIDA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${character.currentHp} / ${character.maxHp}",
                            fontSize = 14.sp,
                            color = OnSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Large Interactive HP Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.adjustHp(-1) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(SurfaceBase, RoundedCornerShape(8.dp))
                                .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "-1 HP", tint = DamageRed)
                        }

                        Text(
                            text = "${character.currentHp}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold
                        )

                        IconButton(
                            onClick = { viewModel.adjustHp(+1) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(SurfaceBase, RoundedCornerShape(8.dp))
                                .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+1 HP", tint = PrimaryGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // HP Progress Bar
                    val hpRatio = if (character.maxHp > 0) (character.currentHp.toFloat() / character.maxHp).coerceIn(0f, 1f) else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceBase)
                            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(hpRatio)
                                    .background(DamageRed)
                            )
                            if (character.tempHp > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.15f)
                                        .background(TertiaryPurple)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Combat Stats Tokens (CA, INIC, DESL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CA (Shield)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp, 64.dp)
                            .clip(ShieldShape)
                            .background(SurfaceRaised)
                            .border(1.dp, DividerColor, ShieldShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${character.ac}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("CA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                }

                // INIC (Hexagon)
                val currentInic = DndRulesEngine.calculateInitiative(character.dex)
                val currentInicStr = DndRulesEngine.getModifierString(character.dex)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenDiceTray("D20", currentInic, "Iniciativa") }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(HexagonShape)
                            .background(SurfaceRaised)
                            .border(1.dp, PrimaryGold.copy(alpha = 0.5f), HexagonShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentInicStr,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("INIC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                }

                // DESL (Hexagon)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(HexagonShape)
                            .background(SurfaceRaised)
                            .border(1.dp, DividerColor, HexagonShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character.speed,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("DESL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                }
            }

            // Expired Condition Alert Banner
            if (expiredMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DamageRed.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DamageRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.clearExpiredConditionMessage() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DamageRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(expiredMsg!!, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                        Icon(Icons.Default.Close, contentDescription = "OK", tint = OnSurface, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // 4. Condições e Efeitos D&D 5e
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONDIÇÕES E EFEITOS ATIVOS (${activeConditions.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    if (activeConditions.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.advanceCharacterTurn() },
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("⏳ Finalizar Turno (-1)", fontSize = 11.sp, color = PrimaryGold)
                        }
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        OutlinedButton(
                            onClick = { showAddConditionModal = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGold),
                            shape = CircleShape,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adicionar", fontSize = 13.sp)
                        }
                    }

                    if (activeConditions.isEmpty()) {
                        item {
                            Text(
                                "Nenhuma condição ativa. Clique em Adicionar para selecionar uma condição D&D 5e.",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                            )
                        }
                    } else {
                        items(activeConditions) { cond ->
                            val isDamageOrRed = cond.name.lowercase() in listOf("cego", "envenenado", "paralisado", "atordoado", "inconsciente", "exaustão")
                            val chipColor = if (isDamageOrRed) DamageRed else PrimaryGold
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurfaceRaised)
                                    .border(1.dp, chipColor, CircleShape)
                                    .clickable { selectedConditionForDetail = cond }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (cond.isPermanent) Icons.Default.AutoAwesome else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = chipColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cond.displayLabel(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = chipColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Stats Grid (Atributos)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "FOR",
                        score = character.str,
                        mod = DndRulesEngine.getModifierString(character.str),
                        isHighlighted = (highlightedAttribute == "FOR"),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            highlightedAttribute = if (highlightedAttribute == "FOR") null else "FOR"
                            skillsExpanded = true
                        }
                    )
                    StatCard(
                        label = "DES",
                        score = character.dex,
                        mod = DndRulesEngine.getModifierString(character.dex),
                        isHighlighted = (highlightedAttribute == "DES"),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            highlightedAttribute = if (highlightedAttribute == "DES") null else "DES"
                            skillsExpanded = true
                        }
                    )
                    StatCard(
                        label = "CON",
                        score = character.con,
                        mod = DndRulesEngine.getModifierString(character.con),
                        isHighlighted = (highlightedAttribute == "CON"),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            highlightedAttribute = if (highlightedAttribute == "CON") null else "CON"
                            skillsExpanded = true
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "INT",
                        score = character.intScore,
                        mod = DndRulesEngine.getModifierString(character.intScore),
                        isHighlighted = (highlightedAttribute == "INT"),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            highlightedAttribute = if (highlightedAttribute == "INT") null else "INT"
                            skillsExpanded = true
                        }
                    )
                    StatCard(
                        label = "SAB",
                        score = character.wis,
                        mod = DndRulesEngine.getModifierString(character.wis),
                        isHighlighted = (highlightedAttribute == "SAB"),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            highlightedAttribute = if (highlightedAttribute == "SAB") null else "SAB"
                            skillsExpanded = true
                        }
                    )
                    StatCard(
                        label = "CAR",
                        score = character.cha,
                        mod = DndRulesEngine.getModifierString(character.cha),
                        isHighlighted = (highlightedAttribute == "CAR"),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            highlightedAttribute = if (highlightedAttribute == "CAR") null else "CAR"
                            skillsExpanded = true
                        }
                    )
                }
            }

            // 6. Complete D&D 5e Skills Grid (Perícias)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (highlightedAttribute != null) 2.dp else 1.dp,
                    color = if (highlightedAttribute != null) PrimaryGold else DividerColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { skillsExpanded = !skillsExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PERÍCIAS (REGRAS D&D 5E)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            if (highlightedAttribute != null) {
                                Text(
                                    text = "⚡ Destacando perícias de $highlightedAttribute",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGold
                                )
                            }
                        }
                        Icon(
                            imageVector = if (skillsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = if (highlightedAttribute != null) PrimaryGold else OnSurfaceVariant
                        )
                    }

                    if (skillsExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                        Spacer(modifier = Modifier.height(10.dp))

                        val skillsList = DndRulesEngine.ALL_SKILLS
                        val rows = skillsList.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            rows.forEach { rowSkills ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowSkills.forEach { skill ->
                                        val isHighlighted = highlightedAttribute != null && skill.attributeName == highlightedAttribute
                                        val bonusVal = DndRulesEngine.getSkillBonus(skill, character)
                                        val bonusStr = if (bonusVal >= 0) "+$bonusVal" else "$bonusVal"

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isHighlighted) PrimaryGold.copy(alpha = 0.25f) else SurfaceBase
                                                )
                                                .border(
                                                    width = if (isHighlighted) 2.dp else 1.dp,
                                                    color = if (isHighlighted) PrimaryGold else DividerColor,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    onOpenDiceTray("D20", bonusVal, "Teste: ${skill.name} (${skill.attributeName})")
                                                }
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = skill.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isHighlighted) PrimaryGold else OnSurface
                                                    )
                                                    Text(
                                                        text = skill.attributeName,
                                                        fontSize = 10.sp,
                                                        color = if (isHighlighted) PrimaryGold else OnSurfaceVariant
                                                    )
                                                }
                                                Text(
                                                    text = bonusStr,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isHighlighted) PrimaryGold else OnSurface
                                                )
                                            }
                                        }
                                    }
                                    if (rowSkills.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Spell Slots Summary (Espaços de Magia)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESPAÇOS DE MAGIA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Level 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                            }
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nível 1", fontSize = 14.sp, color = OnSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(character.slot1Max) { index ->
                                val isAvailable = index < character.slot1Current
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (isAvailable) PrimaryGold else Color.Transparent)
                                        .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, CircleShape)
                                        .clickable { viewModel.useSpellSlot(1) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Level 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nível 2", fontSize = 14.sp, color = OnSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(character.slot2Max) { index ->
                                val isAvailable = index < character.slot2Current
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (isAvailable) PrimaryGold else Color.Transparent)
                                        .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, CircleShape)
                                        .clickable { viewModel.useSpellSlot(2) }
                                )
                            }
                        }
                    }
                }
            }

            // 8. D&D 5e Rules Base Derived Calculations Card (Estatísticas Automáticas)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ESTATÍSTICAS AUTOMÁTICAS (REGRAS D&D 5E)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Proficiência", fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("+${DndRulesEngine.calculateProficiencyBonus(character.level)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("CD de Magia (${DndRulesEngine.getSpellcastingAbilityName(character.characterClass)})", fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("${DndRulesEngine.calculateSpellSaveDc(character)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ataque Mágico", fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("+${DndRulesEngine.calculateSpellAttackBonus(character)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Percepção Passiva", fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("${DndRulesEngine.calculatePassivePerception(character.wis, character.level)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
            }

            // 8. Quick Rests (Moved to Bottom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    onClick = { viewModel.performShortRest() },
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.NightShelter, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Descanso Curto", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        Text("GASTA DADOS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                    }
                }

                Card(
                    onClick = { viewModel.performLongRest() },
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Bed, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Descanso Longo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        Text("RECUPERA TUDO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                    }
                }
            }
        }

        // Floating Action Button (FAB for Dice Tray)
        FloatingActionButton(
            onClick = { onOpenDiceTray("D20", 4, "Rolagem") },
            containerColor = PrimaryGold,
            contentColor = OnPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.Casino, contentDescription = "Rolar Dados", modifier = Modifier.size(28.dp))
        }

        // Dialogs
        if (showHpModal) {
            EditHpModal(
                character = character,
                onDismiss = { showHpModal = false },
                onSaveHp = { cur, max, temp ->
                    viewModel.updateCurrentCharacter(
                        character.copy(currentHp = cur, maxHp = max, tempHp = temp)
                    )
                    showHpModal = false
                }
            )
        }

        if (showEditDialog) {
            EditCharacterDialog(
                character = character,
                onDismiss = { showEditDialog = false },
                onSave = { updatedChar ->
                    viewModel.updateCurrentCharacter(updatedChar)
                    showEditDialog = false
                }
            )
        }

        if (showCharacterSwitcher) {
            CharacterSwitcherDialog(
                allCharacters = allCharacters,
                activeCharacterId = character.id,
                onSelectCharacter = { id ->
                    viewModel.switchCharacter(id)
                    showCharacterSwitcher = false
                },
                onNewCharacter = {
                    showCharacterSwitcher = false
                    showNewCharDialog = true
                },
                onDeleteCharacter = { id ->
                    viewModel.deleteCharacter(id)
                },
                onDismiss = { showCharacterSwitcher = false }
            )
        }

        if (showNewCharDialog) {
            NewCharacterDialog(
                onDismiss = { showNewCharDialog = false },
                onCreate = { name, cls, race ->
                    viewModel.createNewCharacter(name, cls, race)
                    showNewCharDialog = false
                }
            )
        }

        if (showJsonDialog) {
            JsonExportImportDialog(
                jsonStr = viewModel.exportCharacterJson(),
                onImport = { json ->
                    val success = viewModel.importCharacterFromJson(json)
                    if (success) showJsonDialog = false
                    success
                },
                onDismiss = { showJsonDialog = false }
            )
        }

        if (selectedConditionForDetail != null) {
            ConditionDetailModal(
                condition = selectedConditionForDetail!!,
                onDismiss = { selectedConditionForDetail = null },
                onAddTurn = {
                    val updated = selectedConditionForDetail!!.copy(remainingTurns = selectedConditionForDetail!!.remainingTurns + 1)
                    viewModel.updateConditionInCharacter(updated)
                    selectedConditionForDetail = updated
                },
                onSubtractTurn = {
                    if (selectedConditionForDetail!!.remainingTurns > 1) {
                        val updated = selectedConditionForDetail!!.copy(remainingTurns = selectedConditionForDetail!!.remainingTurns - 1)
                        viewModel.updateConditionInCharacter(updated)
                        selectedConditionForDetail = updated
                    } else {
                        viewModel.removeConditionFromCharacter(selectedConditionForDetail!!.name)
                        selectedConditionForDetail = null
                    }
                },
                onRemove = {
                    viewModel.removeConditionFromCharacter(selectedConditionForDetail!!.name)
                    selectedConditionForDetail = null
                }
            )
        }

        if (showAddConditionModal) {
            AddConditionBottomSheet(
                onDismiss = { showAddConditionModal = false },
                onAdd = { newCond ->
                    viewModel.addConditionToCharacter(newCond)
                    showAddConditionModal = false
                }
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    score: Int,
    mod: String,
    isHighlighted: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) PrimaryGold.copy(alpha = 0.25f) else SurfaceRaised
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isHighlighted) 2.dp else 1.dp,
            color = if (isHighlighted) PrimaryGold else DividerColor
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isHighlighted) PrimaryGold else OnSurfaceVariant, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(mod, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isHighlighted) PrimaryGold else OnSurface)
            Text("$score", fontSize = 12.sp, color = if (isHighlighted) PrimaryGold else OnSurfaceVariant)
        }
    }
}

@Composable
fun SkillRow(
    name: String,
    mod: String,
    isProficient: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 14.sp, color = OnSurface)
        Text(
            text = mod,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isProficient) PrimaryGold else OnSurface
        )
    }
}

@Composable
fun EditCharacterDialog(
    character: CharacterEntity,
    onDismiss: () -> Unit,
    onSave: (CharacterEntity) -> Unit
) {
    var name by remember { mutableStateOf(character.name) }
    var cls by remember { mutableStateOf(character.characterClass) }
    var subCls by remember { mutableStateOf(character.subClass) }
    var race by remember { mutableStateOf(character.race) }
    var bg by remember { mutableStateOf(character.background) }
    var levelStr by remember { mutableStateOf(character.level.toString()) }
    var maxHpStr by remember { mutableStateOf(character.maxHp.toString()) }
    var acStr by remember { mutableStateOf(character.ac.toString()) }
    var speed by remember { mutableStateOf(character.speed) }
    var avatarUrl by remember { mutableStateOf(character.avatarUrl) }

    var strVal by remember { mutableStateOf(character.str.toString()) }
    var dexVal by remember { mutableStateOf(character.dex.toString()) }
    var conVal by remember { mutableStateOf(character.con.toString()) }
    var intVal by remember { mutableStateOf(character.intScore.toString()) }
    var wisVal by remember { mutableStateOf(character.wis.toString()) }
    var chaVal by remember { mutableStateOf(character.cha.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Ficha do Personagem", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                AutocompleteOutlinedTextField(
                    value = cls,
                    onValueChange = { cls = it },
                    label = "Classe Principal",
                    suggestions = DndExpansionRules.CLASSES,
                    modifier = Modifier.fillMaxWidth()
                )

                val subSuggestions = DndExpansionRules.SUBCLASSES_BY_CLASS.entries
                    .find { cls.contains(it.key.split(" ")[0], ignoreCase = true) }?.value
                    ?: DndExpansionRules.SUBCLASSES_BY_CLASS.values.flatten()

                AutocompleteOutlinedTextField(
                    value = subCls,
                    onValueChange = { subCls = it },
                    label = "Subclasse",
                    suggestions = subSuggestions,
                    modifier = Modifier.fillMaxWidth()
                )

                AutocompleteOutlinedTextField(
                    value = race,
                    onValueChange = { race = it },
                    label = "Raça / Linhagem",
                    suggestions = DndExpansionRules.RACES,
                    modifier = Modifier.fillMaxWidth()
                )

                AutocompleteOutlinedTextField(
                    value = bg,
                    onValueChange = { bg = it },
                    label = "Antecedente",
                    suggestions = DndExpansionRules.BACKGROUNDS,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = levelStr,
                    onValueChange = { levelStr = it },
                    label = { Text("Nível") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxHpStr,
                    onValueChange = { maxHpStr = it },
                    label = { Text("HP Máximo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = acStr,
                    onValueChange = { acStr = it },
                    label = { Text("CA (Classe de Armadura)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = speed,
                    onValueChange = { speed = it },
                    label = { Text("Deslocamento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("URL do Avatar/Foto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Atributos Básicos:", fontWeight = FontWeight.Bold, color = PrimaryGold, modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = strVal, onValueChange = { strVal = it }, label = { Text("FOR") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dexVal, onValueChange = { dexVal = it }, label = { Text("DES") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = conVal, onValueChange = { conVal = it }, label = { Text("CON") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = intVal, onValueChange = { intVal = it }, label = { Text("INT") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = wisVal, onValueChange = { wisVal = it }, label = { Text("SAB") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = chaVal, onValueChange = { chaVal = it }, label = { Text("CAR") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newLvl = levelStr.toIntOrNull() ?: character.level
                    val newHp = maxHpStr.toIntOrNull() ?: character.maxHp
                    val newAc = acStr.toIntOrNull() ?: character.ac
                    val updated = character.copy(
                        name = name,
                        characterClass = cls,
                        subClass = subCls,
                        race = race,
                        background = bg,
                        level = newLvl,
                        maxHp = newHp,
                        currentHp = character.currentHp.coerceAtMost(newHp),
                        ac = newAc,
                        speed = speed,
                        avatarUrl = avatarUrl,
                        str = strVal.toIntOrNull() ?: character.str,
                        dex = dexVal.toIntOrNull() ?: character.dex,
                        con = conVal.toIntOrNull() ?: character.con,
                        intScore = intVal.toIntOrNull() ?: character.intScore,
                        wis = wisVal.toIntOrNull() ?: character.wis,
                        cha = chaVal.toIntOrNull() ?: character.cha
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Salvar Alterações")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CharacterSwitcherDialog(
    allCharacters: List<CharacterEntity>,
    activeCharacterId: Long,
    onSelectCharacter: (Long) -> Unit,
    onNewCharacter: () -> Unit,
    onDeleteCharacter: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gerenciar Personagens", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allCharacters.forEach { char ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (char.id == activeCharacterId) PrimaryGold.copy(alpha = 0.15f) else SurfaceRaised
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, if (char.id == activeCharacterId) PrimaryGold else DividerColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCharacter(char.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(char.name, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("${char.characterClass} Nível ${char.level} • ${char.race}", fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                            if (allCharacters.size > 1) {
                                IconButton(onClick = { onDeleteCharacter(char.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = DamageRed)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNewCharacter,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("+ Criar Novo Personagem")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}

@Composable
fun NewCharacterDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cls by remember { mutableStateOf("Guerreiro") }
    var race by remember { mutableStateOf("Humano") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Personagem", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Aventureiro") })
                OutlinedTextField(value = cls, onValueChange = { cls = it }, label = { Text("Classe") })
                OutlinedTextField(value = race, onValueChange = { race = it }, label = { Text("Raça") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, cls, race) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) { Text("Criar Ficha") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun JsonExportImportDialog(
    jsonStr: String,
    onImport: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var inputJson by remember { mutableStateOf(jsonStr) }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backup / Importar JSON", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Copie o código abaixo para salvar um backup, ou cole o JSON de uma ficha para importar:", fontSize = 12.sp, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = inputJson,
                    onValueChange = { inputJson = it; errorMsg = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = { Text("JSON da Ficha") }
                )
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = DamageRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onImport(inputJson)
                    if (!success) {
                        errorMsg = "Formato JSON inválido. Verifique o código inserido."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) { Text("Importar JSON") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConditionBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (ActiveCondition) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedName by remember { mutableStateOf("Cego") }
    var isCustom by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var isPermanent by remember { mutableStateOf(false) }
    var turnsCount by remember { mutableIntStateOf(1) }
    var noteText by remember { mutableStateOf("") }

    val conditionRule = remember(selectedName) { DndRulesEngine.getConditionInfo(selectedName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aplicar Condição D&D 5e",
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }
            }

            Text("Selecione uma Condição das Regras:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)

            // Grid / Chips of all D&D 5e Conditions
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(DndRulesEngine.ALL_CONDITIONS) { cond ->
                    val isSel = !isCustom && selectedName.equals(cond.name, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) PrimaryGold else SurfaceRaised)
                            .border(1.dp, if (isSel) PrimaryGold else DividerColor, RoundedCornerShape(8.dp))
                            .clickable {
                                isCustom = false
                                selectedName = cond.name
                                if (cond.name == "Concentração") {
                                    isPermanent = true
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cond.name,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color(0xFF3D2E00) else OnSurface
                        )
                    }
                }
            }

            if (!isCustom) {
                // Rule Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceBase)
                        .border(1.dp, PrimaryGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Efeito D&D 5e: ${conditionRule.name}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(conditionRule.summary, fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }
            }

            // Duration Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tipo de Duração:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = !isPermanent,
                        onClick = { isPermanent = false },
                        label = { Text("Por Turnos") }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = isPermanent,
                        onClick = { isPermanent = true },
                        label = { Text("Contínuo/Concentração") }
                    )
                }
            }

            if (!isPermanent) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Número de Turnos:", fontSize = 14.sp, color = OnSurface)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { turnsCount = (turnsCount - 1).coerceAtLeast(1) }) {
                            Icon(Icons.Default.Remove, contentDescription = "-1", tint = PrimaryGold)
                        }
                        Text("$turnsCount ${if (turnsCount == 1) "turno" else "turnos"}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGold)
                        IconButton(onClick = { turnsCount += 1 }) {
                            Icon(Icons.Default.Add, contentDescription = "+1", tint = PrimaryGold)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Anotação Adicional (Ex: CD 14 CON, Escudo de Fé)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val finalName = if (isCustom) customName.ifBlank { "Condição" } else selectedName
                    val newCond = ActiveCondition(
                        name = finalName,
                        remainingTurns = if (isPermanent) 0 else turnsCount,
                        isPermanent = isPermanent,
                        note = noteText.trim()
                    )
                    onAdd(newCond)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Aplicar Condição ao Personagem", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDetailModal(
    condition: ActiveCondition,
    onDismiss: () -> Unit,
    onAddTurn: () -> Unit,
    onSubtractTurn: () -> Unit,
    onRemove: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val info = remember(condition.name) { DndRulesEngine.getConditionInfo(condition.name) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = DamageRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = info.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }
            }

            // Duration Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceBase)
                    .border(1.dp, PrimaryGold, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Duração Atual:", fontSize = 11.sp, color = OnSurfaceVariant)
                        Text(
                            text = condition.displayLabel(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }
                    if (!condition.isPermanent) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = onSubtractTurn, contentPadding = PaddingValues(horizontal = 8.dp)) {
                                Text("-1 Turno", fontSize = 11.sp)
                            }
                            OutlinedButton(onClick = onAddTurn, contentPadding = PaddingValues(horizontal = 8.dp)) {
                                Text("+1 Turno", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Rule Details
            Text("Descrição e Regras D&D 5e:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryGold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = info.fullDescription,
                    fontSize = 13.sp,
                    color = OnSurface,
                    lineHeight = 20.sp
                )
            }

            // Remove Button
            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(containerColor = DamageRed, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remover Condição", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditHpModal(
    character: CharacterEntity,
    onDismiss: () -> Unit,
    onSaveHp: (currentHp: Int, maxHp: Int, tempHp: Int) -> Unit
) {
    var curHp by remember { mutableIntStateOf(character.currentHp) }
    var maxHp by remember { mutableIntStateOf(character.maxHp) }
    var tempHp by remember { mutableIntStateOf(character.tempHp) }

    var curHpStr by remember { mutableStateOf(character.currentHp.toString()) }
    var maxHpStr by remember { mutableStateOf(character.maxHp.toString()) }
    var tempHpStr by remember { mutableStateOf(character.tempHp.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Pontos de Vida (PV)", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceBase)
                        .border(1.dp, PrimaryGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PV ATUAIS / MÁXIMOS", fontSize = 11.sp, color = OnSurfaceVariant, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$curHp / $maxHp",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold
                        )
                        if (tempHp > 0) {
                            Text("+$tempHp PV Temporários", fontSize = 12.sp, color = TertiaryPurple, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("Ajustes Rápidos de Dano / Cura:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(-10, -5, -1, 1, 5, 10).forEach { delta ->
                        OutlinedButton(
                            onClick = {
                                curHp = (curHp + delta).coerceIn(0, maxHp.coerceAtLeast(1))
                                curHpStr = curHp.toString()
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (delta > 0) "+$delta" else "$delta",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (delta > 0) HealGreen else DamageRed
                            )
                        }
                    }
                }

                Text("Deslize para alterar PV:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                Slider(
                    value = curHp.toFloat(),
                    onValueChange = {
                        curHp = it.toInt()
                        curHpStr = curHp.toString()
                    },
                    valueRange = 0f..(maxHp.coerceAtLeast(1)).toFloat(),
                    colors = SliderDefaults.colors(thumbColor = PrimaryGold, activeTrackColor = PrimaryGold)
                )

                OutlinedTextField(
                    value = curHpStr,
                    onValueChange = {
                        curHpStr = it
                        it.toIntOrNull()?.let { v -> curHp = v }
                    },
                    label = { Text("PV Atuais") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxHpStr,
                    onValueChange = {
                        maxHpStr = it
                        it.toIntOrNull()?.let { v -> maxHp = v }
                    },
                    label = { Text("PV Máximos") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tempHpStr,
                    onValueChange = {
                        tempHpStr = it
                        it.toIntOrNull()?.let { v -> tempHp = v }
                    },
                    label = { Text("PV Temporários") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCur = curHpStr.toIntOrNull() ?: curHp
                    val finalMax = maxHpStr.toIntOrNull() ?: maxHp
                    val finalTemp = tempHpStr.toIntOrNull() ?: tempHp
                    onSaveHp(finalCur, finalMax, finalTemp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
            ) {
                Text("Salvar PV", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
