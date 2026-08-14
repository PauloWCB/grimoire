package com.example.domain.rules

import com.example.data.model.CharacterEntity
import org.json.JSONArray
import org.json.JSONObject

data class ConditionRuleInfo(
    val name: String,
    val summary: String,
    val fullDescription: String
)

data class DndSkillInfo(
    val name: String,
    val attributeName: String, // "FOR", "DES", "INT", "SAB", "CAR"
    val attributeFullName: String
)

data class ActiveCondition(
    val name: String,
    val remainingTurns: Int = 1,
    val isPermanent: Boolean = false,
    val note: String = ""
) {
    fun displayLabel(): String {
        return if (isPermanent) {
            if (note.isNotBlank()) "$name ($note)" else "$name (Contínuo)"
        } else {
            val turnText = if (remainingTurns == 1) "1 turno" else "$remainingTurns turnos"
            if (note.isNotBlank()) "$name ($turnText - $note)" else "$name ($turnText)"
        }
    }
}

object DndRulesEngine {

    // --- Core Ability Modifiers ---
    fun getAbilityModifier(score: Int): Int {
        return Math.floorDiv(score - 10, 2)
    }

    fun getModifierString(score: Int): String {
        val mod = getAbilityModifier(score)
        return if (mod >= 0) "+$mod" else "$mod"
    }

    // --- Derived Character Statistics ---
    fun calculateProficiencyBonus(level: Int): Int {
        return 2 + (level.coerceAtLeast(1) - 1) / 4
    }

    fun calculateInitiative(dexScore: Int, extraBonus: Int = 0): Int {
        return getAbilityModifier(dexScore) + extraBonus
    }

    fun calculatePassivePerception(wisScore: Int, level: Int, isProficient: Boolean = true): Int {
        val prof = if (isProficient) calculateProficiencyBonus(level) else 0
        return 10 + getAbilityModifier(wisScore) + prof
    }

    fun getSpellcastingAbilityName(characterClass: String): String {
        return when (characterClass.lowercase().trim()) {
            "mago", "artífice", "wizard", "artificer" -> "INT"
            "clérigo", "druida", "patrulheiro", "cleric", "druid", "ranger" -> "WIS"
            "bardo", "paladino", "feiticeiro", "bruxo", "bard", "paladin", "sorcerer", "warlock" -> "CHA"
            else -> "INT"
        }
    }

    fun getSpellcastingAbilityScore(character: CharacterEntity): Int {
        return when (getSpellcastingAbilityName(character.characterClass)) {
            "INT" -> character.intScore
            "WIS" -> character.wis
            "CHA" -> character.cha
            else -> maxOf(character.intScore, character.wis, character.cha)
        }
    }

    fun calculateSpellSaveDc(character: CharacterEntity): Int {
        val prof = calculateProficiencyBonus(character.level)
        val spellMod = getAbilityModifier(getSpellcastingAbilityScore(character))
        return 8 + prof + spellMod
    }

    fun calculateSpellAttackBonus(character: CharacterEntity): Int {
        val prof = calculateProficiencyBonus(character.level)
        val spellMod = getAbilityModifier(getSpellcastingAbilityScore(character))
        return prof + spellMod
    }

    fun calculateCarryingCapacityKg(strScore: Int): Float {
        return strScore * 7.5f
    }

