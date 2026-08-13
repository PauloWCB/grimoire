package com.example.ui.screens.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CharacterEntity
import com.example.domain.rules.DndRulesEngine
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DamageRed
import com.example.ui.theme.DividerColor
import com.example.ui.theme.HealGreen
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SurfaceBase
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceRaised

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    pc: CharacterEntity? = null,
    npc: CampaignNpcEntity? = null,
    onClose: () -> Unit,
    onSendToCombat: (() -> Unit)? = null
) {
    val name = pc?.name ?: npc?.name ?: "Personagem"
    val subtitle = pc?.let { "${it.race} ${it.characterClass} • Nível ${it.level}" }
        ?: npc?.let { "${it.roleOrTitle} • ND ${it.challengeRating}" }
        ?: "Ficha Completa"

    val currentHp = pc?.currentHp ?: npc?.hp ?: 10
    val maxHp = pc?.maxHp ?: npc?.maxHp ?: 10
    val ac = pc?.ac ?: npc?.ac ?: 10
    val speed = pc?.speed ?: npc?.speed ?: "9m"
    val avatarUrl = pc?.avatarUrl ?: npc?.avatarUrl ?: ""

    val str = pc?.str ?: npc?.str ?: 10
    val dex = pc?.dex ?: npc?.dex ?: 10
    val con = pc?.con ?: npc?.con ?: 10
    val intScore = pc?.intScore ?: npc?.intScore ?: 10
    val wis = pc?.wis ?: npc?.wis ?: 10
    val cha = pc?.cha ?: npc?.cha ?: 10

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(name, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryGold)
                        Text(subtitle, fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = PrimaryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.4f)),
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
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                                .border(2.dp, PrimaryGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            } else {
                                Text(name.take(1), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Text(subtitle, fontSize = 13.sp, color = PrimaryGold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column {
                                    Text("PONTOS DE VIDA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = HealGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("$currentHp / $maxHp", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HealGreen)
                                    }
                                }

                                Column {
                                    Text("ARMADURA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("CA $ac", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                                    }
                                }

                                Column {
                                    Text("DESLOC.", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                                    Text(speed, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                }
                            }
                        }
                    }
                }
            }

            // Atributos (STR, DEX, CON, INT, WIS, CHA)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ATRIBUTOS & MODIFICADORES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            "FOR" to str,
                            "DES" to dex,
                            "CON" to con,
                            "INT" to intScore,
                            "SAB" to wis,
                            "CAR" to cha
                        ).forEach { (label, value) ->
                            val mod = DndRulesEngine.getAbilityModifier(value)
                            val modStr = if (mod >= 0) "+$mod" else "$mod"

                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.width(52.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                                    Text(value.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceContainer)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(modStr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HealGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Perícias & Modificadores de Testes
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("PERÍCIAS & TESTES (D&D 5E)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val profBonus = pc?.let { DndRulesEngine.calculateProficiencyBonus(it.level) } ?: 2
                        val skills = listOf(
                            "Acrobacia (DES)" to DndRulesEngine.getAbilityModifier(dex),
                            "Arcanismo (INT)" to DndRulesEngine.getAbilityModifier(intScore),
                            "Atletismo (FOR)" to DndRulesEngine.getAbilityModifier(str) + profBonus,
                            "Atuação (CAR)" to DndRulesEngine.getAbilityModifier(cha),
                            "Enganação (CAR)" to DndRulesEngine.getAbilityModifier(cha),
                            "História (INT)" to DndRulesEngine.getAbilityModifier(intScore),
                            "Intimidação (CAR)" to DndRulesEngine.getAbilityModifier(cha),
                            "Intuição (SAB)" to DndRulesEngine.getAbilityModifier(wis),
                            "Investigação (INT)" to DndRulesEngine.getAbilityModifier(intScore) + profBonus,
                            "Lidar com Animais (SAB)" to DndRulesEngine.getAbilityModifier(wis),
                            "Medicina (SAB)" to DndRulesEngine.getAbilityModifier(wis),
                            "Natureza (INT)" to DndRulesEngine.getAbilityModifier(intScore),
                            "Percepção (SAB)" to DndRulesEngine.getAbilityModifier(wis) + profBonus,
                            "Persuasão (CAR)" to DndRulesEngine.getAbilityModifier(cha) + profBonus,
                            "Prestidigitação (DES)" to DndRulesEngine.getAbilityModifier(dex),
                            "Religião (INT)" to DndRulesEngine.getAbilityModifier(intScore),
                            "Sobrevivência (SAB)" to DndRulesEngine.getAbilityModifier(wis),
                            "Furtividade (DES)" to DndRulesEngine.getAbilityModifier(dex)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            skills.chunked(2).forEach { rowSkills ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowSkills.forEach { (skillName, valBonus) ->
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(skillName, fontSize = 11.sp, color = OnSurfaceVariant)
                                            Text(
                                                text = if (valBonus >= 0) "+$valBonus" else "$valBonus",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryGold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Ataques e Ações / Speells / Habilidades
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ATAQUES & AÇÕES EM COMBATE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                        }

                        val actions = npc?.actionsJson ?: "[\"Ataque de Espada (+5, 1d8+3 cortante)\", \"Ataque com Arco Curto (+4, 1d6+2 perfurante, 24m)\"]"
                        Text(actions.replace("[", "").replace("]", "").replace("\"", ""), fontSize = 12.sp, color = OnSurface, lineHeight = 18.sp)

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MAGIAS & CONJURAÇÕES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                        }

                        val spells = npc?.spellsJson ?: "[\"Truque: Luz\", \"Truque: Raio de Fogo\", \"1º Nível: Escudo Arcano (4/dia)\", \"2º Nível: Passo Nebuloso (2/dia)\"]"
                        Text(spells.replace("[", "").replace("]", "").replace("\"", ""), fontSize = 12.sp, color = OnSurfaceVariant, lineHeight = 18.sp)
                    }
                }
            }

            // Descrição & Histórico
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("DESCRIÇÃO & PERSONALIDADE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = npc?.description ?: "Aventureiro destemido explorando as terras sob a orientação do Grimoire VTT.",
                            fontSize = 12.sp,
                            color = OnSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Send to combat button if provided
            if (onSendToCombat != null) {
                item {
                    Button(
                        onClick = onSendToCombat,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = Color(0xFF3D2E00)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lançar no Tracker de Combate", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
