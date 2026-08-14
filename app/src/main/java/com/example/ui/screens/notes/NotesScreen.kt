package com.example.ui.screens.notes

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.NoteEntity
import com.example.ui.theme.DividerColor
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerGold
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryParchment
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised
import com.example.ui.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: GrimoireViewModel) {
    val notes by viewModel.notes.collectAsState()
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Da Mesa (Handouts), 1: Minhas Notas
    var searchQuery by remember { mutableStateOf("") }
    var selectedNoteForDetail by remember { mutableStateOf<NoteEntity?>(null) }
    var showCreateNoteModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diário & Documentos",
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub Tabs: Da Mesa vs Minhas Notas
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = SurfaceRaised,
                contentColor = PrimaryGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                        color = PrimaryGold
                    )
                },
                divider = { Box(modifier = Modifier.height(1.dp).background(DividerColor)) }
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Text(
                            "Da Mesa (${notes.count { it.isHandout }})",
                            fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Text(
                            "Minhas Notas (${notes.count { !it.isHandout }})",
                            fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar anotações, pistas ou mapas...", fontSize = 13.sp, color = OnSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = SurfaceRaised,
                    unfocusedContainerColor = SurfaceBase
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filtered List
            val currentList = notes.filter { note ->
                val matchesTab = if (selectedSubTab == 0) note.isHandout else !note.isHandout
                val matchesQuery = searchQuery.isEmpty() ||
                        note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
                matchesTab && matchesQuery
            }

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedSubTab == 0) Icons.Default.Map else Icons.Default.Description,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedSubTab == 0) "Nenhum documento compartilhado pelo Mestre." else "Nenhuma nota pessoal.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(currentList, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { selectedNoteForDetail = note },
                            onDelete = { viewModel.deleteNote(note.id) }
                        )
                    }
                }
            }
        }

        // FAB for creating note (Minhas Notas tab)
        if (selectedSubTab == 1) {
            FloatingActionButton(
                onClick = { showCreateNoteModal = true },
                containerColor = PrimaryGold,
                contentColor = Color(0xFF3D2E00),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Nota")
            }
        }
    }

            // Modal Details for Handout / Note
    if (selectedNoteForDetail != null) {
        NoteDetailViewerModal(
            note = selectedNoteForDetail!!,
            onDismiss = { selectedNoteForDetail = null },
            onUpdateNote = { updatedNote ->
                viewModel.updateNote(updatedNote)
                selectedNoteForDetail = null
            }
        )
    }

    // Modal Editor for New Note
    if (showCreateNoteModal) {
        CreateNoteBottomSheet(
            onDismiss = { showCreateNoteModal = false },
            onSave = { title, content, tags, imageUrl, isHandout ->
                viewModel.createPersonalNote(title, content, tags, imageUrl, isHandout)
                showCreateNoteModal = false
            }
        )
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Image Preview for Maps/Handouts
            if (note.imageUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceBase)
                ) {
                    AsyncImage(
                        model = note.imageUrl,
                        contentDescription = note.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Mapa / Visual", fontSize = 10.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = note.date,
                    fontSize = 11.sp,
                    color = SecondaryParchment
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tag Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val tags = try {
                        note.tagsJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
                    } catch (e: Exception) {
                        listOf("Pista")
                    }
                    tags.forEach { tag ->
                        val cleanTag = tag.trim()
                        if (cleanTag.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryContainerGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("#$cleanTag", fontSize = 10.sp, color = PrimaryGold, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                if (!note.isHandout) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailViewerModal(
    note: NoteEntity,
    onDismiss: () -> Unit,
    onUpdateNote: (NoteEntity) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var zoomLevel by remember { mutableStateOf(1.0f) }
    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(note.title) }
    var editContent by remember { mutableStateOf(note.content) }
    var editImageUrl by remember { mutableStateOf(note.imageUrl) }

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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Editar Documento" else note.title,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
                Row {
                    androidx.compose.material3.TextButton(onClick = { isEditing = !isEditing }) {
                        Text(if (isEditing) "Cancelar" else "Editar", color = PrimaryGold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                    }
                }
            }

            Text(
                text = "Data: ${note.date}",
                fontSize = 12.sp,
                color = SecondaryParchment
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = { editTitle = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editImageUrl,
                    onValueChange = { editImageUrl = it },
                    label = { Text("URL da Imagem / Mapa") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editContent,
                    onValueChange = { editContent = it },
                    label = { Text("Conteúdo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val updated = note.copy(
                            title = editTitle,
                            content = editContent,
                            imageUrl = editImageUrl
                        )
                        onUpdateNote(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Alterações")
                }
            } else {
                if (note.imageUrl.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceBase)
                            .border(1.dp, PrimaryGold, RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = note.imageUrl,
                            contentDescription = note.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                        ) {
                            IconButton(onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.8f) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ZoomOut, contentDescription = null, tint = OnSurface, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.5f) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ZoomIn, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceRaised)
                        .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = note.content,
                        fontSize = 14.sp,
                        color = OnSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Fechar Documento", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteBottomSheet(
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, tags: List<String>, imageUrl: String, isHandout: Boolean) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isHandout by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf("Pista") }

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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nova Anotação",
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = PrimaryGold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da Nota", color = SecondaryParchment) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = SurfaceRaised,
                    unfocusedContainerColor = SurfaceBase
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL da Imagem ou Mapa (Opcional)", color = SecondaryParchment) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = SurfaceRaised,
                    unfocusedContainerColor = SurfaceBase
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.Switch(
                    checked = isHandout,
                    onCheckedChange = { isHandout = it },
                    colors = androidx.compose.material3.SwitchDefaults.colors(checkedThumbColor = PrimaryGold)
                )
                Text(if (isHandout) "Documento 'Da Mesa' (Handout do Mestre)" else "Anotação Pessoal", fontSize = 12.sp, color = OnSurface)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Categoria / Tag", fontSize = 12.sp, color = SecondaryParchment)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Pista", "NPC", "Localização", "Missão", "Mapa").forEach { tag ->
                    val isSelected = selectedTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) PrimaryGold else SurfaceRaised)
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF3D2E00) else OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Conteúdo / Descrição", color = SecondaryParchment) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = SurfaceRaised,
                    unfocusedContainerColor = SurfaceBase
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, content, listOf(selectedTag), imageUrl, isHandout)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Salvar no Diário", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
