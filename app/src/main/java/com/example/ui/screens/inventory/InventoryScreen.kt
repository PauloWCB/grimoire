package com.example.ui.screens.inventory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.ItemEntity
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CopperColor
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.ElectrumColor
import com.example.ui.theme.GoldColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineGold
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.PlatinumColor
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SilverColor
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: GrimoireViewModel
) {
    val characterState by viewModel.character.collectAsState()
    val character = characterState ?: CharacterEntity()
    val items by viewModel.items.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var selectedCurrencyForEdit by remember { mutableStateOf<Pair<String, Int>?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val equippedItems = items.filter { it.category == "Equipado" }
    val backpackItems = items.filter { it.category == "Mochila" }
    val consumableItems = items.filter { it.category == "Consumíveis" }

    val totalWeight = items.sumOf { (it.weightKg * it.quantity).toDouble() }.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // 1. Inventory Header Title (At the top of Inventory tab)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "INVENTÁRIO DO PERSONAGEM",
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
            }

            // 2. Currencies Row (Moedas do Personagem)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MOEDAS DO PERSONAGEM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Clique na moeda para alterar o saldo",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CurrencyBadge("PC", character.cp, CopperColor) { selectedCurrencyForEdit = "CP" to character.cp }
                        CurrencyBadge("PP", character.sp, SilverColor) { selectedCurrencyForEdit = "SP" to character.sp }
                        CurrencyBadge("PE", character.ep, ElectrumColor) { selectedCurrencyForEdit = "EP" to character.ep }
                        CurrencyBadge("PO", character.gp, GoldColor) { selectedCurrencyForEdit = "GP" to character.gp }
                        CurrencyBadge("PL", character.pp, PlatinumColor) { selectedCurrencyForEdit = "PP" to character.pp }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Items Groups List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category: Equipado
                if (equippedItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "EQUIPADO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(equippedItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = { viewModel.selectItemForDetail(item) },
                            onQtyChange = { delta -> viewModel.adjustItemQuantity(item, delta) }
                        )
                    }
                }

                // Category: Mochila
                if (backpackItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "MOCHILA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(backpackItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = { viewModel.selectItemForDetail(item) },
                            onQtyChange = { delta -> viewModel.adjustItemQuantity(item, delta) }
                        )
                    }
                }

                // Category: Consumíveis
                if (consumableItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "CONSUMÍVEIS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(consumableItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = { viewModel.selectItemForDetail(item) },
                            onQtyChange = { delta -> viewModel.adjustItemQuantity(item, delta) }
                        )
                    }
                }
            }
        }

        // FAB for Add Item (matches Roll Dice FAB style)
        FloatingActionButton(
            onClick = { showAddItemDialog = true },
            containerColor = PrimaryGold,
            contentColor = Color(0xFF3D2E00),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Item", modifier = Modifier.size(28.dp))
        }

        // Item Detail Bottom Sheet
        if (selectedItem != null) {
            ItemDetailBottomSheet(
                item = selectedItem!!,
                sheetState = sheetState,
                onDismiss = { viewModel.selectItemForDetail(null) },
                onToggleEquip = {
                    viewModel.toggleItemEquipped(selectedItem!!)
                    viewModel.selectItemForDetail(null)
                },
                onDelete = {
                    viewModel.deleteItem(selectedItem!!.id)
                }
            )
        }

        // Currency Edit Modal Bottom Sheet
        if (selectedCurrencyForEdit != null) {
            CurrencyEditModalBottomSheet(
                currencyType = selectedCurrencyForEdit!!.first,
                currentAmount = selectedCurrencyForEdit!!.second,
                onDismiss = { selectedCurrencyForEdit = null },
                onSave = { newAmount ->
                    viewModel.setCurrency(selectedCurrencyForEdit!!.first, newAmount)
                    selectedCurrencyForEdit = null
                }
            )
        }

        // Add Item Dialog
        if (showAddItemDialog) {
            AddItemDialog(
                onDismiss = { showAddItemDialog = false },
                onAdd = { name, cat, weight, qty, props, description ->
                    viewModel.addItem(name, cat, weight, qty, props, description)
                    showAddItemDialog = false
                }
            )
        }
    }
}

