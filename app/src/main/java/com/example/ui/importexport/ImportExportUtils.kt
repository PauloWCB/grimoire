package com.example.ui.importexport

import com.example.data.model.CharacterEntity
import com.example.data.model.NoteEntity
import com.example.data.model.SpellEntity
import org.json.JSONArray
import org.json.JSONObject

data class ExportedData(
    val character: CharacterEntity,
    val spells: List<SpellEntity>,
    val notes: List<NoteEntity>
)

object ImportExportUtils {

    fun toJson(character: CharacterEntity, spells: List<SpellEntity>, notes: List<NoteEntity>): String {
        val root = JSONObject()

        // Character JSON
        val charObj = JSONObject().apply {
            put("id", character.id)
            put("name", character.name)
            put("characterClass", character.characterClass)
            put("subClass", character.subClass)
            put("level", character.level)
            put("subClassLevel", character.subClassLevel)
            put("race", character.race)
            put("background", character.background)
            put("currentHp", character.currentHp)
            put("maxHp", character.maxHp)
            put("tempHp", character.tempHp)
            put("ac", character.ac)
            put("initiative", character.initiative)
            put("speed", character.speed)
            put("str", character.str)
            put("dex", character.dex)
            put("con", character.con)
            put("intScore", character.intScore)
            put("wis", character.wis)
            put("cha", character.cha)
            put("conditionsJson", character.conditionsJson)
            put("slot1Current", character.slot1Current)
            put("slot1Max", character.slot1Max)
            put("slot2Current", character.slot2Current)
            put("slot2Max", character.slot2Max)
            put("slot3Current", character.slot3Current)
            put("slot3Max", character.slot3Max)
            put("slot4Current", character.slot4Current)
            put("slot4Max", character.slot4Max)
            put("activeConcentration", character.activeConcentration)
            put("cp", character.cp)
            put("sp", character.sp)
            put("ep", character.ep)
            put("gp", character.gp)
            put("pp", character.pp)
            put("avatarUrl", character.avatarUrl)
        }
        root.put("character", charObj)

        // Spells JSON Array
        val spellsArray = JSONArray()
        for (s in spells) {
            val sObj = JSONObject().apply {
                put("name", s.name)
                put("level", s.level)
                put("school", s.school)
                put("castingTime", s.castingTime)
                put("range", s.range)
                put("damageOrEffect", s.damageOrEffect)
                put("saveOrAttack", s.saveOrAttack)
                put("components", s.components)
                put("description", s.description)
                put("higherLevels", s.higherLevels)
                put("isPrepared", s.isPrepared)
                put("isConcentration", s.isConcentration)
                put("isRitual", s.isRitual)
            }
            spellsArray.put(sObj)
        }
        root.put("spells", spellsArray)

        // Notes JSON Array
        val notesArray = JSONArray()
        for (n in notes) {
            val nObj = JSONObject().apply {
                put("title", n.title)
                put("content", n.content)
                put("date", n.date)
                put("tagsJson", n.tagsJson)
                put("isHandout", n.isHandout)
                put("imageUrl", n.imageUrl)
            }
            notesArray.put(nObj)
        }
        root.put("notes", notesArray)

        return root.toString(2)
    }

    fun fromJson(jsonStr: String): ExportedData? {
        return try {
            val root = JSONObject(jsonStr)
            val charObj = if (root.has("character")) root.getJSONObject("character") else root

            val character = CharacterEntity(
                id = charObj.optLong("id", 0L),
                name = charObj.optString("name", "Personagem Importado"),
                characterClass = charObj.optString("characterClass", charObj.optString("class", "Guerreiro")),
                subClass = charObj.optString("subClass", ""),
                level = charObj.optInt("level", 1),
                subClassLevel = charObj.optInt("subClassLevel", 0),
                race = charObj.optString("race", "Humano"),
                background = charObj.optString("background", "Aventureiro"),
                currentHp = charObj.optInt("currentHp", charObj.optInt("hp", 20)),
                maxHp = charObj.optInt("maxHp", charObj.optInt("hp", 20)),
                tempHp = charObj.optInt("tempHp", 0),
                ac = charObj.optInt("ac", 14),
                initiative = charObj.optInt("initiative", 2),
                speed = charObj.optString("speed", "9m"),
                str = charObj.optInt("str", 10),
                dex = charObj.optInt("dex", 10),
                con = charObj.optInt("con", 10),
                intScore = charObj.optInt("intScore", charObj.optInt("int", 10)),
                wis = charObj.optInt("wis", 10),
                cha = charObj.optInt("cha", 10),
                conditionsJson = charObj.optString("conditionsJson", "[]"),
                slot1Current = charObj.optInt("slot1Current", 2),
                slot1Max = charObj.optInt("slot1Max", 2),
                slot2Current = charObj.optInt("slot2Current", 0),
                slot2Max = charObj.optInt("slot2Max", 0),
                slot3Current = charObj.optInt("slot3Current", 0),
                slot3Max = charObj.optInt("slot3Max", 0),
                slot4Current = charObj.optInt("slot4Current", 0),
                slot4Max = charObj.optInt("slot4Max", 0),
                activeConcentration = charObj.optString("activeConcentration", ""),
                cp = charObj.optInt("cp", 0),
                sp = charObj.optInt("sp", 0),
                ep = charObj.optInt("ep", 0),
                gp = charObj.optInt("gp", 50),
                pp = charObj.optInt("pp", 0),
                avatarUrl = charObj.optString("avatarUrl", "")
            )

            val spellsList = mutableListOf<SpellEntity>()
            if (root.has("spells")) {
                val spellsArray = root.getJSONArray("spells")
                for (i in 0 until spellsArray.length()) {
                    val sObj = spellsArray.getJSONObject(i)
                    spellsList.add(
                        SpellEntity(
                            name = sObj.optString("name", "Magia"),
                            level = sObj.optInt("level", 1),
                            school = sObj.optString("school", "Evocação"),
                            castingTime = sObj.optString("castingTime", "1 Ação"),
                            range = sObj.optString("range", "18m"),
                            damageOrEffect = sObj.optString("damageOrEffect", "Dano"),
                            saveOrAttack = sObj.optString("saveOrAttack", ""),
                            components = sObj.optString("components", "V, S"),
                            description = sObj.optString("description", ""),
                            higherLevels = sObj.optString("higherLevels", ""),
                            isPrepared = sObj.optBoolean("isPrepared", true),
                            isConcentration = sObj.optBoolean("isConcentration", false),
                            isRitual = sObj.optBoolean("isRitual", false)
                        )
                    )
                }
            }

            val notesList = mutableListOf<NoteEntity>()
            if (root.has("notes")) {
                val notesArray = root.getJSONArray("notes")
                for (i in 0 until notesArray.length()) {
                    val nObj = notesArray.getJSONObject(i)
                    notesList.add(
                        NoteEntity(
                            title = nObj.optString("title", "Nota"),
                            content = nObj.optString("content", ""),
                            date = nObj.optString("date", "Importado"),
                            tagsJson = nObj.optString("tagsJson", "[\"Geral\"]"),
                            isHandout = nObj.optBoolean("isHandout", false),
                            imageUrl = nObj.optString("imageUrl", "")
                        )
                    )
                }
            }

            ExportedData(character, spellsList, notesList)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
