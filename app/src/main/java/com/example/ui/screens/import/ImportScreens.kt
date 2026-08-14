package com.example.ui.screens.import

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CharacterEntity
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGold
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel
import com.example.ui.viewmodel.ImportStep

@Composable
fun ImportFlowContainer(
    viewModel: GrimoireViewModel,
    onFinishImport: () -> Unit
) {
    val step by viewModel.importStep.collectAsState()
    val character by viewModel.character.collectAsState()

    when (step) {
        ImportStep.IDLE -> ImportInitialScreen(
            onChoosePdf = { viewModel.startPdfImport() },
            onCreateBlank = {
                viewModel.proceedToReview()
            }
        )
        ImportStep.READING -> ReadingScrollScreen(
            viewModel = viewModel,
            onCancel = { viewModel.cancelImport() }
        )
        ImportStep.COMPLETED -> ImportCompletedScreen(
            character = character,
            onReviewData = { viewModel.proceedToReview() }
        )
        ImportStep.REVIEWING -> DataReviewScreen(
            character = character,
            onCancel = { viewModel.cancelImport() },
            onSave = { updatedChar ->
                viewModel.saveImportedCharacter(updatedChar)
                onFinishImport()
            },
            onUpdateWisdom = { newWis -> viewModel.updateWisdomScore(newWis) }
        )
        ImportStep.FINISHED -> {
            onFinishImport()
        }
    }
}

// SCREEN 1: Importe sua ficha
@Composable
fun ImportInitialScreen(
    onChoosePdf: () -> Unit,
    onCreateBlank: () -> Unit
) {
    var showHowItWorks by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(BackgroundDark)
                .drawBehind {
                    drawLine(
                        color = DividerColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCreateBlank) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = PrimaryGold
                )
            }
            Text(
                text = "Grimoire",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                color = PrimaryGold
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        // Main Drop Zone
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 180.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val stroke = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow)
                    .drawBehind {
                        drawRoundRect(
                            color = OutlineVariant,
                            style = Stroke(width = 2.dp.toPx(), pathEffect = stroke)
                        )
                    }
                    .clickable { onChoosePdf() }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Arcane Corners
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val cornerSize = 20.dp.toPx()
                            // Top-Left
                            drawLine(PrimaryGold, Offset(0f, 0f), Offset(cornerSize, 0f), 2.dp.toPx())
                            drawLine(PrimaryGold, Offset(0f, 0f), Offset(0f, cornerSize), 2.dp.toPx())
                            // Top-Right
                            drawLine(PrimaryGold, Offset(size.width - cornerSize, 0f), Offset(size.width, 0f), 2.dp.toPx())
                            drawLine(PrimaryGold, Offset(size.width, 0f), Offset(size.width, cornerSize), 2.dp.toPx())
                            // Bottom-Left
                            drawLine(PrimaryGold, Offset(0f, size.height - cornerSize), Offset(0f, size.height), 2.dp.toPx())
                            drawLine(PrimaryGold, Offset(0f, size.height), Offset(cornerSize, size.height), 2.dp.toPx())
                            // Bottom-Right
                            drawLine(PrimaryGold, Offset(size.width - cornerSize, size.height), Offset(size.width, size.height), 2.dp.toPx())
                            drawLine(PrimaryGold, Offset(size.width, size.height - cornerSize), Offset(size.width, size.height), 2.dp.toPx())
                        }
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Upload",
                        tint = PrimaryGold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Importe sua ficha",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Faça o upload do seu PDF para carregar automaticamente seus dados.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }
            }
        }

        // Bottom Actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onChoosePdf,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGold,
                    contentColor = OnSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Escolher PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF3D2E00)
                )
            }

            OutlinedButton(
                onClick = onCreateBlank,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGold),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Criar personagem do zero",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            TextButton(onClick = { showHowItWorks = true }) {
                Text(
                    text = "Como funciona?",
                    color = PrimaryGold,
                    fontSize = 14.sp
                )
            }
        }
    }

    if (showHowItWorks) {
        AlertDialog(
            onDismissRequest = { showHowItWorks = false },
            containerColor = SurfaceContainer,
            title = {
                Text(
                    text = "Como funciona a importação?",
                    fontFamily = FontFamily.Serif,
                    color = PrimaryGold
                )
            },
            text = {
                Text(
                    text = "O Grimoire analisa sua ficha de personagem em formato PDF (D&D 5e ou TTRPG) e extrai automaticamente seus atributos, magias, itens e dados de combate para preencher seu grimório digital em segundos.",
                    color = OnSurface,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showHowItWorks = false }) {
                    Text("Entendido", color = PrimaryGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// SCREEN 2: Lendo pergaminho...
@Composable
fun ReadingScrollScreen(
    viewModel: GrimoireViewModel,
    onCancel: () -> Unit
) {
    val step1Done by viewModel.importProgressStep1.collectAsState()
    val step2Done by viewModel.importProgressStep2.collectAsState()
    val step3Done by viewModel.importProgressStep3.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(64.dp)
                .background(BackgroundDark)
                .drawBehind {
                    drawLine(
                        color = DividerColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancelar",
                    tint = PrimaryGold
                )
            }
            Text(
                text = "Grimoire",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                color = PrimaryGold
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        // Progress Card
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainer)
                .border(1.dp, DividerColor, RoundedCornerShape(12.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Headline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = PrimaryGold.copy(alpha = pulseAlpha),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lendo pergaminho...",
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    color = OnSurface
                )
            }

            // Steps
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Step 1: Atributos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (step1Done) PrimaryGold.copy(alpha = 0.2f) else SurfaceContainerLow)
                            .border(1.dp, if (step1Done) PrimaryGold else OutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step1Done) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(18.dp))
                        } else {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PrimaryGold, strokeWidth = 2.dp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Atributos", fontSize = 16.sp, color = OnSurface)
                }

                // Step 2: Magias
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (step2Done) PrimaryGold.copy(alpha = 0.2f) else SurfaceContainerLow)
                            .border(1.dp, if (step2Done) PrimaryGold else OutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step2Done) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(18.dp))
                        } else if (step1Done) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PrimaryGold, strokeWidth = 2.dp)
                        } else {
                            Box(modifier = Modifier.size(6.dp).background(OutlineVariant, CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Magias", fontSize = 16.sp, color = if (step1Done) OnSurface else OnSurfaceVariant)
                }

                // Step 3: Inventário
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (step3Done) PrimaryGold.copy(alpha = 0.2f) else SurfaceContainerLow)
                            .border(1.dp, if (step3Done) PrimaryGold else OutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step3Done) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(18.dp))
                        } else if (step2Done) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PrimaryGold, strokeWidth = 2.dp)
                        } else {
                            Box(modifier = Modifier.size(6.dp).background(OutlineVariant, CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Inventário", fontSize = 16.sp, color = if (step2Done) OnSurface else OnSurfaceVariant)
                }
            }

            // Indeterminate Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(SurfaceContainerLow)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(if (step3Done) 1f else if (step2Done) 0.7f else if (step1Done) 0.35f else 0.15f)
                        .background(PrimaryGold)
                )
            }
        }

        // Cancel button
        TextButton(
            onClick = onCancel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("Cancelar", color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// SCREEN 3: Importação concluída!
@Composable
fun ImportCompletedScreen(
    character: CharacterEntity?,
    onReviewData: () -> Unit
) {
    val name = character?.name ?: "Thalric Ironfoot"
    val classTitle = "GUERREIRO NÍVEL ${character?.level ?: 5}"
    val avatarUrl = character?.avatarUrl ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Importação concluída!",
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Character Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.padding(bottom = 16.dp)) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .border(2.dp, PrimaryGold, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BackgroundDark)
                                .border(1.dp, PrimaryGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = classTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Text(
                text = "Todos os dados foram extraídos com sucesso.",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onReviewData,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = OnSurface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Revisar dados", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp, color = Color(0xFF3D2E00))
            }
        }
    }
}

