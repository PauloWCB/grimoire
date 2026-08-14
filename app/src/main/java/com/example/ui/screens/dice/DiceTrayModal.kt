package com.example.ui.screens.dice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ThreeJsDiceView
import com.example.ui.screens.character.HexagonShape
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGold
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceTrayBottomSheet(
    viewModel: GrimoireViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val selectedDie by viewModel.selectedDie.collectAsState()
    val diceQuantity by viewModel.diceQuantity.collectAsState()
    val diceModifier by viewModel.diceModifier.collectAsState()
    val diceMode by viewModel.diceMode.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val lastResult by viewModel.lastRollResult.collectAsState()
    val recentRolls by viewModel.recentRolls.collectAsState()

    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            rotationAnim.animateTo(
                targetValue = rotationAnim.value + 360f * 3,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeDiceTray() },
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
                    Icon(Icons.Default.Casino, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BANDEJA DE DADOS",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                }

                IconButton(onClick = { viewModel.closeDiceTray() }) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dice Selector Chips
            val diceOptions = listOf("D4", "D6", "D8", "D10", "D12", "D20", "D100")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(diceOptions) { die ->
                    val isSelected = die == selectedDie
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryGold else SurfaceRaised)
                            .border(1.dp, if (isSelected) PrimaryGold else DividerColor, CircleShape)
                            .clickable { viewModel.setDieType(die) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = die,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF3D2E00) else OnSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Steppers & Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("QTD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceRaised)
                            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                    ) {
                        IconButton(onClick = { viewModel.setDiceQuantity(diceQuantity - 1) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "-1", tint = OnSurface, modifier = Modifier.size(12.dp))
                        }
                        Text("$diceQuantity", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface, modifier = Modifier.padding(horizontal = 6.dp))
                        IconButton(onClick = { viewModel.setDiceQuantity(diceQuantity + 1) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "+1", tint = OnSurface, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                // Modifier Stepper
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MODIFICADOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceRaised)
                            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                    ) {
                        IconButton(onClick = { viewModel.setDiceModifier(diceModifier - 1) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "-1", tint = OnSurface, modifier = Modifier.size(12.dp))
                        }
                        Text("${if (diceModifier >= 0) "+$diceModifier" else diceModifier}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, modifier = Modifier.padding(horizontal = 6.dp))
                        IconButton(onClick = { viewModel.setDiceModifier(diceModifier + 1) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "+1", tint = OnSurface, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                // Advantage Selector
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ROLAGEM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceRaised)
                            .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                    ) {
                        listOf("Desv", "Normal", "Vant").forEach { mode ->
                            val isSelected = diceMode == mode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) PrimaryContainerGold else Color.Transparent)
                                    .clickable { viewModel.setDiceMode(mode) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF3D2E00) else OnSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3D Dice Stage & Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceBase)
                    .border(1.dp, PrimaryGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // 3D WebGL / Three.js Dice Canvas
                ThreeJsDiceView(
                    modifier = Modifier.fillMaxSize(),
                    isRolling = isRolling,
                    dieType = selectedDie,
                    onTapRoll = { if (!isRolling) viewModel.rollDice() }
                )

                // Result Overlay Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    if (lastResult != null && !isRolling) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceRaised.copy(alpha = 0.9f))
                                .border(1.dp, PrimaryGold, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Resultado: ",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = "${lastResult!!.first}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${lastResult!!.second})",
                                    fontSize = 11.sp,
                                    color = OnSurface
                                )
                            }
                        }
                    } else if (isRolling) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryGold)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ROLANDO DADO 3D...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3D2E00)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Roll Button
            Button(
                onClick = { viewModel.rollDice() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                shape = RoundedCornerShape(8.dp),
                enabled = !isRolling,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Rolar Dados ($diceQuantity$selectedDie ${if (diceModifier >= 0) "+$diceModifier" else diceModifier})", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Roll History
            if (recentRolls.isNotEmpty()) {
                Text(
                    text = "HISTÓRICO RECENTE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(recentRolls) { roll ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceRaised)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(roll.label, fontSize = 12.sp, color = OnSurface)
                            Text(
                                text = "${roll.totalResult} (${roll.dieRoll} + ${roll.modifier})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGold
                            )
                        }
                    }
                }
            }
        }
    }
}
