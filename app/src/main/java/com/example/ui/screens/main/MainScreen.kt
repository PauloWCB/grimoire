package com.example.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.theme.HealGreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.components.RoleSwitcherBottomSheet
import com.example.ui.screens.ascension.AscensionWizardModal
import com.example.ui.screens.campaign.CampaignScreen
import com.example.ui.screens.character.CharacterDetailScreen
import com.example.ui.screens.character.CharacterScreen
import com.example.ui.screens.combat.CombatScreen
import com.example.ui.screens.dice.DiceTrayBottomSheet
import com.example.ui.screens.dm.CreateCampaignItemScreen
import com.example.ui.screens.dm.DmCharactersScreen
import com.example.ui.screens.dm.DmSpellsScreen
import com.example.ui.screens.dm.GiveItemScreen
import com.example.ui.screens.dm.NpcGeneratorScreen
import com.example.ui.screens.dm.SendHandoutScreen
import com.example.ui.screens.import.ImportFlowContainer
import com.example.ui.screens.inventory.InventoryScreen
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.portal.LoginPortalScreen
import com.example.ui.screens.rests.LongRestBottomSheet
import com.example.ui.screens.rests.ShortRestBottomSheet
import com.example.ui.screens.spells.SpellsScreen
import com.example.ui.settings.AppStrings
import com.example.ui.settings.SettingsModal
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.OverlayScreen
import com.example.ui.viewmodel.GrimoireViewModel
import com.example.ui.viewmodel.ImportStep