    // --- Complete D&D 5e Skills List (Perícias) ---
    val ALL_SKILLS: List<DndSkillInfo> = listOf(
        DndSkillInfo("Acrobacia", "DES", "Destreza"),
        DndSkillInfo("Adestrar Animais", "SAB", "Sabedoria"),
        DndSkillInfo("Arcanismo", "INT", "Inteligência"),
        DndSkillInfo("Atletismo", "FOR", "Força"),
        DndSkillInfo("Atuação", "CAR", "Carisma"),
        DndSkillInfo("Enganação", "CAR", "Carisma"),
        DndSkillInfo("Furtividade", "DES", "Destreza"),
        DndSkillInfo("História", "INT", "Inteligência"),
        DndSkillInfo("Intimidação", "CAR", "Carisma"),
        DndSkillInfo("Intuição", "SAB", "Sabedoria"),
        DndSkillInfo("Investigação", "INT", "Inteligência"),
        DndSkillInfo("Medicina", "SAB", "Sabedoria"),
        DndSkillInfo("Natureza", "INT", "Inteligência"),
        DndSkillInfo("Percepção", "SAB", "Sabedoria"),
        DndSkillInfo("Persuasão", "CAR", "Carisma"),
        DndSkillInfo("Prestidigitação", "DES", "Destreza"),
        DndSkillInfo("Religião", "INT", "Inteligência"),
        DndSkillInfo("Sobrevivência", "SAB", "Sabedoria")
    )

    fun getSkillBonus(skill: DndSkillInfo, character: CharacterEntity, isProficient: Boolean = false): Int {
        val score = when (skill.attributeName) {
            "FOR" -> character.str
            "DES" -> character.dex
            "CON" -> character.con
            "INT" -> character.intScore
            "SAB" -> character.wis
            "CAR" -> character.cha
            else -> character.str
        }
        val mod = getAbilityModifier(score)
        val prof = if (isProficient) calculateProficiencyBonus(character.level) else 0
        return mod + prof
    }

