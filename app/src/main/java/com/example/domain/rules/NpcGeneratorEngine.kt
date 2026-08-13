package com.example.domain.rules

import com.example.data.model.CampaignNpcEntity
import kotlin.random.Random

object NpcGeneratorEngine {

    private val FIRST_NAMES = listOf(
        "Alden", "Balthazar", "Caelen", "Dorian", "Eldrin", "Fiora", "Gideon", "Hadrian",
        "Isolda", "Jarvis", "Kaelen", "Lysandra", "Maelor", "Nyssa", "Orion", "Pyria",
        "Quentin", "Rorik", "Seraphina", "Theron", "Ulysses", "Valeria", "Willem", "Zephyr"
    )

    private val LAST_NAMES = listOf(
        "Ironhand", "Shadowweaver", "Stormborn", "Blackwood", "Frostbeard", "Ravenwing",
        "Silverlance", "Ashford", "Oakenshield", "Nightshade", "Sunstrider", "Duskwalker"
    )

    private val CITIZEN_ROLES = listOf(
        "Ferreiro da Cidade", "Mercador Vazio", "Alquimista Local", "Guarda Municipal",
        "Sábio da Biblioteca", "Taberneiro Experiente", "Sacerdote Templário", "Capitão da Guarda"
    )

    private val COMBAT_MONSTER_ROLES = listOf(
        "Lorde Orc Guerreador", "Cultista Fanático", "Mercenário Espada-de-Aluguel",
        "Mago das Sombras", "Lobo Atroz Alfa", "Banshee Ancestral", "Espião Assassino", "Minotauro do Labirinto"
    )

    private val BOSS_ROLES = listOf(
        "Dragão Jovem de Lodo", "Arquilorde Vampiro", "Lich das Profundezas", "Guerreiro Abissal", "Arquimago Corrompido"
    )

    fun generateNpc(campaignId: Long, presetType: String): CampaignNpcEntity {
        val firstName = FIRST_NAMES.random()
        val lastName = LAST_NAMES.random()
        val fullName = "$firstName $lastName"

        return when (presetType.lowercase()) {
            "citizen", "cidadão" -> {
                val role = CITIZEN_ROLES.random()
                val lvl = Random.nextInt(1, 6)
                CampaignNpcEntity(
                    campaignId = campaignId,
                    name = fullName,
                    roleOrTitle = "$role (Nível $lvl)",
                    challengeRating = "1",
                    hp = 20 + lvl * 6,
                    maxHp = 20 + lvl * 6,
                    ac = 12 + Random.nextInt(0, 3),
                    speed = "9m",
                    str = 10 + Random.nextInt(0, 5),
                    dex = 12 + Random.nextInt(0, 3),
                    con = 12 + Random.nextInt(0, 3),
                    intScore = 11 + Random.nextInt(0, 4),
                    wis = 12 + Random.nextInt(0, 3),
                    cha = 10 + Random.nextInt(0, 4),
                    actionsJson = "[\"Adaga (Ataque: +4, 1d4+2 perfurante)\", \"Martelo de Ferreiro (+4, 1d6+2 contundente)\"]",
                    spellsJson = "[\"Luz (Truque)\", \"Remédio (Truque)\"]",
                    description = "Um habitante local de fisionomia firme, acostumado às rotinas do reino e prestativo com viajantes.",
                    isHostile = false
                )
            }
            "boss", "chefe" -> {
                val role = BOSS_ROLES.random()
                val hpVal = Random.nextInt(120, 220)
                CampaignNpcEntity(
                    campaignId = campaignId,
                    name = fullName,
                    roleOrTitle = "$role (Inimigo Lendário)",
                    challengeRating = "10+",
                    hp = hpVal,
                    maxHp = hpVal,
                    ac = 17 + Random.nextInt(0, 3),
                    speed = "9m, voo 12m",
                    str = 18 + Random.nextInt(0, 4),
                    dex = 16 + Random.nextInt(0, 3),
                    con = 18 + Random.nextInt(0, 3),
                    intScore = 16 + Random.nextInt(0, 4),
                    wis = 14 + Random.nextInt(0, 3),
                    cha = 16 + Random.nextInt(0, 4),
                    actionsJson = "[\"Ataque Múltiplo (3x Golpes Subjugadores: +9, 2d10+5 dano)\", \"Sopro / Explosão Mágica (Área 9m: CD 16 DES, 8d6 dano)\", \"Ação Lendária: Teleporte (3/dia)\"]",
                    spellsJson = "[\"Bola de Fogo (3º Nível)\", \"Muralha de Energia (5º Nível)\", \"Contramagia (3º Nível)\"]",
                    description = "Aura ameaçadora envolve este líder formidável. Seus olhos emanam energia mágica instável.",
                    isHostile = true
                )
            }
            else -> { // Medium combat NPC
                val role = COMBAT_MONSTER_ROLES.random()
                val hpVal = Random.nextInt(40, 75)
                CampaignNpcEntity(
                    campaignId = campaignId,
                    name = fullName,
                    roleOrTitle = "$role (Combate Médio - CR 4)",
                    challengeRating = "4",
                    hp = hpVal,
                    maxHp = hpVal,
                    ac = 14 + Random.nextInt(0, 3),
                    speed = "9m",
                    str = 16 + Random.nextInt(0, 3),
                    dex = 14 + Random.nextInt(0, 3),
                    con = 14 + Random.nextInt(0, 3),
                    intScore = 10 + Random.nextInt(0, 3),
                    wis = 12 + Random.nextInt(0, 3),
                    cha = 10 + Random.nextInt(0, 3),
                    actionsJson = "[\"Lança Longa (Ataque: +6, 1d8+3 perfurante)\", \"Arco Composto (+5, 1d8+2 perfurante)\"]",
                    spellsJson = "[\"Escudo Arcano (1/dia)\"]",
                    description = "Guerreiro experiente com cicatrizes de batalhas passadas, equipado para combate direto.",
                    isHostile = true
                )
            }
        }
    }
}