data class NavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: GrimoireViewModel
) {
    val isPortalActive by viewModel.isPortalActive.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState() // "DM" or "PLAYER"

    // If initial portal is open, show LoginPortalScreen
    if (isPortalActive) {
        LoginPortalScreen(
            viewModel = viewModel,
            onEnterApp = {}
        )
        return
    }

    var selectedTab by remember(currentRole) { mutableIntStateOf(0) }

    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()
    val showDiceTray by viewModel.showDiceTray.collectAsState()
    val importStep by viewModel.importStep.collectAsState()
    val showRoleSwitcherSheet by viewModel.showRoleSwitcherSheet.collectAsState()

    val showShortRestModal by viewModel.showShortRestModal.collectAsState()
    val showLongRestModal by viewModel.showLongRestModal.collectAsState()
    val showAscensionFlow by viewModel.showAscensionFlow.collectAsState()
    val showSettingsModal by viewModel.showSettingsModal.collectAsState()
    val activeOverlayScreen by viewModel.activeOverlayScreen.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isPt = appLanguage == "pt"

    val activeCampaign by viewModel.activeCampaign.collectAsState()
    val campaignMembers by viewModel.campaignMembers.collectAsState()

    // If an overlay screen (full screen flow) is active, render it over the main layout
    when (val overlay = activeOverlayScreen) {
        is OverlayScreen.NpcGenerator -> {
            NpcGeneratorScreen(
                campaignId = activeCampaign?.id ?: 1L,
                onClose = { viewModel.closeOverlayScreen() },
                onConfirmSave = { npc ->
                    viewModel.saveCustomNpc(npc)
                    viewModel.closeOverlayScreen()
                }
            )
            return
        }
        is OverlayScreen.CharacterDetailPc -> {
            CharacterDetailScreen(
                pc = overlay.character,
                onClose = { viewModel.closeOverlayScreen() }
            )
            return
        }
        is OverlayScreen.CharacterDetailNpc -> {
            CharacterDetailScreen(
                npc = overlay.npc,
                onClose = { viewModel.closeOverlayScreen() }
            )
            return
        }
        is OverlayScreen.CreateCampaignItem -> {
            CreateCampaignItemScreen(
                onClose = { viewModel.closeOverlayScreen() },
                onConfirmCreate = { name, category, quantity, valueGp, description, imageUrl ->
                    viewModel.dmGiveItemToPlayer("Grupo", name, quantity, valueGp, description)
                    viewModel.closeOverlayScreen()
                }
            )
            return
        }
        is OverlayScreen.GiveItemPlayer -> {
            GiveItemScreen(
                members = campaignMembers,
                onClose = { viewModel.closeOverlayScreen() },
                onConfirmGive = { recipient, item, qty, gpVal, prop ->
                    viewModel.dmGiveItemToPlayer(recipient, item, qty, gpVal, prop)
                    viewModel.closeOverlayScreen()
                }
            )
            return
        }
        is OverlayScreen.SendHandout -> {
            SendHandoutScreen(
                onClose = { viewModel.closeOverlayScreen() },
                onConfirmSend = { title, body, imageUrl ->
                    viewModel.dmSendHandoutNote(title, body, imageUrl)
                    viewModel.closeOverlayScreen()
                }
            )
            return
        }
        OverlayScreen.None -> { /* Continue with standard scaffold */ }
    }

    // Define navigation tabs according to active role
    val navItems = if (currentRole == "DM") {
        listOf(
            NavItem("Campanha", Icons.Default.MenuBook),
            NavItem("Personagens", Icons.Default.Group),
            NavItem("Spells", Icons.Default.AutoAwesome),
            NavItem("Combate", Icons.Default.SportsKabaddi)
        )
    } else {
        listOf(
            NavItem(AppStrings.character(isPt), Icons.Default.Person),
            NavItem(AppStrings.spells(isPt), Icons.Default.AutoAwesome),
            NavItem(AppStrings.inventory(isPt), Icons.Default.Inventory),
            NavItem(AppStrings.combat(isPt), Icons.Default.SportsKabaddi),
            NavItem(AppStrings.notes(isPt), Icons.Default.Description)
        )
    }

    // Ensure selectedTab stays within valid bounds when switching roles
    val safeTab = selectedTab.coerceIn(0, (navItems.size - 1).coerceAtLeast(0))

    // If import flow is active, render full-screen import container
    if (importStep != ImportStep.IDLE && importStep != ImportStep.FINISHED) {
        ImportFlowContainer(
            viewModel = viewModel,
            onFinishImport = { selectedTab = 0 }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.openRoleSwitcherSheet() }
                        ) {
                            AsyncImage(
                                model = character.avatarUrl,
                                contentDescription = character.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, PrimaryGold, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Grimoire",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = PrimaryGold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (currentRole == "DM") PrimaryGold else HealGreen)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (currentRole == "DM") "MESTRE" else "JOGADOR",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                                Text(
                                    text = if (currentRole == "DM") "Visão Geral da Mesa" else character.name,
                                    fontSize = 11.sp,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Portal Login Switcher Button
                    IconButton(onClick = { viewModel.openPortal() }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Portal de Login",
                            tint = PrimaryGold
                        )
                    }

                    // Level Up / Ascensão Action
                    IconButton(onClick = { viewModel.startAscensionFlow() }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = AppStrings.ascension(isPt),
                            tint = PrimaryGold
                        )
                    }

                    // Rest Action
                    IconButton(onClick = { viewModel.openShortRestModal() }) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = AppStrings.rest(isPt),
                            tint = PrimaryGold
                        )
                    }

                    // Settings Action
                    IconButton(onClick = { viewModel.openSettingsModal() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = AppStrings.settings(isPt),
                            tint = PrimaryGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = OnSurface
                ),
                modifier = Modifier.drawBehind {
                    drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceRaised,
                contentColor = OnSurfaceVariant,
                modifier = Modifier.drawBehind {
                    drawLine(DividerColor, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
                }
            ) {
                navItems.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = safeTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(navItem.icon, contentDescription = navItem.title) },
                        label = { Text(navItem.title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF3D2E00),
                            selectedTextColor = PrimaryGold,
                            indicatorColor = PrimaryGold,
                            unselectedIconColor = OnSurfaceVariant,
                            unselectedTextColor = OnSurfaceVariant
                        )
                    )
                }
            }
        },
        containerColor = SurfaceBase
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentRole == "DM") {
                when (safeTab) {
                    0 -> CampaignScreen(
                        viewModel = viewModel,
                        onNavigateToCombat = { selectedTab = 3 }
                    )
                    1 -> DmCharactersScreen(
                        viewModel = viewModel,
                        onNavigateToCombat = { selectedTab = 3 }
                    )
                    2 -> DmSpellsScreen(
                        viewModel = viewModel,
                        onOpenDiceTray = { die, mod, label -> viewModel.openDiceTray(die, mod, label) }
                    )
                    3 -> CombatScreen(
                        viewModel = viewModel,
                        onOpenDiceTray = { die, mod, label -> viewModel.openDiceTray(die, mod, label) }
                    )
                }
            } else { // "PLAYER" mode
                when (safeTab) {
                    0 -> CharacterScreen(
                        viewModel = viewModel,
                        onOpenDiceTray = { die, mod, label -> viewModel.openDiceTray(die, mod, label) }
                    )
                    1 -> SpellsScreen(
                        viewModel = viewModel,
                        onOpenDiceTray = { die, mod, label -> viewModel.openDiceTray(die, mod, label) }
                    )
                    2 -> InventoryScreen(
                        viewModel = viewModel
                    )
                    3 -> CombatScreen(
                        viewModel = viewModel,
                        onOpenDiceTray = { die, mod, label -> viewModel.openDiceTray(die, mod, label) }
                    )
                    4 -> NotesScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Role Switcher Bottom Sheet
    if (showRoleSwitcherSheet) {
        RoleSwitcherBottomSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.closeRoleSwitcherSheet() }
        )
    }

    // Global Dice Tray Sheet Modal
    if (showDiceTray) {
        DiceTrayBottomSheet(viewModel = viewModel)
    }

    // Short Rest Modal
    if (showShortRestModal) {
        ShortRestBottomSheet(viewModel = viewModel)
    }

    // Long Rest Modal
    if (showLongRestModal) {
        LongRestBottomSheet(viewModel = viewModel)
    }

    // Settings Modal
    if (showSettingsModal) {
        SettingsModal(
            viewModel = viewModel,
            onDismiss = { viewModel.closeSettingsModal() }
        )
    }

    // Ascension Flow Modal
    if (showAscensionFlow) {
        AscensionWizardModal(viewModel = viewModel)
    }
}
