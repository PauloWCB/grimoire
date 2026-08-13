package com.example.ui.screens.rests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterEntity
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.HealGreen
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortRestBottomSheet(
    viewModel: GrimoireViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()

    var diceToSpend by remember { mutableIntStateOf(1) }
    val maxAvailableDice = 3
    val projectedHp = (character.currentHp + (diceToSpend * 7)).coerceAtMost(character.maxHp)

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeShortRestModal() },
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = null,
                        tint = PrimaryGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Descanso Curto",
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                }
                IconButton(onClick = { viewModel.closeShortRestModal() }) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gaste Dados de Vida para recuperar pontos de vida perdidos durante a aventura.",
                fontSize = 13.sp,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hit Dice Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dados de Vida (d10)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text("Disponível: $maxAvailableDice/5", fontSize = 12.sp, color = OnSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(5) { idx ->
                            val isAvailable = idx < maxAvailableDice
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isAvailable) PrimaryContainerGold else SurfaceBase)
                                    .border(1.dp, if (isAvailable) PrimaryGold else DividerColor, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("d10", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isAvailable) Color(0xFF3D2E00) else OnSurfaceVariant)
                            }
                        }
                    }

                    // Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceBase)
                            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                    ) {
                        IconButton(onClick = { if (diceToSpend > 1) diceToSpend-- }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "-1", tint = OnSurface, modifier = Modifier.size(14.dp))
                        }
                        Text("$diceToSpend", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { if (diceToSpend < maxAvailableDice) diceToSpend++ }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "+1", tint = OnSurface, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.openDiceTray("D10", character.con, "Dado de Vida (Descanso Curto)")
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null, tint = PrimaryGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rolar Dados de Vida", fontSize = 14.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Diff Preview
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PREVISÃO DE PV",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${character.currentHp}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("$projectedHp", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.closeShortRestModal() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Cancelar", color = SecondaryParchment)
                }

                Button(
                    onClick = {
                        viewModel.performShortRest()
                        viewModel.closeShortRestModal()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Confirmar Descanso", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongRestBottomSheet(
    viewModel: GrimoireViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()

    var restoreHpChecked by remember { mutableStateOf(true) }
    var restoreSpellsChecked by remember { mutableStateOf(true) }
    var restoreHitDiceChecked by remember { mutableStateOf(true) }
    var clearConditionsChecked by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeLongRestModal() },
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceRaised)
                    .border(1.dp, PrimaryGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bedtime, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Descanso Longo",
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGold
            )

            Text(
                text = "Revigora corpo e mente. Analise as restaurações abaixo.",
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Restoration Diff List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp)
            ) {
                // HP Restoration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = restoreHpChecked,
                            onCheckedChange = { restoreHpChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryGold, checkmarkColor = Color(0xFF3D2E00))
                        )
                        Text("Pontos de Vida", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${character.currentHp}", fontSize = 14.sp, color = DamageRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${character.maxHp}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                }

                // Spells Restoration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = restoreSpellsChecked,
                            onCheckedChange = { restoreSpellsChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryGold, checkmarkColor = Color(0xFF3D2E00))
                        )
                        Column {
                            Text("Espaços de Magia", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                            Text("Todos Restaurados", fontSize = 12.sp, color = SecondaryParchment)
                        }
                    }
                }

                // Hit Dice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = restoreHitDiceChecked,
                            onCheckedChange = { restoreHitDiceChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryGold, checkmarkColor = Color(0xFF3D2E00))
                        )
                        Text("Dados de Vida", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("2", fontSize = 14.sp, color = OnSurfaceVariant)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("4", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                }

                // Conditions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = clearConditionsChecked,
                            onCheckedChange = { clearConditionsChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryGold, checkmarkColor = Color(0xFF3D2E00))
                        )
                        Column {
                            Text("Condições", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Exausto (1) ", fontSize = 12.sp, color = SecondaryParchment)
                                Text("removido", fontSize = 12.sp, textDecoration = TextDecoration.LineThrough, color = OnSurfaceVariant.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.closeLongRestModal() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Cancelar", color = SecondaryParchment)
                }

                Button(
                    onClick = {
                        viewModel.performLongRest()
                        viewModel.closeLongRestModal()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Confirmar Descanso", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
