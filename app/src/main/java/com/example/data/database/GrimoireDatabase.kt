package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.GrimoireDao
import com.example.data.model.CampaignEntity
import com.example.data.model.CampaignSessionEntity
import com.example.data.model.CampaignNpcEntity
import com.example.data.model.CampaignMemberEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.ItemEntity
import com.example.data.model.NoteEntity
import com.example.data.model.RollHistoryEntity
import com.example.data.model.SpellEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CharacterEntity::class,
        SpellEntity::class,
        ItemEntity::class,
        RollHistoryEntity::class,
        NoteEntity::class,
        CampaignEntity::class,
        CampaignSessionEntity::class,
        CampaignNpcEntity::class,
        CampaignMemberEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GrimoireDatabase : RoomDatabase() {

    abstract fun grimoireDao(): GrimoireDao

    companion object {
        @Volatile
        private var INSTANCE: GrimoireDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GrimoireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GrimoireDatabase::class.java,
                    "grimoire_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(GrimoireDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class GrimoireDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.grimoireDao())
                }
            }
        }

        suspend fun populateDatabase(dao: GrimoireDao) {
            // Populate initial Character
            dao.insertCharacter(
                CharacterEntity(
                    id = 1L,
                    name = "Thalric Ironfoot",
                    characterClass = "Guerreiro",
                    subClass = "Mago",
                    level = 5,
                    subClassLevel = 2,
                    race = "Anão",
                    background = "Soldado",
                    currentHp = 45,
                    maxHp = 58,
                    tempHp = 0,
                    ac = 16,
                    initiative = 2,
                    speed = "9m",
                    str = 18,
                    dex = 14,
                    con = 16,
                    intScore = 18,
                    wis = 10,
                    cha = 8,
                    conditionsJson = "[\"Cego (1 rd)\", \"Concentração\"]",
                    slot1Current = 2,
                    slot1Max = 4,
                    slot2Current = 1,
                    slot2Max = 2,
                    slot3Current = 2,
                    slot3Max = 3,
                    slot4Current = 1,
                    slot4Max = 2,
                    activeConcentration = "Invocar Elemental",
                    cp = 120,
                    sp = 45,
                    ep = 0,
                    gp = 250,
                    pp = 10,
                    currentWeightKg = 85.0f,
                    maxWeightKg = 150.0f
                )
            )

            // Populate Spells
            val initialSpells = listOf(
                SpellEntity(
                    name = "Bola de Fogo",
                    level = 3,
                    school = "Evocação",
                    castingTime = "1 Ação",
                    range = "45m (150 pés)",
                    damageOrEffect = "8d6 Fogo",
                    saveOrAttack = "DES CD 15",
                    components = "V, S, M (uma bola minúscula de guano de morcego e enxofre)",
                    description = "Uma centelha brilhante salta da ponta do seu dedo em direção a um ponto que você escolher dentro do alcance e então se expande com um estrondo baixo, explodindo em chamas. Cada criatura em uma esfera de 20 pés de raio centrada no ponto deve realizar um teste de resistência de Destreza. Um alvo sofre 8d6 de dano de fogo se falhar no teste, ou metade desse dano se obtiver sucesso.",
                    higherLevels = "Quando você conjurar esta magia usando um espaço de magia de 4º nível ou superior, o dano aumenta em 1d6 para cada nível do espaço acima do 3º.",
                    isPrepared = true,
                    isConcentration = false,
                    isRitual = false
                ),
                SpellEntity(
                    name = "Escudo Arcano",
                    level = 1,
                    school = "Abjuração",
                    castingTime = "1 Reação",
                    range = "Pessoal",
                    damageOrEffect = "+5 CA",
                    saveOrAttack = "",
                    components = "V, S",
                    description = "Uma barreira invisível de força mágica surge para proteger você. Até o início do seu próximo turno, você tem um bônus de +5 na CA, incluindo contra o ataque desencadeador.",
                    higherLevels = "",
                    isPrepared = true,
                    isConcentration = false,
                    isRitual = false
                ),
                SpellEntity(
                    name = "Mísseis Mágicos",
                    level = 1,
                    school = "Evocação",
                    castingTime = "1 Ação",
                    range = "36m",
                    damageOrEffect = "3x (1d4 + 1) Energia",
                    saveOrAttack = "Acerto automático",
                    components = "V, S",
                    description = "Você cria três dardos brilhantes de força mágica. Cada dardo atinge uma criatura à sua escolha que você possa ver dentro do alcance. Um dardo causa 1d4 + 1 de dano de energia ao seu alvo.",
                    higherLevels = "Quando você conjura esta magia usando um espaço de magia de 2º nível ou superior, a magia cria mais um dardo para cada nível do espaço acima do 1º.",
                    isPrepared = true,
                    isConcentration = false,
                    isRitual = false
                ),
                SpellEntity(
                    name = "Raio de Gelo",
                    level = 0,
                    school = "Evocação",
                    castingTime = "1 Ação",
                    range = "18m",
                    damageOrEffect = "1d8 Frio",
                    saveOrAttack = "Ataque à Distância",
                    components = "V, S",
                    description = "Um feixe frígido de luz azul-branca atinge uma criatura dentro do alcance. Faça um ataque à distância com simpatia mágica contra o alvo. Se atingir, ele sofre 1d8 de dano de frio e seu deslocamento é reduzido em 3 metros até o início do seu próximo turno.",
                    higherLevels = "",
                    isPrepared = true,
                    isConcentration = false,
                    isRitual = false
                ),
                SpellEntity(
                    name = "Bênção",
                    level = 1,
                    school = "Encantamento",
                    castingTime = "1 Ação",
                    range = "9m",
                    damageOrEffect = "+1d4 em ataques/testes",
                    saveOrAttack = "",
                    components = "V, S, M (um borrifo de água benta)",
                    description = "Você abençoa até três criaturas à sua escolha dentro do alcance. Sempre que um alvo fizer uma jogada de ataque ou um teste de resistência antes da magia terminar, o alvo pode rolar um d4 e adicionar o número rolado à jogada.",
                    higherLevels = "Quando você conjura esta magia usando um espaço de magia de 2º nível ou superior, você pode afetar uma criatura adicional para cada nível do espaço acima do 1º.",
                    isPrepared = true,
                    isConcentration = true,
                    isRitual = false
                ),
                SpellEntity(
                    name = "Curar Ferimentos",
                    level = 1,
                    school = "Evocação",
                    castingTime = "1 Ação",
                    range = "Toque",
                    damageOrEffect = "1d8 + Mod",
                    saveOrAttack = "",
                    components = "V, S",
                    description = "Uma criatura que você tocar recupera um número de pontos de vida igual a 1d8 + seu modificador de habilidade de conjuração.",
                    higherLevels = "Quando você conjura esta magia usando um espaço de magia de 2º nível ou superior, a cura aumenta em 1d8 para cada nível do espaço acima do 1º.",
                    isPrepared = true,
                    isConcentration = false,
                    isRitual = false
                )
            )
            dao.insertSpells(initialSpells)

            // Populate Items
            val initialItems = listOf(
                ItemEntity(
                    name = "Espada Longa +1",
                    category = "Equipado",
                    weightKg = 1.5f,
                    quantity = 1,
                    valueGp = 500,
                    rarity = "Incomum",
                    requiresAttunement = true,
                    isAttuned = true,
                    attunedTo = "Thalric Ironfoot",
                    properties = "Versatile (1d10) • Mágica (+1)",
                    description = "Uma espada perfeitamente balanceada forjada em aço anão antigo com runas de corte.",
                    lore = "Forjada nas forjas de ferro das montanhas profundas.",
                    iconType = "swords"
                ),
                ItemEntity(
                    name = "Flame Tongue Longsword",
                    category = "Equipado",
                    weightKg = 1.5f,
                    quantity = 1,
                    valueGp = 5000,
                    rarity = "Raro",
                    requiresAttunement = true,
                    isAttuned = true,
                    attunedTo = "Thalric Ironfoot",
                    properties = "1d8 slashing + 2d6 fire • Versatile (1d10)",
                    description = "You can use a bonus action to speak this magic sword's command word, causing flames to erupt from the blade. These flames shed bright light in a 40-foot radius and dim light for an additional 40 feet.",
                    lore = "Forged in the brass city, this blade hungers for the dry air of the material plane.",
                    iconType = "fire_sword",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDww0tAa5KNArM1eI9LmNUEzxUmYnOX01jA-0Xswg4bwgxCJrW0dhNP5JCxEUL90ZODuSgDM1sqZViWtcEzC43yXVEuY_Dp1k13tznZZ8ere2OcVruXw6OQJKUmq7ChFU06_K6lD8mf3-JESlEbNJlHW4Cnzg7dgVakZiH88oDzU037fqmeKUqQfQk5FITcNtVJfOKgGbqOa-Yng3KRwVGpuStLpDeKIX1rE2nAN8u1f-l1KcMw9Q"
                ),
                ItemEntity(
                    name = "Escudo do Sentinela",
                    category = "Equipado",
                    weightKg = 3.0f,
                    quantity = 1,
                    valueGp = 300,
                    rarity = "Incomum",
                    requiresAttunement = true,
                    isAttuned = true,
                    attunedTo = "Thalric Ironfoot",
                    properties = "+2 CA • Vantagem em Iniciativa",
                    description = "Um escudo reluzente adornado com o olho de uma coruja sentinela.",
                    lore = "Concede vigilância sobrenatural e tempo de reação aguçado ao portador.",
                    iconType = "shield"
                ),
                ItemEntity(
                    name = "Saco de Dormir",
                    category = "Mochila",
                    weightKg = 3.5f,
                    quantity = 1,
                    valueGp = 2,
                    rarity = "Comum",
                    properties = "Equipamento de viagem",
                    description = "Um saco de dormir confortável de lã prensada impermeável.",
                    iconType = "bed"
                ),
                ItemEntity(
                    name = "Poção de Cura",
                    category = "Consumíveis",
                    weightKg = 0.5f,
                    quantity = 3,
                    valueGp = 50,
                    rarity = "Comum",
                    properties = "2d4 + 2 HP",
                    description = "Um líquido vermelho borbulhante que fecha ferimentos quando ingerido.",
                    iconType = "potion"
                )
            )
            dao.insertItems(initialItems)

            // Populate Roll History
            val initialRolls = listOf(
                RollHistoryEntity(label = "Ataque Espada", totalResult = 15, dieRoll = 11, modifier = 4),
                RollHistoryEntity(label = "Iniciativa", totalResult = 12, dieRoll = 10, modifier = 2)
            )
            initialRolls.forEach { dao.insertRoll(it) }

            // Populate Initial Notes / Handouts
            val initialNotes = listOf(
                NoteEntity(
                    title = "Mapa das Terras Altas",
                    content = "Mapa cartográfico detalhado das regiões montanhosas, relevos e desfiladeiros antigos.",
                    date = "Compartilhado há 2 horas",
                    tagsJson = "[\"Mapa\", \"Localização\"]",
                    isHandout = true,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCNj15qKh-GbO2WamAgqsMTo0ndkR1FiLRipOm5BXr5WavMfU1gFEzTf0GalNEDnyd2RCwbBHs48TRp3AQC2zuxgQL8sHfrbGFOQKzVzPgEc85C4AmX9swPoLzcRkZ8kd6D9pCinoWrogMUnxyMM2DW_IS1-o3CpOUqHu8EmKkvVwlZxKHGrE8AWFEUmwSnNfqiLj_86-7Vr8HecuihcWI3Ad7xm_5v18X4nZLRs5DJB2Qf3YiWkQ"
                ),
                NoteEntity(
                    title = "Carta de Selene",
                    content = "\"Encontrem-me nas ruínas sob a lua nova. Os cultistas despertaram algo que estava adormecido há eras, e temo que o véu esteja se rompendo...\"",
                    date = "Documento da Mesa",
                    tagsJson = "[\"Documento\", \"Pista\"]",
                    isHandout = true
                ),
                NoteEntity(
                    title = "O Enigma da Porta de Bronze",
                    content = "A porta exige sangue real para abrir. Kael tentou forçar a fechadura e quase perdeu a mão esquerda para uma armadilha rúnica evocation.",
                    date = "14 Out, 2023",
                    tagsJson = "[\"Pista\", \"Localização\"]",
                    isHandout = false
                ),
                NoteEntity(
                    title = "Mercadorias de Vala",
                    content = "Compramos poções de cura maior por 50gp cada. Vala parece estar escondendo suprimentos no porão, ouvi barulhos estranhos vindo de lá durante a noite.",
                    date = "12 Out, 2023",
                    tagsJson = "[\"NPC\"]",
                    isHandout = false
                )
            )
            dao.insertNotes(initialNotes)

            // Populate Campaigns
            val campaignId1 = dao.insertCampaign(
                CampaignEntity(
                    id = 1L,
                    title = "Sombra de Ravenloft",
                    synopsis = "Mistos de névoa e pesadelos cobrem o reino de Barovia. Sob o olhar atento do Conde Strahd, a expedição busca sobreviver nas terras amaldiçoadas.",
                    system = "D&D 5e",
                    inviteCode = "RAVEN-5E",
                    bannerUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuADaxuvw5o625ChcLC8EKs4Cgj5U0ULxdeoRWf-tCdLunTP9aioasW6SNXiYq4V6lyjTnUtISr5xzLhSP2bcRNTwFF5M_uHsF_OgQ2IZFS07L5UPmcGOicGzf4OOmBK4P9rKhhesfamoBO36angtBd3kfkWyrZBpZ_mamAU2H7MhUPDsNf6tfJUxH_siQZPtaKba7EgOTVxEdlBKDbDmnUdkyUsistgrq5N9YR76HFuY2aBVPtPVlpieg",
                    dmName = "Archmage Valerius",
                    isLive = true,
                    currentSessionTitle = "Sessão 8: O Castelo Ravenloft"
                )
            )

            val campaignId2 = dao.insertCampaign(
                CampaignEntity(
                    id = 2L,
                    title = "O Códice de Cinzas",
                    synopsis = "As chamas divinas do império foram extintas. Apenas cinzas caem dos céus, trazendo horrores esquecidos. O objetivo é recuperar fragmentos do grimório primordial.",
                    system = "D&D 5e",
                    inviteCode = "CODEX-99",
                    bannerUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAo_VoJihaeq4sskMVzAzhvw3a-Z0iV2W-5FfnreKGR_eYq8B6Ja7lYZW9IJGvf-MXsskKOrLGdx-rCqTINSaWcHUubdsojgR4CB1NoMePnZBFS6mpoCKoKx16J1iBi-pBgCCLL6WC6szu1sdNSS2tP1NHSNmjgn0g-IPwBQEWJEYJmtx0VR31Hhy93I1hJvtToGJFSJrFQ0ibp-kFuYNvDLz5Nz3VnCiAokF1NRKhuugNHu-5MVFbSXg",
                    dmName = "Mestre Valerius",
                    isLive = false,
                    currentSessionTitle = "Sessão 3: As Ruínas do Templo"
                )
            )

            // Populate Campaign Sessions
            dao.insertSession(
                CampaignSessionEntity(
                    campaignId = campaignId1,
                    sessionNumber = 8,
                    title = "O Festim dos Vampiros",
                    dateText = "Hoje (Ao Vivo)",
                    summary = "O grupo adentrou a sala do trono e confrontou as criações de Strahd. Kaelen desferiu um golpe crítico mas a névoa cobriu o salão.",
                    dmNotes = "O baú secreto sob a tapeçaria contém a Relíquia do Sol. Lembrar de pedir teste de Percepção Passiva 16.",
                    publicNotes = "O grupo encontrou um pergaminho antigo com símbolos de proteção contra mortos-vivos."
                )
            )
            dao.insertSession(
                CampaignSessionEntity(
                    campaignId = campaignId1,
                    sessionNumber = 7,
                    title = "Emboscada nas Névoas",
                    dateText = "12 Out, 2023",
                    summary = "Lutaram contra os lobos de Barovia e encontraram abrigo na igreja abandonada.",
                    dmNotes = "Lorde Strahd observou o combate disfarçado no alto da torre.",
                    publicNotes = "Adquiriram 3 poções de cura com o padre da vila."
                )
            )

            // Populate Campaign NPCs
            dao.insertNpc(
                CampaignNpcEntity(
                    campaignId = campaignId1,
                    name = "Lorde Strahd von Zarovich",
                    roleOrTitle = "Conde de Barovia",
                    challengeRating = "15",
                    hp = 144,
                    maxHp = 144,
                    ac = 16,
                    speed = "9m, voo 9m",
                    str = 18,
                    dex = 18,
                    con = 18,
                    intScore = 20,
                    wis = 15,
                    cha = 18,
                    actionsJson = "[\"Garrar (Ataque: +9, 2d8+4 cortante + 4d6 necrótico)\", \"Morder (+9, 1d6+4 perfurante + 3d6 necrótico)\", \"Regeneração (20 HP por turno)\"]",
                    spellsJson = "[\"Bola de Fogo (3º nível)\", \"Invisibilidade (2º nível)\", \"Domínio (5º nível)\"]",
                    description = "Mestre incontestável das terras amaldiçoadas. Traja vestes nobres com armadura negra reluzente.",
                    avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAT3_8ksXlQf4rhDiM0rvK9dXh3png-N9YATzbEqzShgiG0iMuZHGPLFJOq8X8vybRZy3maIfOq-wYo9bhof07uoB9t7sKtqccWlcIfV80N7-L0fWNtwi5wkFGn4H8GS-JQG8Bvp1ihZa8VcjQku5I9x-IOs14GFb3ApyxDFWtvE0gweFpkXRBb0qYPiiBDZqk7lAiwhfQxwZPkMe0LVWJ9DZXyNBRiM6p9Wj42HsYJY-uvQmfCpg5oVw",
                    isHostile = true
                )
            )
            dao.insertNpc(
                CampaignNpcEntity(
                    campaignId = campaignId1,
                    name = "Ismark Kolyanovich",
                    roleOrTitle = "Nobre da Vila",
                    challengeRating = "3",
                    hp = 58,
                    maxHp = 58,
                    ac = 17,
                    speed = "9m",
                    str = 16,
                    dex = 13,
                    con = 14,
                    intScore = 12,
                    wis = 11,
                    cha = 13,
                    actionsJson = "[\"Ataque Múltiplo (2x Espada Grande: +5, 2d6+3 cortante)\"]",
                    spellsJson = "[]",
                    description = "Nobre honrado conhecido como 'Ismark o Menor'. Busca proteger sua irmã Ireena.",
                    avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC2Kh4l2nWZimBJacbbVBZk7FDLG1Ol4IP8jdTPjoJO0ZqaCYn_qUQTLkgGbebX6jn4RxolKqdpaDqaAot9dkvKr9zhtMxgBaii7sR7_-AScmbHaDIXrR-69ecR1tXe0bUnhgU5d_HXX_mPzZmBNxB6B1NhwV2A2Znc4RRDVVPStPKqUdfo4i3r6-kHK1vp3DTsjiYkQA4utvrfZNlBFjgRPjz2_ahspD3YmYXjzLcF4QIu-E_ERXgt2g",
                    isHostile = false
                )
            )

            // Populate Campaign Members
            dao.insertMembers(
                listOf(
                    CampaignMemberEntity(
                        campaignId = campaignId1,
                        playerName = "Paulo",
                        characterName = "Thalric Ironfoot",
                        characterClass = "Guerreiro 5 / Mago 2",
                        level = 7,
                        currentHp = 45,
                        maxHp = 58,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDZ1pDSTUqrtLn175gz-34SeLnER-s2Tiy6EQTff-r9-hhv8ghZIziBIHJv70_luIy0-XeyKNyohEowtBbAzebS-3rAZcJWKmQrMzbC6GZ_W2hi2zUbAy42YNsiBJZh_e-Xj2n1TsIT7GXLEW2M3lNInxiN0F7XrRZQA6DO5fMRP3IhYEjpMERQm6HXksuTncjilDeNMgCIDBgH0M3lEarxTKvWkz4Ymd-ZMpS5JFCMsliVzI-shDF6dg",
                        role = "PLAYER"
                    ),
                    CampaignMemberEntity(
                        campaignId = campaignId1,
                        playerName = "Mestre Valerius",
                        characterName = "Dungeon Master",
                        characterClass = "Mestre da Mesa",
                        level = 20,
                        currentHp = 999,
                        maxHp = 999,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDG1yuKm3ShRA4LbOvbvhg-pU2ehjzTwtzlpeLLTvrWitfK3eI8-Wxf7KeyCSlMzJGaXTFqb_R1IHKJYo0lizlgE8jaUhyR-tsPyHW8Dow_YWe_segRA0f9LcC8r5tx00Q3cMcprMKD8xdGJj-YfozZKv9Ri36zkjP4qmxQso3Udvw1YJxbU3stSCtL-PgK-JMCRWtAx3dLMZol3-IsV_nAU-aE9pvxU4JNZ4A2-CeK774KlKLiF5IeAw",
                        role = "DM"
                    ),
                    CampaignMemberEntity(
                        campaignId = campaignId1,
                        playerName = "Lucas",
                        characterName = "Thorne",
                        characterClass = "Paladino 6",
                        level = 6,
                        currentHp = 52,
                        maxHp = 52,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAx4ISzuLUplhzHTOozEZtSyS9oEMFXX2IXgPHtWrWO9EbhhO9V13T4jnGCPby2mCrscZWpgnJ3-NUT4z1eSVzYbhkbhagmau31wGq3kRsuH_7G0Sudi1Ee1JsixD251GRICgZp_ehrm8gdmP1Iq2Leaq-dbDVR-a5EgrM-CGEPVl4EQHOu26zulM095ZYGT_9PdRd2DbWg-YzeOOqH9dk9GbnPHwwZpxn-bPmuvwOMweRHuCkHWTjVsA",
                        role = "PLAYER"
                    ),
                    CampaignMemberEntity(
                        campaignId = campaignId1,
                        playerName = "Beatriz",
                        characterName = "Lilith",
                        characterClass = "Bruxa 6",
                        level = 6,
                        currentHp = 38,
                        maxHp = 42,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCFwVg1NHuatNLizY8k2C5KpZTeEM7AOPeYQMPWqQMofD6uqOtwj1gwEbu4LZbuLdn3_xSktJ64pT61Wxn2OnA-1JclxXQeX7zcqWnkUJL7RGnu6a3urGTWZA25DrEXqHi7N8cOizQA7E_31HxvR_ssbuBOVy8XA4xiyRM5ViLoxLfDo_zz1SOLDDoF7zTeYlkIPD0z6hdjEhC_UXsGUgpaluXZUucktQtvCOYZq_q0z7MToecHnuu5yQ",
                        role = "PLAYER"
                    )
                )
            )
        }
    }
}
