package com.example.ui.screens.dm

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpellEntity
import com.example.ui.screens.spells.SpellCard
import com.example.ui.screens.spells.SpellDetailBottomSheet
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmSpellsScreen(
    viewModel: GrimoireViewModel,
    onOpenDiceTray: (String, Int, String) -> Unit
) {
    val spells by viewModel.spells.collectAsState()
    val campaignNpcs by viewModel.campaignNpcs.collectAsState()
    val selectedSpell by viewModel.selectedSpell.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("Todas") }

    val filterOptions = listOf("Todas", "Magias dos Jogadores", "Truques", "Nível 1", "Nível 2", "Nível 3", "Nível 4+")

    val filteredSpells = spells.filter { spell ->
        val matchesSearch = spell.name.contains(searchQuery, ignoreCase = true) ||
                spell.school.contains(searchQuery, ignoreCase = true) ||
                spell.description.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (activeFilter) {
            "Truques" -> spell.level == 0
            "Nível 1" -> spell.level == 1
            "Nível 2" -> spell.level == 2
            "Nível 3" -> spell.level == 3
            "Nível 4+" -> spell.level >= 4
            else -> true
        }

        matchesSearch && matchesFilter
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "REPOSITÓRIO DE MAGIAS DA CAMPANHA",
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
                Text(
                    text = "Visão do Mestre • Magias e Poderes de PCs e NPCs",
                    fontSize = 11.sp,
                    color = OnSurfaceVariant
                )
            }

            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar magia na campanha...", color = OnSurfaceVariant, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = SurfaceRaised,
                    unfocusedContainerColor = SurfaceRaised,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = filter == activeFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGold,
                            selectedLabelColor = Color(0xFF3D2E00),
                            containerColor = SurfaceRaised,
                            labelColor = OnSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = DividerColor,
                            selectedBorderColor = PrimaryGold
                        ),
                        shape = CircleShape
                    )
                }
            }

            // Spells List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSpells) { spell ->
                    SpellCard(
                        spell = spell,
                        onClick = { viewModel.selectSpellForDetail(spell) },
                        onCast = {
                            viewModel.castSpell(spell, spell.level.coerceAtLeast(1))
                            if (spell.damageOrEffect.contains("d", ignoreCase = true)) {
                                onOpenDiceTray("D20", 4, "Ataque - ${spell.name}")
                            }
                        }
                    )
                }
            }
        }

        // Spell Detail Bottom Sheet Modal
        if (selectedSpell != null) {
            SpellDetailBottomSheet(
                spell = selectedSpell!!,
                sheetState = sheetState,
                onDismiss = { viewModel.selectSpellForDetail(null) },
                onDelete = {
                    viewModel.deleteSpell(selectedSpell!!.id)
                    viewModel.selectSpellForDetail(null)
                },
                onCast = { level ->
                    viewModel.castSpell(selectedSpell!!, level)
                    viewModel.selectSpellForDetail(null)
                    if (selectedSpell!!.damageOrEffect.contains("d", ignoreCase = true)) {
                        onOpenDiceTray("D6", 0, "Dano - ${selectedSpell!!.name}")
                    }
                }
            )
        }
    }
}