    // --- Complete D&D 5e Conditions & Effects Knowledge Base ---
    val ALL_CONDITIONS: List<ConditionRuleInfo> = listOf(
        ConditionRuleInfo(
            name = "Cego",
            summary = "Falha em testes de visão. Ataques sofridos têm vantagem, seus ataques têm desvantagem.",
            fullDescription = "• Uma criatura cega não pode ver e falha automaticamente em qualquer teste de habilidade que requeira visão.\n" +
                    "• Jogadas de ataque contra a criatura possuem VANTAGEM.\n" +
                    "• As jogadas de ataque da criatura possuem DESVANTAGEM."
        ),
        ConditionRuleInfo(
            name = "Encantado",
            summary = "Não pode atacar o encantador. O encantador tem vantagem em testes sociais contra a criatura.",
            fullDescription = "• Uma criatura encantada não pode atacar o encantador nem alvejá-lo com habilidades nocivas ou efeitos mágicos.\n" +
                    "• O encantador possui VANTAGEM em qualquer teste de habilidade para interagir socialmente com a criatura."
        ),
        ConditionRuleInfo(
            name = "Surdo",
            summary = "Não pode ouvir e falha em testes que exigem audição.",
            fullDescription = "• Uma criatura surda não pode ouvir e falha automaticamente em qualquer teste de habilidade que requeira a audição."
        ),
        ConditionRuleInfo(
            name = "Amedrontado",
            summary = "Desvantagem em ataques e testes com a fonte do medo visível. Não pode se aproximar.",
            fullDescription = "• Uma criatura amedrontada tem DESVANTAGEM em testes de habilidade e jogadas de ataque enquanto a fonte do seu medo estiver em sua linha de visão.\n" +
                    "• A criatura não pode se mover voluntariamente para uma posição que a aproxime da fonte do medo."
        ),
        ConditionRuleInfo(
            name = "Agarrado",
            summary = "Deslocamento é reduzido a 0.",
            fullDescription = "• O deslocamento de uma criatura agarrada torna-se 0 e ela não pode se beneficiar de nenhum bônus em seu deslocamento.\n" +
                    "• A condição encerra se o agarrador for incapacitado ou se um efeito mover a criatura para fora do alcance."
        ),
        ConditionRuleInfo(
            name = "Incapacitado",
            summary = "Não pode realizar ações ou reações.",
            fullDescription = "• Uma criatura incapacitada não pode realizar ações nem reações de qualquer tipo."
        ),
        ConditionRuleInfo(
            name = "Invisível",
            summary = "Impossível de ser vista. Seus ataques têm vantagem, ataques sofridos têm desvantagem.",
            fullDescription = "• Uma criatura invisível é impossível de ser vista sem o auxílio de magia ou sentido especial (como Visão Trucidante).\n" +
                    "• As jogadas de ataque contra a criatura possuem DESVANTAGEM.\n" +
                    "• As jogadas de ataque da criatura possuem VANTAGEM."
        ),
        ConditionRuleInfo(
            name = "Paralisado",
            summary = "Incapacitado, não se move nem fala. Falha em salvaguardas de FOR/DES. Ataques a 1.5m são acertos críticos.",
            fullDescription = "• A criatura está incapacitada e não pode se mover ou falar.\n" +
                    "• Falha automaticamente em testes de resistência de Força e Destreza.\n" +
                    "• Ataques contra a criatura têm VANTAGEM e qualquer ataque que atingir a criatura é um acerto crítico automático se o atacante estiver a até 1,5 metro."
        ),
        ConditionRuleInfo(
            name = "Petrificado",
            summary = "Transformado em pedra inanimada. Imune a venenos e resistente a todos os danos.",
            fullDescription = "• A criatura é transformada, junto com todos os seus objetos não mágicos, em uma substância sólida inanimada (geralmente pedra).\n" +
                    "• Está incapacitada, cega e inconsciente. Tem resistência a todos os danos e imunidade a veneno e doença."
        ),
        ConditionRuleInfo(
            name = "Envenenado",
            summary = "Desvantagem em jogadas de ataque e testes de habilidade.",
            fullDescription = "• Uma criatura envenenada possui DESVANTAGEM em jogadas de ataque e testes de habilidade de qualquer tipo."
        ),
        ConditionRuleInfo(
            name = "Caído",
            summary = "Só pode rastejar. Ataques próprios têm desvantagem. Ataques sofridos a 1.5m têm vantagem.",
            fullDescription = "• A única opção de movimento da criatura é rastejar, a menos que gaste metade do seu deslocamento para se levantar.\n" +
                    "• A criatura tem DESVANTAGEM em suas jogadas de ataque.\n" +
                    "• Ataques a 1,5m contra a criatura têm VANTAGEM; ataques à distância têm DESVANTAGEM."
        ),
        ConditionRuleInfo(
            name = "Restringido",
            summary = "Deslocamento 0. Desvantagem em ataques e salvaguardas de DES. Ataques sofridos têm vantagem.",
            fullDescription = "• O deslocamento da criatura torna-se 0.\n" +
                    "• As jogadas de ataque contra a criatura possuem VANTAGEM, e os ataques da criatura têm DESVANTAGEM.\n" +
                    "• A criatura tem DESVANTAGEM em testes de resistência de Destreza."
        ),
        ConditionRuleInfo(
            name = "Atordoado",
            summary = "Incapacitado, não pode se mover. Falha em salvaguardas de FOR/DES. Ataques sofridos têm vantagem.",
            fullDescription = "• A criatura está incapacitada, não pode se mover e fala apenas hesitantemente.\n" +
                    "• Falha automaticamente em testes de resistência de Força e Destreza.\n" +
                    "• Jogadas de ataque contra a criatura possuem VANTAGEM."
        ),
        ConditionRuleInfo(
            name = "Inconsciente",
            summary = "Incapacitado, cai no chão. Falha em FOR/DES. Ataques sofridos a 1.5m são acertos críticos.",
            fullDescription = "• A criatura está incapacitada, cai no chão e larga o que estiver segurando.\n" +
                    "• Falha automaticamente em testes de resistência de Força e Destreza.\n" +
                    "• Jogadas de ataque contra ela têm VANTAGEM. Qualquer ataque a até 1,5m é um acerto crítico."
        ),
        ConditionRuleInfo(
            name = "Exaustão",
            summary = "Efeito acumulativo de 1 a 6 níveis (Nível 1: Desvantagem em testes, Nível 6: Morte).",
            fullDescription = "• Nível 1: Desvantagem em testes de habilidade.\n" +
                    "• Nível 2: Deslocamento reduzido à metade.\n" +
                    "• Nível 3: Desvantagem em jogadas de ataque e testes de resistência.\n" +
                    "• Nível 4: Pontos de vida máximos reduzidos à metade.\n" +
                    "• Nível 5: Deslocamento reduzido a 0.\n" +
                    "• Nível 6: Morte instantânea."
        ),
        ConditionRuleInfo(
            name = "Concentração",
            summary = "Mantém uma magia ativa. Ao sofrer dano, faça um teste de CON (CD 10 ou metade do dano).",
            fullDescription = "• Mantém uma magia de concentração ativa.\n" +
                    "• Se sofrer dano, deve realizar um teste de resistência de Constituição (CD 10 ou metade do dano sofrido, o que for maior) para manter a magia.\n" +
                    "• Ficar incapacitado ou conjurar outra magia de concentração encerra a concentração atual."
        )
    )

