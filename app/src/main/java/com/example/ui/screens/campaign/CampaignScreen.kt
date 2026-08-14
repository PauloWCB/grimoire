package com.example.ui.screens.campaign

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CampaignEntity
import com.example.data.model.CampaignMemberEntity
import com.example.data.model.CampaignNpcEntity
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
fun CampaignScreen(
    viewModel: GrimoireViewModel,
    onNavigateToCombat: () -> Unit
) {
    val activeCampaign by viewModel.activeCampaign.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val campaignSessions by viewModel.campaignSessions.collectAsState()
    val campaignNpcs by viewModel.campaignNpcs.collectAsState()
    val campaignMembers by viewModel.campaignMembers.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("História & Sessões", "Cheatsheet do Mestre (DMG)", "NPCs & Fichas", "Membros da Mesa")

    var showNewSessionDialog by remember { mutableStateOf(false) }
    var showDmActionsMenu by remember { mutableStateOf(false) }

    val campaign = activeCampaign ?: CampaignEntity(
        title = "Sombra de Ravenloft",
        synopsis = "Mistos de névoa e pesadelos cobrem o reino de Barovia. Sob o olhar atento do Conde Strahd, a expedição busca sobreviver.",
        dmName = "Archmage Valerius",
        isLive = true
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner & Campaign Meta
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(SurfaceContainer)
                        ) {
                            if (campaign.bannerUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = campaign.bannerUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.45f))
                            )

                            // Role & Live Badges
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryGold)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (currentRole == "DM") "VISÃO DO MESTRE" else "MODO JOGADOR",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3D2E00)
                                    )
                                }

                                if (campaign.isLive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DamageRed)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "● SESSÃO AO VIVO",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = campaign.title,
                                fontFamily = FontFamily.Serif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mestre: ${campaign.dmName} • Sistema: ${campaign.system}",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = campaign.synopsis,
                                fontSize = 13.sp,
                                color = OnSurface,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainer)
                                        .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Convite: ", fontSize = 11.sp, color = OnSurfaceVariant)
                                        Text(campaign.inviteCode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Button(
                                    onClick = onNavigateToCombat,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
                                ) {
                                    Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ir para o Combate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Tabs for Campaign sections
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceRaised,
                    contentColor = PrimaryGold,
                    edgePadding = 0.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) PrimaryGold else OnSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> { // História & Sessões
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SESSÕES E LOGS DA CAMPANHA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                            if (currentRole == "DM") {
                                OutlinedButton(
                                    onClick = { showNewSessionDialog = true },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Nova Sessão", fontSize = 11.sp, color = PrimaryGold)
                                }
                            }
                        }
                    }

                    items(campaignSessions) { session ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Sessão ${session.sessionNumber}: ${session.title}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                    Text(session.dateText, fontSize = 11.sp, color = PrimaryGold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(session.summary, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp)

                                if (session.publicNotes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SurfaceContainer)
                                            .padding(8.dp)
                                    ) {
                                        Text("📜 Notas Públicas: ${session.publicNotes}", fontSize = 12.sp, color = OnSurface)
                                    }
                                }

                                if (currentRole == "DM" && session.dmNotes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryGold.copy(alpha = 0.15f))
                                            .border(1.dp, PrimaryGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Notas Privadas do Mestre: ${session.dmNotes}", fontSize = 12.sp, color = PrimaryGold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> { // CHEATSHEET DO MESTRE (DMG REFERENCE)
                    item {
                        DmCheatSheetContent()
                    }
                }

                2 -> { // NPCs & Fichas
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("GERENCIADOR DE NPCS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                                if (currentRole == "DM") {
                                    Button(
                                        onClick = { viewModel.openNpcGenerator() },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Gerar NPC Aleatório", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    items(campaignNpcs) { npc ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (npc.isHostile) DamageRed.copy(alpha = 0.5f) else HealGreen.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openCharacterDetailNpc(npc) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
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
                                            Text(npc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                            Text("${npc.roleOrTitle} • ND ${npc.challengeRating}", fontSize = 12.sp, color = OnSurfaceVariant)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (npc.isHostile) DamageRed.copy(alpha = 0.2f) else HealGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (npc.isHostile) "Hostil" else "Aliado",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (npc.isHostile) DamageRed else HealGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("HP: ${npc.hp}/${npc.maxHp}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                    Text("CA: ${npc.ac}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                                    Text("Desloc: ${npc.speed}", fontSize = 12.sp, color = OnSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(npc.description, fontSize = 12.sp, color = OnSurfaceVariant)

                                if (currentRole == "DM") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.addNpcToCombat(npc)
                                            onNavigateToCombat()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold)
                                    ) {
                                        Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Lançar no Combate", fontSize = 12.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> { // Membros da Mesa
                    item {
                        Text("JOGADORES E PERSONAGENS VINCULADOS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                    }

                    items(campaignMembers) { member ->
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (member.avatarUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = member.avatarUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.matchParentSize()
                                            )
                                        } else {
                                            Text(member.characterName.take(1), fontWeight = FontWeight.Bold, color = PrimaryGold)
                                        }
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(member.characterName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                            Text("(${member.playerName})", fontSize = 12.sp, color = OnSurfaceVariant)
                                        }
                                        Text("${member.characterClass} • HP ${member.currentHp}/${member.maxHp}", fontSize = 12.sp, color = OnSurfaceVariant)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (member.role == "DM") PrimaryGold.copy(alpha = 0.2f) else SurfaceContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = member.role,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (member.role == "DM") PrimaryGold else OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button for DM Actions
        if (currentRole == "DM") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { showDmActionsMenu = true },
                    containerColor = PrimaryGold,
                    contentColor = Color(0xFF3D2E00)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Ações do Mestre")
                }
            }
        }

        // DM Actions Modal Sheet
        if (showDmActionsMenu) {
            AlertDialog(
                onDismissRequest = { showDmActionsMenu = false },
                containerColor = SurfaceRaised,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PAINEL DE AÇÕES DO MESTRE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Escolha a jornada que deseja realizar:", fontSize = 12.sp, color = OnSurfaceVariant)

                        OutlinedButton(
                            onClick = {
                                showDmActionsMenu = false
                                viewModel.openCreateCampaignItem()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f))
                        ) {
                            Text("📦 Cadastrar Item na Campanha", fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                showDmActionsMenu = false
                                viewModel.openNpcGenerator()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f))
                        ) {
                            Text("👤 Cadastrar NPC na Campanha", fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                showDmActionsMenu = false
                                viewModel.openGiveItemPlayer()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f))
                        ) {
                            Text("🎁 Adicionar Item ao Inventário do Jogador", fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                showDmActionsMenu = false
                                viewModel.openSendHandout()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f))
                        ) {
                            Text("📜 Enviar Nota / Handout / Pista de Mesa", fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDmActionsMenu = false }) {
                        Text("Cancelar", color = OnSurfaceVariant)
                    }
                }
            )
        }

        // Dialogs
        if (showNewSessionDialog) {
            NewSessionDialog(
                onDismiss = { showNewSessionDialog = false },
                onCreate = { title, summary, dmNotes, publicNotes ->
                    viewModel.addSessionToCampaign(title, summary, dmNotes, publicNotes)
                    showNewSessionDialog = false
                }
            )
        }
    }
}