@Composable
fun CurrencyBadge(
    label: String,
    amount: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceBase),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text("$amount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Alterar", fontSize = 9.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ItemCard(
    item: ItemEntity,
    onClick: () -> Unit,
    onQtyChange: (Int) -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, DividerColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.iconType) {
                            "shield" -> Icons.Default.Shield
                            "potion" -> Icons.Default.LocalDrink
                            "bed" -> Icons.Default.Bed
                            else -> Icons.Default.SportsKabaddi
                        },
                        contentDescription = null,
                        tint = PrimaryGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(item.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    if (item.properties.isNotEmpty()) {
                        Text(item.properties, fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                    Text("${item.weightKg} kg", fontSize = 11.sp, color = OutlineGold)
                }
            }

            // Quantity Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceBase)
                    .border(1.dp, DividerColor, RoundedCornerShape(6.dp))
            ) {
                IconButton(onClick = { onQtyChange(-1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "-1", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
                Text("${item.quantity}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface, modifier = Modifier.padding(horizontal = 6.dp))
                IconButton(onClick = { onQtyChange(+1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "+1", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailBottomSheet(
    item: ItemEntity,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onToggleEquip: () -> Unit,
    onDelete: () -> Unit
) {
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
                .verticalScroll(rememberScrollState())
        ) {
            // Optional Illustration Header
            if (item.imageUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceBase)
                        .border(1.dp, PrimaryGold, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Rarity Tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PrimaryContainerGold)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(item.rarity.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3D2E00))
                }

                if (item.requiresAttunement) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceRaised)
                            .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("REQUER SINTONIZAÇÃO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = item.name,
                fontFamily = FontFamily.Serif,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lore / Description
            if (item.lore.isNotEmpty()) {
                Text(
                    text = item.lore,
                    fontSize = 13.sp,
                    color = SecondaryParchment,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = item.description,
                fontSize = 14.sp,
                color = OnSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Propriedades:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text(item.properties, fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Peso:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text("${item.weightKg} kg", fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Valor:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text("${item.valueGp} PO", fontSize = 13.sp, color = OnSurface, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DamageRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DamageRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover")
                }

                Button(
                    onClick = onToggleEquip,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (item.category == "Equipado") "Desequipar Item" else "Equipar Item",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Float, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Mochila") }
    var weightStr by remember { mutableStateOf("1.0") }
    var qtyStr by remember { mutableStateOf("1") }
    var props by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainer,
        title = {
            Text("Adicionar Novo Item", fontFamily = FontFamily.Serif, color = PrimaryGold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Item") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria (Equipado, Mochila, Consumíveis)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Peso (kg)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                    )
                    OutlinedTextField(
                        value = qtyStr,
                        onValueChange = { qtyStr = it },
                        label = { Text("Quantidade") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                    )
                }

                OutlinedTextField(
                    value = props,
                    onValueChange = { props = it },
                    label = { Text("Propriedades (ex: 1d8 cortante)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição / Efeito do Item") },
                    modifier = Modifier.height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGold, unfocusedBorderColor = DividerColor)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        val w = weightStr.toFloatOrNull() ?: 1.0f
                        val q = qtyStr.toIntOrNull() ?: 1
                        onAdd(name, category, w, q, props, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00))
            ) {
                Text("Adicionar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = OnSurfaceVariant)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyEditModalBottomSheet(
    currencyType: String,
    currentAmount: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val fullName = when (currencyType) {
        "CP" -> "Peças de Cobre (PC)"
        "SP" -> "Peças de Prata (PP)"
        "EP" -> "Peças de Electrum (PE)"
        "GP" -> "Peças de Ouro (PO)"
        "PP" -> "Peças de Platina (PL)"
        else -> currencyType
    }
    val coinColor = when (currencyType) {
        "CP" -> CopperColor
        "SP" -> SilverColor
        "EP" -> ElectrumColor
        "GP" -> GoldColor
        "PP" -> PlatinumColor
        else -> PrimaryGold
    }

    var textValue by remember(currentAmount) { mutableStateOf(currentAmount.toString()) }
    val parsedValue = textValue.toIntOrNull() ?: 0
    var sliderVal by remember(currentAmount) { mutableFloatStateOf(currentAmount.toFloat()) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AJUSTAR MOEDAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = coinColor,
                letterSpacing = 1.sp
            )

            Text(
                text = fullName,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            // Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceBase)
                    .border(1.5.dp, coinColor, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$parsedValue $currencyType",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = coinColor
                )
            }

            // Text Field Input
            OutlinedTextField(
                value = textValue,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    textValue = filtered
                    val num = filtered.toIntOrNull() ?: 0
                    sliderVal = num.toFloat().coerceIn(0f, 1000f)
                },
                label = { Text("Digite o Valor Total ($currencyType)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = coinColor,
                    unfocusedBorderColor = DividerColor
                )
            )

            // Slider Drag Adjustment
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Arraste para ajustar valor:", fontSize = 12.sp, color = OnSurfaceVariant)
                    Text("${sliderVal.toInt()} $currencyType", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = coinColor)
                }
                Slider(
                    value = sliderVal,
                    onValueChange = { newVal ->
                        sliderVal = newVal
                        textValue = newVal.toInt().toString()
                    },
                    valueRange = 0f..maxOf((currentAmount * 2).toFloat(), 500f),
                    colors = SliderDefaults.colors(
                        thumbColor = coinColor,
                        activeTrackColor = coinColor,
                        inactiveTrackColor = DividerColor
                    )
                )
            }

            // Quick Delta Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(-50, -10, -1, 1, 10, 50, 100).forEach { delta ->
                    val label = if (delta > 0) "+$delta" else "$delta"
                    OutlinedButton(
                        onClick = {
                            val updated = (parsedValue + delta).coerceAtLeast(0)
                            textValue = updated.toString()
                            sliderVal = updated.toFloat()
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Confirm Button
            Button(
                onClick = {
                    onSave(parsedValue)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Confirmar Novo Saldo", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