    fun getConditionInfo(name: String): ConditionRuleInfo {
        val cleanName = name.split("(")[0].trim()
        return ALL_CONDITIONS.find { it.name.equals(cleanName, ignoreCase = true) }
            ?: ConditionRuleInfo(
                name = cleanName,
                summary = "Efeito personalizado ativado na ficha.",
                fullDescription = "Efeito ou condição personalizada ($name) aplicada durante o combate ou aventura."
            )
    }

    // --- Serialization & Deserialization of Conditions ---
    fun parseConditionsJson(jsonStr: String): List<ActiveCondition> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<ActiveCondition>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.get(i)
                if (item is JSONObject) {
                    list.add(
                        ActiveCondition(
                            name = item.optString("name", "Condição"),
                            remainingTurns = item.optInt("remainingTurns", 1),
                            isPermanent = item.optBoolean("isPermanent", false),
                            note = item.optString("note", "")
                        )
                    )
                } else if (item is String) {
                    val str = item.trim()
                    if (str.lowercase().contains("concentração") || str.lowercase().contains("contínuo")) {
                        list.add(ActiveCondition(name = str.split("(")[0].trim(), remainingTurns = 0, isPermanent = true))
                    } else if (str.contains("(")) {
                        val namePart = str.substringBefore("(").trim()
                        val inside = str.substringAfter("(").substringBefore(")").trim()
                        val turns = inside.filter { it.isDigit() }.toIntOrNull() ?: 1
                        list.add(ActiveCondition(name = namePart, remainingTurns = turns, isPermanent = false))
                    } else {
                        list.add(ActiveCondition(name = str, remainingTurns = 1, isPermanent = false))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun serializeConditionsJson(conditions: List<ActiveCondition>): String {
        val jsonArray = JSONArray()
        for (c in conditions) {
            val obj = JSONObject().apply {
                put("name", c.name)
                put("remainingTurns", c.remainingTurns)
                put("isPermanent", c.isPermanent)
                put("note", c.note)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    /**
     * Decrements turn duration for active non-permanent conditions by 1 turn.
     * Returns a pair: (Updated condition list, List of expired condition names)
     */
    fun tickConditions(conditions: List<ActiveCondition>): Pair<List<ActiveCondition>, List<String>> {
        val updatedList = mutableListOf<ActiveCondition>()
        val expiredNames = mutableListOf<String>()

        for (c in conditions) {
            if (c.isPermanent) {
                updatedList.add(c)
            } else {
                val nextTurns = c.remainingTurns - 1
                if (nextTurns <= 0) {
                    expiredNames.add(c.name)
                } else {
                    updatedList.add(c.copy(remainingTurns = nextTurns))
                }
            }
        }

        return Pair(updatedList, expiredNames)
    }
}