@Composable
fun DmCheatSheetContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "GUIA RÁPIDO E REGRAS ESSENCIAIS DO MESTRE (DMG D&D 5E)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            letterSpacing = 1.sp
        )

        // Section 1: Condições (Conditions)
        CheatSheetAccordionCard(
            title = "📜 14 CONDIÇÕES (CONDITIONS)",
            subtitle = "Regras exatas de efeitos em combate (Cego, Paralisado, Exaustão, etc.)"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ConditionRuleItem("Cego (Blinded)", "Falha em testes visuais. Ataques contra o alvo têm Vantagem; ataques do alvo têm Desvantagem.")
                ConditionRuleItem("Enfeitiçado (Charmed)", "Não pode atacar quem o enfeitiçou. O conjurador tem Vantagem em testes sociais.")
                ConditionRuleItem("Surdo (Deafened)", "Falha em testes que exigem audição.")
                ConditionRuleItem("Caído (Prone)", "Movimento custa o dobro. Ataques a 1,5m contra o alvo têm Vantagem; a distância têm Desvantagem.")
                ConditionRuleItem("Agarrado (Grappled)", "Deslocamento reduzido a 0.")
                ConditionRuleItem("Incapacitado (Incapacitated)", "Não pode realizar Ações nem Reações.")
                ConditionRuleItem("Invisível (Invisible)", "Impossível de ser visto diretamente. Ataques têm Vantagem; ataques sofridos têm Desvantagem.")
                ConditionRuleItem("Paralisado (Paralyzed)", "Incapacitado, não fala nem se move. Falha em salvaguardas de FOR e DES. Ataques contra têm Vantagem e a 1,5m são Críticos.")
                ConditionRuleItem("Petrificado (Petrified)", "Peso x10. Incapacitado. Resistência a todo dano. Imune a venenos.")
                ConditionRuleItem("Envenenado (Poisoned)", "Desvantagem em jogadas de Ataque e Testes de Habilidade.")
                ConditionRuleItem("Restrito (Restrained)", "Deslocamento 0. Ataques contra têm Vantagem; ataques do alvo têm Desvantagem.")
                ConditionRuleItem("Atordoado (Stunned)", "Incapacitado, não se move. Falha em salvaguardas de FOR e DES. Ataques contra têm Vantagem.")
                ConditionRuleItem("Inconsciente (Unconscious)", "Incapacitado, cai no chão. Falha automatica em FOR e DES. Ataques a 1,5m são Críticos.")
                ConditionRuleItem("Exaustão (1-6)", "N1: Desvantagem Habilidade • N2: Deslocamento / 2 • N3: Desvantagem Ataques/Saves • N4: HP Máx / 2 • N5: Desloc 0 • N6: MORTE")
            }
        }

        // Section 2: Graus de Dificuldade (DC)
        CheatSheetAccordionCard(
            title = "🎯 GRAUS DE DIFICULDADE (DC / CD)",
            subtitle = "Tabela padrão do Livro do Mestre para testes de habilidade"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("● Muito Fácil: CD 5", fontSize = 13.sp, color = OnSurface)
                Text("● Fácil: CD 10", fontSize = 13.sp, color = OnSurface)
                Text("● Médio: CD 15 (Padrão para desafios moderados)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("● Difícil: CD 20", fontSize = 13.sp, color = OnSurface)
                Text("● Muito Difícil: CD 25", fontSize = 13.sp, color = OnSurface)
                Text("● Quase Impossível: CD 30", fontSize = 13.sp, color = DamageRed)
            }
        }

        // Section 3: Cobertura
        CheatSheetAccordionCard(
            title = "🛡️ REGRAS DE COBERTURA (COVER)",
            subtitle = "Bônus defensivos oferecidos por obstáculos do terreno"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("● Meia Cobertura (+2 CA / +2 Salvaguarda DES): Troncos baixos, móveis, criaturas.", fontSize = 13.sp, color = OnSurface)
                Text("● Três Quartos (+5 CA / +5 Salvaguarda DES): Portinholas, frestas de castelo.", fontSize = 13.sp, color = OnSurface)
                Text("● Cobertura Total: Impossível ser mirado diretamente por ataques ou magias.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
            }
        }

        // Section 4: Dano Improvisado e Queda
        CheatSheetAccordionCard(
            title = "💥 TABELA DE DANO IMPROVISADO & QUEDA (DMG p. 249)",
            subtitle = "Referência rápida de dano para armadilhas, desastres e quedas"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("● Dano de Queda: 1d6 de dano de contusão a cada 3m (10 pés) de queda (máximo de 20d6).", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("● Queimar mão em vela / Picada de inseto: 1d10 de dano", fontSize = 13.sp, color = OnSurface)
                Text("● Teto desabando / Atingido por raio: 2d10 a 4d10 de dano", fontSize = 13.sp, color = OnSurface)
                Text("● Esmagado por pedra / Acid elemental: 10d10 de dano", fontSize = 13.sp, color = OnSurface)
                Text("● Cair em vulcão de lava / Esmagado por Titã: 18d10 de dano", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DamageRed)
            }
        }

        // Section 5: Poções e Efeitos
        CheatSheetAccordionCard(
            title = "🧪 POÇÕES E EFEITOS MÁGICOS",
            subtitle = "Tipos de poções oficiais do LdM / DMG, raridade e efeitos"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("● Poção de Cura (Comum): Recupera 2d4 + 2 HP (50 PO)", fontSize = 13.sp, color = HealGreen)
                Text("● Poção de Cura Maior (Incomum): Recupera 4d4 + 4 HP (150 PO)", fontSize = 13.sp, color = HealGreen)
                Text("● Poção de Cura Superior (Rara): Recupera 8d4 + 8 HP (450 PO)", fontSize = 13.sp, color = HealGreen)
                Text("● Poção de Cura Suprema (Muito Rara): Recupera 10d4 + 20 HP (1.350 PO)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HealGreen)
                Text("● Poção de Invisibilidade (Rara): Invisível por 1 hora ou até atacar/conjurar (500 PO)", fontSize = 13.sp, color = PrimaryGold)
                Text("● Poção de Força do Gigante da Colina (Incomum): Define FOR para 21 por 1 hora (200 PO)", fontSize = 13.sp, color = OnSurface)
                Text("● Poção de Voo (Muito Rara): Deslocamento de Voo igual ao terrestre por 1 hora (500 PO)", fontSize = 13.sp, color = PrimaryGold)
                Text("● Poção de Respiração Aquática (Incomum): Respira debaixo d'água por 1 hora (100 PO)", fontSize = 13.sp, color = OnSurface)
                Text("● Poção de Heroísmo (Rara): +10 HP Temp + Efeito de Heroísmo por 1 hora (300 PO)", fontSize = 13.sp, color = HealGreen)
                Text("● Poção de Resistência (Incomum): Resistência a 1 tipo de dano específico por 1 hora (100 PO)", fontSize = 13.sp, color = OnSurface)
            }
        }

        // Section 6: Preços de Taverna
        CheatSheetAccordionCard(
            title = "🍻 PREÇOS DE TAVERNA & ACOMODAÇÕES (PHB/DMG)",
            subtitle = "Valores de mercado para refeições, bebidas e quartos"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("● Estalagem (Esquálida): 7 PC / dia (cama de palha e caldinho fino)", fontSize = 13.sp, color = OnSurface)
                Text("● Estalagem (Pobre): 1 PP / dia (quarto coletivo simples)", fontSize = 13.sp, color = OnSurface)
                Text("● Estalagem (Modesta): 5 PP / dia (quarto limpo com lençol)", fontSize = 13.sp, color = OnSurface)
                Text("● Estalagem (Confortável): 2 PO / dia (quarto privativo e boa refeição)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("● Estalagem (Rica): 4 PO / dia (serviço de banho quente e banquete)", fontSize = 13.sp, color = PrimaryGold)
                Text("● Caneca de Cerveja de Trigo: 4 PC", fontSize = 13.sp, color = OnSurface)
                Text("● Jarro de Vinho Comum: 2 PP • Vinho Fino de Coleção: 10 PO", fontSize = 13.sp, color = OnSurface)
            }
        }

        // Section 7: Testes de Morte
        CheatSheetAccordionCard(
            title = "💀 TESTES DE MORTE & ESTABILIZAÇÃO",
            subtitle = "Regras quando um personagem cai a 0 Pontos de Vida"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("● Rolar d20 sem modificadores: 10+ = Sucesso • <10 = Falha", fontSize = 13.sp, color = OnSurface)
                Text("● Rolar 1 = 2 Falhas automáticas • Rolar 20 = Recupera 1 HP e acorda", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("● 3 Sucessos = Estabiliza (permanece com 0 HP) • 3 Falhas = Morte permanente", fontSize = 13.sp, color = DamageRed)
                Text("● Estabilizar com Kit Médico: Teste de Sabedoria (Medicina) CD 10", fontSize = 13.sp, color = OnSurface)
            }
        }
    }
}