// SCREEN 4: Data Review Screen (Thalric Ironfoot review)
@Composable
fun DataReviewScreen(
    character: CharacterEntity?,
    onCancel: () -> Unit,
    onSave: (CharacterEntity) -> Unit,
    onUpdateWisdom: (Int) -> Unit
) {
    var currentChar by remember(character) {
        mutableStateOf(character ?: CharacterEntity())
    }

    var showWisdomDialog by remember { mutableStateOf(false) }
    var wisdomInputText by remember { mutableStateOf("10") }

    var sectionIdentidadeOpen by remember { mutableStateOf(true) }
    var sectionAtributosOpen by remember { mutableStateOf(true) }
    var sectionCombateOpen by remember { mutableStateOf(false) }
    var sectionPericiasOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0B0A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF0E0B0A))
                    .drawBehind {
                        drawLine(
                            color = DividerColor,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentChar.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = PrimaryGold.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(40.dp))
            }

            // Confidence Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryContainerGold)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF3D2E00), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3 campos precisam da sua atenção",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3D2E00)
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // SECTION: Identidade
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sectionIdentidadeOpen = !sectionIdentidadeOpen }
                            .drawBehind {
                                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                            }
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Identidade", fontFamily = FontFamily.Serif, fontSize = 22.sp, color = PrimaryGold)
                        Icon(
                            imageVector = if (sectionIdentidadeOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }

                    if (sectionIdentidadeOpen) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceRaised)
                                .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                        ) {
                            // Classe
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Classe", color = OutlineGold, fontSize = 14.sp)
                                Text("${currentChar.characterClass} ${currentChar.level}", color = SecondaryParchment, fontWeight = FontWeight.Medium)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))

                            // Raça (Warning)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainerLow)
                                    .drawBehind {
                                        drawLine(PrimaryContainerGold, Offset(0f, 0f), Offset(0f, size.height), 4.dp.toPx())
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Raça", color = OutlineGold, fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(currentChar.race, color = SecondaryParchment, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = PrimaryContainerGold, modifier = Modifier.size(16.dp))
                                }
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))

                            // Antecedente
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Antecedente", color = OutlineGold, fontSize = 14.sp)
                                Text(currentChar.background, color = SecondaryParchment, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // SECTION: Atributos
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sectionAtributosOpen = !sectionAtributosOpen }
                            .drawBehind {
                                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                            }
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Atributos", fontFamily = FontFamily.Serif, fontSize = 22.sp, color = PrimaryGold)
                        Icon(
                            imageVector = if (sectionAtributosOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }

                    if (sectionAtributosOpen) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // FOR
                            AttributeReviewCard(label = "FOR", score = currentChar.str, mod = "+4", modifier = Modifier.weight(1f))
                            // DES (Warning)
                            AttributeReviewCard(label = "DES", score = currentChar.dex, mod = "+2", hasWarning = true, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // CON
                            AttributeReviewCard(label = "CON", score = currentChar.con, mod = "+3", modifier = Modifier.weight(1f))
                            // INT
                            AttributeReviewCard(label = "INT", score = currentChar.intScore, mod = "+0", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // SAB (Missing/Fillable)
                            AttributeMissingCard(
                                label = "SAB",
                                currentWis = currentChar.wis,
                                onFill = { showWisdomDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            // CAR
                            AttributeReviewCard(label = "CAR", score = currentChar.cha, mod = "+1", modifier = Modifier.weight(1f))
                        }
                    }
                }

                // SECTION: Combate
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sectionCombateOpen = !sectionCombateOpen }
                            .drawBehind {
                                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                            }
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Combate", fontFamily = FontFamily.Serif, fontSize = 22.sp, color = PrimaryGold)
                        Icon(
                            imageVector = if (sectionCombateOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }
                    if (sectionCombateOpen) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Classe de Armadura: ${currentChar.ac} | Iniciativa: +${currentChar.initiative} | Deslocamento: ${currentChar.speed}", color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                }

                // SECTION: Perícias
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sectionPericiasOpen = !sectionPericiasOpen }
                            .drawBehind {
                                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                            }
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Perícias", fontFamily = FontFamily.Serif, fontSize = 22.sp, color = PrimaryGold)
                        Icon(
                            imageVector = if (sectionPericiasOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }
                    if (sectionPericiasOpen) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Atletismo (+6), Arcanismo (+7), História (+7), Percepção (+2)", color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        }

        // Bottom Sticky Action Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = SurfaceRaised,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .drawBehind {
                        drawLine(DividerColor, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "${currentChar.reviewedFieldsCount} campos revisados de ${currentChar.totalFieldsToReview}",
                    fontSize = 14.sp,
                    color = OutlineGold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancelar", color = OnSurface, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { onSave(currentChar) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainerGold,
                            contentColor = Color(0xFF3D2E00)
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Salvar personagem", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showWisdomDialog) {
        AlertDialog(
            onDismissRequest = { showWisdomDialog = false },
            containerColor = SurfaceContainer,
            title = {
                Text("Preencher Sabedoria (SAB)", fontFamily = FontFamily.Serif, color = PrimaryGold)
            },
            text = {
                Column {
                    Text("Insira o valor do atributo de Sabedoria para o personagem:", color = OnSurface, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = wisdomInputText,
                        onValueChange = { wisdomInputText = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGold,
                            unfocusedBorderColor = DividerColor,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newWis = wisdomInputText.toIntOrNull() ?: 10
                        currentChar = currentChar.copy(wis = newWis)
                        onUpdateWisdom(newWis)
                        showWisdomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = OnSurface)
                ) {
                    Text("Confirmar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWisdomDialog = false }) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun AttributeReviewCard(
    label: String,
    score: Int,
    mod: String,
    hasWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceRaised)
            .border(
                1.dp,
                if (hasWarning) PrimaryContainerGold else DividerColor,
                RoundedCornerShape(4.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (hasWarning) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = PrimaryContainerGold,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OutlineGold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$score", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceContainer)
                    .border(1.dp, DividerColor, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text(mod, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryParchment)
            }
        }
    }
}

@Composable
fun AttributeMissingCard(
    label: String,
    currentWis: Int,
    onFill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFilled = currentWis != 10 && currentWis > 0
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceRaised)
            .drawBehind {
                val stroke = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                drawRoundRect(
                    color = PrimaryContainerGold,
                    style = Stroke(width = 1.dp.toPx(), pathEffect = stroke)
                )
                drawLine(
                    color = PrimaryContainerGold,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 4.dp.toPx()
                )
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OutlineGold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isFilled) "$currentWis" else "--",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFilled) PrimaryGold else OnSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onFill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainerGold.copy(alpha = 0.2f),
                    contentColor = PrimaryContainerGold
                ),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryContainerGold),
                modifier = Modifier.height(28.dp)
            ) {
                Text(if (isFilled) "Editar" else "Preencher", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
