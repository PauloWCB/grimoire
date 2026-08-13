package com.example.ui.screens.ascension

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterEntity
import com.example.domain.rules.DndFeatsDatabase
import com.example.domain.rules.DndSpellsDatabase
import com.example.domain.rules.PresetFeat
import com.example.domain.rules.PresetSpell
import com.example.ui.theme.DividerColor
import com.example.ui.theme.HealGreen
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AscensionWizardModal(
    viewModel: GrimoireViewModel,
    sheetState: androidx.compose.material3.SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val step by viewModel.ascensionStep.collectAsState()
    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeAscensionFlow() },
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header / Stepper Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (step in 2..4) {
                        IconButton(onClick = { viewModel.setAscensionStep(step - 1) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = PrimaryGold)
                        }
                    }
                    Text(
                        text = if (step == 5) "Resumo de Evolução" else "Ascensão de Nível",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                }
                IconButton(onClick = { viewModel.closeAscensionFlow() }) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }
            }

            if (step <= 4) {
                Spacer(modifier = Modifier.height(8.dp))
                val totalCurrentLvl = character.level + if (character.subClass.isNotBlank()) character.subClassLevel else 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Passo $step de 4", fontSize = 12.sp, color = SecondaryParchment, fontWeight = FontWeight.Bold)
                    Text("Nível Total ${totalCurrentLvl + 1}", fontSize = 12.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { step / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryGold,
                    trackColor = SurfaceRaised
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (step) {
                1 -> Step1ClassSelection(viewModel, character)
                2 -> Step2HpRoll(viewModel, character)
                3 -> Step3NewFeatures(viewModel, character)
                4 -> Step4Choices(viewModel, character)
                5 -> Step5Summary(viewModel, character)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1ClassSelection(viewModel: GrimoireViewModel, character: CharacterEntity) {
    val selectedClass by viewModel.ascensionClass.collectAsState()
    val allDndClasses = remember {
        listOf("Bárbaro", "Bardo", "Clérigo", "Druida", "Feiticeiro", "Guerreiro", "Ladino", "Mago", "Monge", "Paladino", "Patrulheiro", "Bruxo")
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    val primaryClassName = character.characterClass.ifBlank { "Guerreiro" }
    val secondaryClassName = character.subClass

    Column {
        Text("Escolha a Classe para este Nível", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Text("Evolua sua classe principal ou escolha uma nova multiclasse.", fontSize = 12.sp, color = OnSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // Option 1: Primary Class
        val isPrimarySelected = selectedClass.equals(primaryClassName, ignoreCase = true)
        Card(
            onClick = { viewModel.setAscensionClass(primaryClassName) },
            colors = CardDefaults.cardColors(containerColor = if (isPrimarySelected) SurfaceRaised else SurfaceBase),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isPrimarySelected) PrimaryGold else DividerColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainerGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = PrimaryGold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("$primaryClassName (Classe Principal)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text("Avança para Nível ${character.level + 1} de $primaryClassName", fontSize = 12.sp, color = SecondaryParchment)
                }
                RadioButton(
                    selected = isPrimarySelected,
                    onClick = { viewModel.setAscensionClass(primaryClassName) },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Existing Subclass / Multiclass if present
        if (secondaryClassName.isNotBlank()) {
            val isSecondarySelected = selectedClass.equals(secondaryClassName, ignoreCase = true)
            Card(
                onClick = { viewModel.setAscensionClass(secondaryClassName) },
                colors = CardDefaults.cardColors(containerColor = if (isSecondarySelected) SurfaceRaised else SurfaceBase),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSecondarySelected) PrimaryGold else DividerColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceBase),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$secondaryClassName (Multiclasse Atual)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Avança para Nível ${character.subClassLevel + 1} de $secondaryClassName", fontSize = 12.sp, color = SecondaryParchment)
                    }
                    RadioButton(
                        selected = isSecondarySelected,
                        onClick = { viewModel.setAscensionClass(secondaryClassName) },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Option 3: Other D&D Multiclass Dropdown
        val isOtherSelected = !isPrimarySelected && (!secondaryClassName.isNotBlank() || !selectedClass.equals(secondaryClassName, ignoreCase = true))
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isOtherSelected) SurfaceRaised else SurfaceBase),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isOtherSelected) PrimaryGold else DividerColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceBase),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = OnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Outra Multiclasse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Selecione qualquer classe oficial de D&D 5e", fontSize = 12.sp, color = SecondaryParchment)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (isOtherSelected) selectedClass else "Escolher classe...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGold,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = SurfaceBase,
                            unfocusedContainerColor = SurfaceBase
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        allDndClasses.forEach { cls ->
                            DropdownMenuItem(
                                text = { Text(cls) },
                                onClick = {
                                    viewModel.setAscensionClass(cls)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.setAscensionStep(2) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Avançar para Pontos de Vida", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun Step2HpRoll(viewModel: GrimoireViewModel, character: CharacterEntity) {
    val hpMode by viewModel.ascensionHpMode.collectAsState()
    val chosenClass by viewModel.ascensionClass.collectAsState()

    val (hitDie, dieText) = when {
        chosenClass.contains("Bárbaro", ignoreCase = true) -> Pair(12, "d12")
        chosenClass.contains("Guerreiro", ignoreCase = true) || chosenClass.contains("Paladino", ignoreCase = true) || chosenClass.contains("Patrulheiro", ignoreCase = true) -> Pair(10, "d10")
        chosenClass.contains("Mago", ignoreCase = true) || chosenClass.contains("Feiticeiro", ignoreCase = true) -> Pair(6, "d6")
        else -> Pair(8, "d8") // Clérigo, Bardo, Druida, Ladino, Monge, Bruxo
    }

    val avgRoll = hitDie / 2 + 1
    val conMod = (character.con - 10) / 2
    var rolledHp by remember { mutableIntStateOf(avgRoll) }

    val gainedHp = if (hpMode == "Roll") (rolledHp + conMod).coerceAtLeast(1) else (avgRoll + conMod).coerceAtLeast(1)
    val newMaxHp = character.maxHp + gainedHp

    Column {
        Text("Aumento de Pontos de Vida ($chosenClass)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Text("Dado de Vida: 1$dieText + Modificador de Constituição (${if (conMod >= 0) "+$conMod" else "$conMod"}).", fontSize = 12.sp, color = OnSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Roll option
            Card(
                onClick = { viewModel.setAscensionHpMode("Roll") },
                colors = CardDefaults.cardColors(containerColor = if (hpMode == "Roll") SurfaceRaised else SurfaceBase),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (hpMode == "Roll") PrimaryGold else DividerColor),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Rolar 1$dieText", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text("Dado: $rolledHp | CON: ${if (conMod >= 0) "+$conMod" else "$conMod"}", fontSize = 11.sp, color = SecondaryParchment)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { rolledHp = Random.nextInt(1, hitDie + 1) },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Rolar Novamente", fontSize = 10.sp, color = PrimaryGold)
                    }
                }
            }

            // Average option
            Card(
                onClick = { viewModel.setAscensionHpMode("Average") },
                colors = CardDefaults.cardColors(containerColor = if (hpMode == "Average") SurfaceRaised else SurfaceBase),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (hpMode == "Average") PrimaryGold else DividerColor),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Média Segura", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text("Fixo: $avgRoll + ${if (conMod >= 0) "+$conMod" else "$conMod"}", fontSize = 11.sp, color = SecondaryParchment)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Diff Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceRaised)
                .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOVO MÁXIMO DE PV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${character.maxHp}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryGold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("$newMaxHp", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = HealGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("(+$gainedHp)", fontSize = 14.sp, color = HealGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.setAscensionStep(3) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Ver Recursos Desbloqueados", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun Step3NewFeatures(viewModel: GrimoireViewModel, character: CharacterEntity) {
    val chosenClass by viewModel.ascensionClass.collectAsState()

    Column {
        Text("Recursos de Nível Desbloqueados", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Text("Benefícios concedidos pela classe $chosenClass.", fontSize = 12.sp, color = OnSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Aumento de Atributo ou Talento (Feat)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Escolha +2 em um Atributo ou escolha 1 novo Talento oficial.", fontSize = 12.sp, color = SecondaryParchment)
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Aprender Novas Magias / Feats", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Acesse e selecione magias disponíveis para sua classe.", fontSize = 12.sp, color = SecondaryParchment)
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Ajustes de Espaços de Magia", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1º Nível: ${character.slot1Max}", fontSize = 12.sp, color = OnSurface)
                        Text("2º Nível: ${character.slot2Max}", fontSize = 12.sp, color = OnSurface)
                        Text("3º Nível: ${character.slot3Max}", fontSize = 12.sp, color = OnSurface)
                        Text("4º Nível: ${character.slot4Max}", fontSize = 12.sp, color = OnSurface)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.setAscensionStep(4) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Fazer Escolhas de Talento & Magias", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step4Choices(viewModel: GrimoireViewModel, character: CharacterEntity) {
    val choiceType by viewModel.ascensionChoiceType.collectAsState()
    val selectedFeat by viewModel.ascensionSelectedFeat.collectAsState()
    val selectedAttribute by viewModel.ascensionSelectedAttribute.collectAsState()
    val selectedSpells by viewModel.ascensionSelectedSpells.collectAsState()

    var featDropdownExpanded by remember { mutableStateOf(false) }
    var spellSearchQuery by remember { mutableStateOf("") }

    val attributeOptions = listOf("FOR", "DES", "CON", "INT", "SAB", "CAR")
    val attributeNames = mapOf(
        "FOR" to "Força (FOR)",
        "DES" to "Destreza (DES)",
        "CON" to "Constituição (CON)",
        "INT" to "Inteligência (INT)",
        "SAB" to "Sabedoria (SAB)",
        "CAR" to "Carisma (CAR)"
    )

    Column {
        Text("Escolha de Talento ou Atributo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Text("Selecione como deseja evoluir o seu personagem neste nível.", fontSize = 12.sp, color = OnSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // Option 1: ASI (+2 Atributo)
        Card(
            onClick = { viewModel.setAscensionChoiceType("ASI") },
            colors = CardDefaults.cardColors(containerColor = if (choiceType == "ASI") SurfaceRaised else SurfaceBase),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (choiceType == "ASI") PrimaryGold else DividerColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = choiceType == "ASI",
                        onClick = { viewModel.setAscensionChoiceType("ASI") },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Aumento de Atributo (+2)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Adicione +2 ao valor total de um atributo principal.", fontSize = 11.sp, color = SecondaryParchment)
                    }
                }

                if (choiceType == "ASI") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Selecione o Atributo:", fontSize = 12.sp, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        attributeOptions.forEach { attr ->
                            val isSel = selectedAttribute == attr
                            OutlinedButton(
                                onClick = { viewModel.setAscensionSelectedAttribute(attr) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSel) PrimaryGold else Color.Transparent,
                                    contentColor = if (isSel) Color(0xFF3D2E00) else OnSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) PrimaryGold else DividerColor),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Text(attr, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Feat (Talento)
        Card(
            onClick = { viewModel.setAscensionChoiceType("Feat") },
            colors = CardDefaults.cardColors(containerColor = if (choiceType == "Feat") SurfaceRaised else SurfaceBase),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (choiceType == "Feat") PrimaryGold else DividerColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = choiceType == "Feat",
                        onClick = { viewModel.setAscensionChoiceType("Feat") },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Escolher um Talento (Feat)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Selecione uma habilidade especial da biblioteca de Feats.", fontSize = 11.sp, color = SecondaryParchment)
                    }
                }

                if (choiceType == "Feat") {
                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = featDropdownExpanded,
                        onExpandedChange = { featDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedFeat?.name ?: "Selecione um Talento...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = featDropdownExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGold,
                                unfocusedBorderColor = DividerColor,
                                focusedContainerColor = SurfaceBase,
                                unfocusedContainerColor = SurfaceBase
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = featDropdownExpanded,
                            onDismissRequest = { featDropdownExpanded = false }
                        ) {
                            DndFeatsDatabase.ALL_PRESET_FEATS.forEach { feat ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(feat.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(feat.summary, fontSize = 11.sp, color = SecondaryParchment)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setAscensionSelectedFeat(feat)
                                        featDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    selectedFeat?.let { feat ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceBase)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Pré-requisito: ${feat.prerequisite}", fontSize = 11.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(feat.description, fontSize = 11.sp, color = OnSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spells Selection
        Text("Aprender Novas Magias (Opcional)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Text("Marque as magias que seu personagem aprendeu neste nível:", fontSize = 12.sp, color = OnSurfaceVariant)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = spellSearchQuery,
            onValueChange = { spellSearchQuery = it },
            placeholder = { Text("Filtrar magias...", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        val matchingSpells = remember(spellSearchQuery) {
            DndSpellsDatabase.ALL_PRESET_SPELLS.filter {
                spellSearchQuery.isBlank() || it.name.contains(spellSearchQuery, ignoreCase = true)
            }.take(8)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            matchingSpells.forEach { sp ->
                val isChecked = selectedSpells.any { it.name == sp.name }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isChecked) SurfaceRaised else SurfaceBase)
                        .clickable { viewModel.toggleAscensionSelectedSpell(sp) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { viewModel.toggleAscensionSelectedSpell(sp) },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(sp.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("${sp.school} • Nível ${sp.level}", fontSize = 11.sp, color = SecondaryParchment)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Friendly Tip Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryContainerGold)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Você também pode cadastrar ou alterar magias e talentos a qualquer momento na Aba de Magias/Habilidades.",
                fontSize = 11.sp,
                color = OnSurface
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.setAscensionStep(5) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Ver Resumo da Ascensão", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun Step5Summary(viewModel: GrimoireViewModel, character: CharacterEntity) {
    val chosenClass by viewModel.ascensionClass.collectAsState()
    val hpMode by viewModel.ascensionHpMode.collectAsState()
    val choiceType by viewModel.ascensionChoiceType.collectAsState()
    val selectedFeat by viewModel.ascensionSelectedFeat.collectAsState()
    val selectedAttribute by viewModel.ascensionSelectedAttribute.collectAsState()
    val selectedSpells by viewModel.ascensionSelectedSpells.collectAsState()

    val (hitDie, _) = when {
        chosenClass.contains("Bárbaro", ignoreCase = true) -> Pair(12, "d12")
        chosenClass.contains("Guerreiro", ignoreCase = true) || chosenClass.contains("Paladino", ignoreCase = true) || chosenClass.contains("Patrulheiro", ignoreCase = true) -> Pair(10, "d10")
        chosenClass.contains("Mago", ignoreCase = true) || chosenClass.contains("Feiticeiro", ignoreCase = true) -> Pair(6, "d6")
        else -> Pair(8, "d8")
    }
    val conMod = (character.con - 10) / 2
    val gainedHp = (hitDie / 2 + 1 + conMod).coerceAtLeast(1)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PrimaryContainerGold)
                .border(2.dp, PrimaryGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(38.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Evolução Confirmada!",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold
        )

        Text(
            text = "${character.name} ascendeu na classe $chosenClass.",
            fontSize = 13.sp,
            color = OnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceRaised)
                .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("+$gainedHp PV Máximo (${character.maxHp} -> ${character.maxHp + gainedHp})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                if (choiceType == "ASI") {
                    Text("Aumento de Atributo: +2 em $selectedAttribute", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                } else {
                    Text("Talento Adquirido: ${selectedFeat?.name ?: "Nenhum"}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
            }

            if (selectedSpells.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("${selectedSpells.size} Nova(s) Magia(s) Adicionada(s)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.finishAscension() },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Concluir Ascensão", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