@Composable
fun CheatSheetAccordionCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (expanded) PrimaryGold else DividerColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    Text(subtitle, fontSize = 11.sp, color = OnSurfaceVariant)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = PrimaryGold
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    Spacer(modifier = Modifier.height(10.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun ConditionRuleItem(name: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("● $name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        Text(desc, fontSize = 12.sp, color = OnSurfaceVariant, lineHeight = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun NewSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var dmNotes by remember { mutableStateOf("") }
    var publicNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nova Sessão", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título da Sessão (Ex: O Templo Oculto)") })
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Resumo do que Aconteceu") }, minLines = 2)
                OutlinedTextField(value = publicNotes, onValueChange = { publicNotes = it }, label = { Text("Notas Públicas para Jogadores") })
                OutlinedTextField(value = dmNotes, onValueChange = { dmNotes = it }, label = { Text("Notas Secretas do Mestre (Privado)") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title, summary, dmNotes, publicNotes) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Registrar Sessão")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun NpcGenDialog(
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gerar Ficha de NPC Aleatória", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Escolha o perfil de NPC que deseja gerar com regras do D&D 5e:", fontSize = 12.sp, color = OnSurfaceVariant)

                Button(
                    onClick = { onGenerate("citizen") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceRaised)
                ) {
                    Text("👨‍🌾 Cidadão / Habitante da Cidade (Nvl 1-5)", color = OnSurface)
                }

                Button(
                    onClick = { onGenerate("combat") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceRaised)
                ) {
                    Text("⚔️ Inimigo / Mercenário de Dificuldade Média", color = OnSurface)
                }

                Button(
                    onClick = { onGenerate("boss") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceRaised)
                ) {
                    Text("🐉 Chefe Lendário de Calabouço", color = OnSurface)
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun GiveItemDialog(
    members: List<CampaignMemberEntity>,
    onDismiss: () -> Unit,
    onGive: (String, String, Int, Int, String) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()?.characterName ?: "Kaelen") }
    var itemName by remember { mutableStateOf("Poção de Cura Maior") }
    var qtyStr by remember { mutableStateOf("1") }
    var gpStr by remember { mutableStateOf("150") }
    var properties by remember { mutableStateOf("Recupera 4d4+4 HP") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Injetar Item no Inventário do Jogador", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Selecione o Personagem:", fontSize = 12.sp, color = OnSurfaceVariant)
                OutlinedTextField(value = selectedMember, onValueChange = { selectedMember = it }, label = { Text("Nome do Personagem") })
                OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Nome do Item / Recompensa") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = qtyStr, onValueChange = { qtyStr = it }, label = { Text("Qtd") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = gpStr, onValueChange = { gpStr = it }, label = { Text("Valor PO") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = properties, onValueChange = { properties = it }, label = { Text("Propriedades / Descrição") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyStr.toIntOrNull() ?: 1
                    val gp = gpStr.toIntOrNull() ?: 0
                    onGive(selectedMember, itemName, qty, gp, properties)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Enviar ao Jogador")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun SendHandoutDialog(
    onDismiss: () -> Unit,
    onSend: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imgUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enviar Nota / Handout para os Jogadores", color = PrimaryGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título da Pista / Documento") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Texto da Nota / Transcrição") }, minLines = 3)
                OutlinedTextField(value = imgUrl, onValueChange = { imgUrl = it }, label = { Text("URL da Imagem / Mapa (Opcional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(title, content, imgUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Publicar Handout")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
