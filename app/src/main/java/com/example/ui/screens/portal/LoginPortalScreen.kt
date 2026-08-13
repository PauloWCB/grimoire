package com.example.ui.screens.portal

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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

@Composable
fun LoginPortalScreen(
    viewModel: GrimoireViewModel,
    onEnterApp: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val allCampaigns by viewModel.allCampaigns.collectAsState()
    val activeCampaignId by viewModel.activeCampaignId.collectAsState()
    val allCharacters by viewModel.allCharacters.collectAsState()
    val selectedCharacterId by viewModel.selectedCharacterId.collectAsState()

    var selectedMode by remember { mutableStateOf(currentRole) } // "PLAYER" or "DM"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / App Branding
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryGold)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF3D2E00),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "GRIMOIRE VTT",
                    fontFamily = FontFamily.Serif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )

                Text(
                    text = "Portal de Login e Seleção de Mesa (D&D 5e)",
                    fontSize = 13.sp,
                    color = OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Role Selector Cards: JOGAR vs MESTRAR
            item {
                Text(
                    text = "COMO VOCÊ DESEJA ENTRAR HOJE?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // JOGAR (Player)
                    Card(
                        onClick = {
                            selectedMode = "PLAYER"
                            viewModel.setCurrentRole("PLAYER")
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMode == "PLAYER") SurfaceRaised else SurfaceContainer
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selectedMode == "PLAYER") 2.dp else 1.dp,
                            color = if (selectedMode == "PLAYER") HealGreen else DividerColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsKabaddi,
                                contentDescription = null,
                                tint = if (selectedMode == "PLAYER") HealGreen else OnSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "JOGAR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMode == "PLAYER") HealGreen else OnSurface
                            )
                            Text(
                                text = "Visão do Jogador",
                                fontSize = 11.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    // MESTRAR (DM)
                    Card(
                        onClick = {
                            selectedMode = "DM"
                            viewModel.setCurrentRole("DM")
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMode == "DM") SurfaceRaised else SurfaceContainer
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selectedMode == "DM") 2.dp else 1.dp,
                            color = if (selectedMode == "DM") PrimaryGold else DividerColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = if (selectedMode == "DM") PrimaryGold else OnSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MESTRAR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMode == "DM") PrimaryGold else OnSurface
                            )
                            Text(
                                text = "Visão do Mestre",
                                fontSize = 11.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Mode Specific List
            if (selectedMode == "PLAYER") {
                item {
                    Text(
                        text = "SELECIONE SEU PERSONAGEM DE JOGADOR:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HealGreen,
                        letterSpacing = 1.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(allCharacters) { charEntity ->
                    val isSelected = selectedCharacterId == charEntity.id
                    Card(
                        onClick = {
                            viewModel.switchCharacter(charEntity.id)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) HealGreen.copy(alpha = 0.15f) else SurfaceContainer
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) HealGreen else DividerColor
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
                                        .background(SurfaceRaised),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (charEntity.avatarUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = charEntity.avatarUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    } else {
                                        Text(charEntity.name.take(1), fontWeight = FontWeight.Bold, color = PrimaryGold)
                                    }
                                }

                                Column {
                                    Text(
                                        text = charEntity.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) HealGreen else OnSurface
                                    )
                                    Text(
                                        text = "${charEntity.characterClass} Nvl ${charEntity.level} • HP ${charEntity.currentHp}/${charEntity.maxHp}",
                                        fontSize = 12.sp,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealGreen)
                            }
                        }
                    }
                }
            } else { // "DM"
                item {
                    Text(
                        text = "MINHAS CAMPANHAS COMO MESTRE:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(allCampaigns) { campaign ->
                    val isSelected = activeCampaignId == campaign.id
                    Card(
                        onClick = {
                            viewModel.selectActiveCampaign(campaign.id)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryGold.copy(alpha = 0.15f) else SurfaceContainer
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PrimaryGold else DividerColor
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
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceRaised)
                                ) {
                                    if (campaign.bannerUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = campaign.bannerUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = campaign.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PrimaryGold else OnSurface
                                    )
                                    Text(
                                        text = "Mestre: ${campaign.dmName} • Convite: ${campaign.inviteCode}",
                                        fontSize = 12.sp,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGold)
                            }
                        }
                    }
                }
            }

            // Primary Action Button
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.setCurrentRole(selectedMode)
                        viewModel.closePortal()
                        onEnterApp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMode == "PLAYER") HealGreen else PrimaryGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = if (selectedMode == "PLAYER") "ENTRAR COMO JOGADOR" else "ENTRAR COMO MESTRE DA MESA",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
