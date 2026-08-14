package com.example.domain.rules

data class SourcebookInfo(
    val id: String,
    val title: String,
    val year: String,
    val summaryPt: String,
    val summaryEn: String,
    val detailsPt: String,
    val detailsEn: String,
    val keyFeaturesPt: List<String>,
    val keyFeaturesEn: List<String>
)

data class RuleTopic(
    val titlePt: String,
    val titleEn: String,
    val sourcebookId: String,
    val categoryPt: String,
    val categoryEn: String,
    val contentPt: String,
    val contentEn: String
)

object DndExpansionRules {

    // --- 1. Sourcebooks Knowledge Base ---
    val SOURCEBOOKS = listOf(
        SourcebookInfo(
            id = "xgte",
            title = "Xanathar's Guide to Everything",
            year = "2017",
            summaryPt = "O primeiro grande suplemento do 5e. Traz mais de 25 subclasses, regras de downtime, usos detalhados de ferramentas, armadilhas e regras opcionais.",
            summaryEn = "The first major 5e expansion. Features over 25 subclasses, downtime activities, detailed tool uses, traps, and optional rules.",
            detailsPt = "Xanathar's Guide to Everything expande as opções do Guia do Mestre e Livro do Jogador com sistemas estruturados para atividades entre aventuras (downtime), regras práticas para o uso de ferramentas de artesãos, armadilhas simples e complexas, e magias famosas como Toll the Dead e Absorb Elements.",
            detailsEn = "Xanathar's Guide to Everything expands DM and Player options with structured systems for downtime activities, practical rules for artisan tools, simple and complex traps, and iconic spells like Toll the Dead and Absorb Elements.",
            keyFeaturesPt = listOf(
                "25+ Subclasses (Hexblade, Swashbuckler, Gloom Stalker, Forge Domain, etc.)",
                "Atividades de Downtime (Comprar Itens Mágicos, Pesquisa, Lutas em Arena, Crimes)",
                "Usos Práticos de Ferramentas (Alquimia, Cervejeiro, Kit de Disfarce, Ferramentas de Ladrão)",
                "Regras Opcionais: Quedas, Sono em Armadura, Nós, Armadilhas Complexas"
            ),
            keyFeaturesEn = listOf(
                "25+ Subclasses (Hexblade, Swashbuckler, Gloom Stalker, Forge Domain, etc.)",
                "Downtime Activities (Buying Magic Items, Research, Pit Fighting, Crime)",
                "Practical Tool Usages (Alchemist's Supplies, Brewer's Supplies, Thieves' Tools)",
                "Optional Rules: Falling, Sleeping in Armor, Knots, Complex Traps"
            )
        ),
        SourcebookInfo(
            id = "tcoe",
            title = "Tasha's Cauldron of Everything",
            year = "2020",
            summaryPt = "Livro disruptivo que introduziu a classe Artífice, Personalização de Origem, Características Opcionais de Classe, Tatuagens Mágicas e Ajudantes (Sidekicks).",
            summaryEn = "Disruptive sourcebook introducing the Artificer class, Customizing Your Origin, Optional Class Features, Magical Tattoos, and Sidekicks.",
            detailsPt = "Tasha's Cauldron of Everything descentralizou os bônus de atributos raciais com a 'Customização de Origem', reescreveu e expandiu as habilidades base de todas as classes com 'Características Opcionais de Classe', e formalizou a classe Artífice para todas as campanhas.",
            detailsEn = "Tasha's Cauldron of Everything decoupled racial ability scores with 'Customizing Your Origin', expanded base class features across all classes, and unlocked the Artificer class globally.",
            keyFeaturesPt = listOf(
                "Classe Artífice completa (Alquimista, Armeiro, Artilheiro, Ferreiro de Batalha)",
                "Customização de Origem (Ajuste livre de atributos e idiomas raciais)",
                "Características Opcionais de Classe (Novos estilos de luta, recursos de conjuração)",
                "Tatuagens Mágicas, Ajudantes (Sidekicks) e Personalização de Magias"
            ),
            keyFeaturesEn = listOf(
                "Full Artificer Class (Alchemist, Armorer, Artillerist, Battle Smith)",
                "Customizing Your Origin (Flexible racial ability scores and languages)",
                "Optional Class Features (New fighting styles, channel divinity options)",
                "Magical Tattoos, Sidekicks, and Customizing Spell Visuals"
            )
        ),
        SourcebookInfo(
            id = "motm",
            title = "Mordenkainen Presents: Monsters of the Multiverse",
            year = "2022",
            summaryPt = "Consolidação definitiva do 5e. Reescreve mais de 30 raças jogáveis e mais de 250 monstros com blocos de estatísticas padronizados.",
            summaryEn = "Definitive 5e consolidation. Rewrites over 30 playable races and 250+ monsters with standardized stat blocks.",
            detailsPt = "Monsters of the Multiverse padronizou as opções raciais do D&D 5e permitindo escolher livremente os aumentos de atributos (+2/+1 ou +1/+1/+1) e unificou habilidades especiais como feitiços inatos para usar qualquer espaço de magia disponível.",
            detailsEn = "Monsters of the Multiverse standardized 5e character options allowing flexible ability score increases (+2/+1 or +1/+1/+1) and unified innate spellcasting mechanics.",
            keyFeaturesPt = listOf(
                "30+ Raças Atualizadas (Aasimar, Genasi, Changeling, Warforged, Tabaxi, Fairy, Harengon)",
                "Atributos Raciais Flexíveis por padrão",
                "250+ Monstros e Criaturas com blocos de estatísticas redesenhados"
            ),
            keyFeaturesEn = listOf(
                "30+ Updated Playable Races (Aasimar, Genasi, Changeling, Warforged, Tabaxi, Fairy, Harengon)",
                "Standardized Flexible Racial Ability Scores",
                "250+ Redesigned Monster Stat Blocks"
            )
        ),
        SourcebookInfo(
            id = "vrgtr",
            title = "Van Richten's Guide to Ravenloft",
            year = "2021",
            summaryPt = "Guia de terror e fantasia sombria. Traz os Presentes Sombrios (Dark Gifts), Linhagens (Dhampir, Hexblood, Reborn) e regras de Medo e Estresse.",
            summaryEn = "Dark fantasy and horror guide. Features Dark Gifts, Lineages (Dhampir, Hexblood, Reborn), and Fear/Stress mechanics.",
            detailsPt = "Van Richten's Guide to Ravenloft introduz mecânicas portáteis para qualquer campanha de mistério ou terror, incluindo os Presentes Sombrios que concedem grandes poderes acompanhados de uma maldição ancestral.",
            detailsEn = "Van Richten's Guide to Ravenloft introduces portable horror mechanics including Dark Gifts which grant formidable powers tied to dark bargains.",
            keyFeaturesPt = listOf(
                "Linhagens de Horror (Dhampir, Sangue-Bruxo/Hexblood, Renascido/Reborn)",
                "Presentes Sombrios (Echoing Soul, Living Shadow, Mist Walker, Symbiotic Being)",
                "Regras e Trilhas de Medo, Estresse e Horror para Mestres e Jogadores"
            ),
            keyFeaturesEn = listOf(
                "Horror Lineages (Dhampir, Hexblood, Reborn)",
                "Dark Gifts (Echoing Soul, Living Shadow, Mist Walker, Symbiotic Being)",
                "Fear, Stress, and Horror Rule Mechanics"
            )
        ),
        SourcebookInfo(
            id = "erftlw",
            title = "Eberron: Rising from the Last War",
            year = "2019",
            summaryPt = "O mundo de Eberron introduz o Artífice, as Marcas do Dragão (Dragonmarks), Patronos de Grupo e raças icônicas como Warforged e Changeling.",
            summaryEn = "The world of Eberron introduces the Artificer, Dragonmarks, Group Patrons, and iconic races like Warforged and Changeling.",
            detailsPt = "Eberron funde magia e tecnologia arcanotécnica. Apresenta o Artífice como classe oficial e permite criar personagens vinculados às Grandes Casas Marcadas pelo Dragão.",
            detailsEn = "Eberron bridges magic and arcantech technology. Introduces the Artificer class and Dragonmarked Houses.",
            keyFeaturesPt = listOf(
                "Classe Artífice e Infusões Arcanas",
                "Raças Únicas: Warforged (Forjado de Guerra), Changeling, Kalashtar, Shifter",
                "12 Variações de Marcas do Dragão (Mark of Making, Warding, Shadow, etc.)",
                "Patronos de Grupo (Guildas, Universidades, Forças Militares)"
            ),
            keyFeaturesEn = listOf(
                "Artificer Class and Arcane Infusions",
                "Unique Races: Warforged, Changeling, Kalashtar, Shifter",
                "12 Dragonmark Variations (Mark of Making, Warding, Shadow, etc.)",
                "Group Patrons (Guilds, Universities, Military Forces)"
            )
        )
    )

    // --- 2. Expanded Class List (including Artificer) ---
    val CLASSES = listOf(
        "Artífice (Artificer)",
        "Bárbaro (Barbarian)",
        "Bardo (Bard)",
        "Bruxo (Warlock)",
        "Clérigo (Cleric)",
        "Druida (Druid)",
        "Feiticeiro (Sorcerer)",
        "Guerreiro (Fighter)",
        "Ladino (Rogue)",
        "Mago (Wizard)",
        "Monge (Monk)",
        "Paladino (Paladin)",
        "Patrulheiro (Ranger)"
    )

    // --- 3. Expanded Subclass List by Class ---
    val SUBCLASSES_BY_CLASS: Map<String, List<String>> = mapOf(
        "Artífice (Artificer)" to listOf(
            "Alquimista (Alchemist) [TCoE]",
            "Armeiro (Armorer) [TCoE]",
            "Artilheiro (Artillerist) [TCoE]",
            "Ferreiro de Batalha (Battle Smith) [TCoE]"
        ),
        "Bárbaro (Barbarian)" to listOf(
            "Caminho do Furioso (Berserker) [PHB]",
            "Caminho do Guerreiro Totêmico (Totem Warrior) [PHB]",
            "Caminho do Guardião Ancestral (Ancestral Guardian) [XGtE]",
            "Caminho do Arauto da Tempestade (Storm Herald) [XGtE]",
            "Caminho do Zelote (Zealot) [XGtE]",
            "Caminho da Magia Selvagem (Wild Magic) [TCoE]",
            "Caminho da Besta (Beast) [TCoE]"
        ),
        "Bardo (Bard)" to listOf(
            "Colégio do Conhecimento (Lore) [PHB]",
            "Colégio da Bravura (Valor) [PHB]",
            "Colégio do Glamour [XGtE]",
            "Colégio das Espadas (Swords) [XGtE]",
            "Colégio dos Sussurros (Whispers) [XGtE]",
            "Colégio da Criação (Creation) [TCoE]",
            "Colégio da Eloquência (Eloquence) [TCoE]"
        ),
        "Bruxo (Warlock)" to listOf(
            "O Corruptor (Fiend) [PHB]",
            "O Arqui-fada (Archfey) [PHB]",
            "O Grande Antigo (Great Old One) [PHB]",
            "Lâmina Sinistra (The Hexblade) [XGtE]",
            "O Celestial [XGtE]",
            "As Profundezas (Fathomless) [TCoE]",
            "O Gênio (Genie) [TCoE]"
        ),
        "Clérigo (Cleric)" to listOf(
            "Domínio da Vida (Life) [PHB]",
            "Domínio da Luz (Light) [PHB]",
            "Domínio do Trovão (Tempest) [PHB]",
            "Domínio da Enganação (Trickery) [PHB]",
            "Domínio da Forja (Forge) [XGtE]",
            "Domínio do Túmulo (Grave) [XGtE]",
            "Domínio da Ordem (Order) [TCoE]",
            "Domínio da Paz (Peace) [TCoE]",
            "Domínio do Crepúsculo (Twilight) [TCoE]"
        ),
        "Druida (Druid)" to listOf(
            "Círculo da Terra (Land) [PHB]",
            "Círculo da Lua (Moon) [PHB]",
            "Círculo dos Sonhos (Dreams) [XGtE]",
            "Círculo do Pastor (Shepherd) [XGtE]",
            "Círculo dos Esporos (Spores) [TCoE]",
            "Círculo das Estrelas (Stars) [TCoE]",
            "Círculo do Fogo Selvagem (Wildfire) [TCoE]"
        ),
        "Guerreiro (Fighter)" to listOf(
            "Campeão (Champion) [PHB]",
            "Mestre de Batalha (Battle Master) [PHB]",
            "Cavaleiro Arcano (Eldritch Knight) [PHB]",
            "Arqueiro Arcano (Arcane Archer) [XGtE]",
            "Cavaleiro (Cavalier) [XGtE]",
            "Samurai [XGtE]",
            "Guerreiro Psíquico (Psi Warrior) [TCoE]",
            "Cavaleiro Rúnico (Rune Knight) [TCoE]"
        ),
        "Ladino (Rogue)" to listOf(
            "Ladrão (Thief) [PHB]",
            "Assassino (Assassin) [PHB]",
            "Trapaceiro Arcano (Arcane Trickster) [PHB]",
            "Inquisitivo (Inquisitive) [XGtE]",
            "Estrategista (Mastermind) [XGtE]",
            "Batedor (Scout) [XGtE]",
            "Esgrimista (Swashbuckler) [XGtE]",
            "Fantasma (Phantom) [TCoE]",
            "Lâmina Psíquica (Soulknife) [TCoE]"
        ),
        "Mago (Wizard)" to listOf(
            "Escola de Evocação [PHB]",
            "Escola de Abjuração [PHB]",
            "Escola de Adivinhação [PHB]",
            "Escola de Ilusão [PHB]",
            "Magia de Guerra (War Magic) [XGtE]",
            "Cantor das Lâminas (Bladesinging) [TCoE]",
            "Ordem dos Escribas (Order of Scribes) [TCoE]"
        ),
        "Monge (Monk)" to listOf(
            "Caminho da Mão Aberta [PHB]",
            "Caminho da Sombra [PHB]",
            "Caminho do Kensei [XGtE]",
            "Caminho da Alma do Sol [XGtE]",
            "Caminho do Mestre Bêbado [XGtE]",
            "Caminho da Misericórdia (Mercy) [TCoE]",
            "Caminho do Eu Astral (Astral Self) [TCoE]"
        ),
        "Paladino (Paladin)" to listOf(
            "Juramento de Devoção [PHB]",
            "Juramento dos Anciões [PHB]",
            "Juramento de Vingança [PHB]",
            "Juramento de Redenção [XGtE]",
            "Juramento de Conquista [XGtE]",
            "Juramento da Glória (Glory) [TCoE]",
            "Juramento dos Vigilantes (Watchers) [TCoE]"
        ),
        "Patrulheiro (Ranger)" to listOf(
            "Conquistador do Averno [PHB]",
            "Perseguidor das Sombras (Gloom Stalker) [XGtE]",
            "Andarilho do Horizonte (Horizon Walker) [XGtE]",
            "Matador de Monstros (Monster Slayer) [XGtE]",
            "Nômade das Fadas (Fey Wanderer) [TCoE]",
            "Mestre do Enxame (Swarmkeeper) [TCoE]"
        )
    )

    // --- 4. Expanded Playable Races & Lineages (MotM, ERftLW, VRGtR) ---
    val RACES = listOf(
        "Humano (Human)",
        "Elfo (Elf)",
        "Anão (Dwarf)",
        "Halfling",
        "Draconato (Dragonborn)",
        "Gnomo (Gnome)",
        "Meio-Elfo (Half-Elf)",
        "Meio-Orc (Half-Orc)",
        "Tiefling",
        // Eberron (ERftLW)
        "Forjado de Guerra (Warforged) [Eberron/MotM]",
        "Changeling (Metamorfo) [Eberron/MotM]",
        "Kalashtar [Eberron]",
        "Shifter (Transforma) [Eberron/MotM]",
        // Van Richten's (VRGtR)
        "Dhampir (Linhagem Vampírica) [VRGtR]",
        "Sangue-Bruxo (Hexblood) [VRGtR]",
        "Renascido (Reborn) [VRGtR]",
        // Multiverse (MotM)
        "Aasimar [MotM]",
        "Fada (Fairy) [MotM]",
        "Harengon (Povo Coelho) [MotM]",
        "Genasi do Ar (Air Genasi) [MotM]",
        "Genasi da Terra (Earth Genasi) [MotM]",
        "Genasi do Fogo (Fire Genasi) [MotM]",
        "Genasi da Água (Water Genasi) [MotM]",
        "Tabaxi [MotM]",
        "Golias (Goliath) [MotM]",
        "Firbolg [MotM]",
        "Gnomo das Profundezas (Svirfneblin) [MotM]",
        "Duergar [MotM]",
        "Eladrin [MotM]",
        "Elfo do Mar (Sea Elf) [MotM]",
        "Bugbear [MotM]",
        "Centauro (Centaur) [MotM]",
        "Githyanki [MotM]",
        "Githzerai [MotM]",
        "Goblin [MotM]",
        "Kenku [MotM]",
        "Kobold [MotM]",
        "Homem-Lagarto (Lizardfolk) [MotM]",
        "Minotauro (Minotaur) [MotM]",
        "Orc [MotM]",
        "Sátiro (Satyr) [MotM]",
        "Tortle [MotM]",
        "Tritão (Triton) [MotM]",
        "Yuan-ti [MotM]"
    )

    // --- 5. Backgrounds ---
    val BACKGROUNDS = listOf(
        "Acolito (Acolyte)",
        "Artesão de Guilda (Guild Artisan)",
        "Artista (Entertainer)",
        "Charlatão (Charlatan)",
        "Criminoso (Criminal)",
        "Eremita (Hermit)",
        "Herói do Povo (Folk Hero)",
        "Marinheiro (Sailor)",
        "Nobre (Noble)",
        "Órfão (Urchin)",
        "Sábio (Sage)",
        "Soldado (Soldier)",
        "Forasteiro (Outlander)",
        // Expanded
        "Origem Personalizada (Custom Origin) [TCoE]",
        "Assombrado (Haunted One) [VRGtR]",
        "Agente de Casa Marcada (House Agent) [ERftLW]",
        "Gladiador (Gladiator) [PHB]",
        "Cavaleiro (Knight) [PHB]",
        "Arqueólogo (Archaeologist) [ToA]",
        "Antropólogo (Anthropologist) [ToA]"
    )

    // --- 6. Dark Gifts (VRGtR) ---
    val DARK_GIFTS = listOf(
        "Presente Sombrio: Alma Ecoante (Echoing Soul) [VRGtR]",
        "Presente Sombrio: Sussurros Aglomerados (Gathering Whispers) [VRGtR]",
        "Presente Sombrio: Sombra Viva (Living Shadow) [VRGtR]",
        "Presente Sombrio: Caminhante das Névoas (Mist Walker) [VRGtR]",
        "Presente Sombrio: Segunda Pele (Second Skin) [VRGtR]",
        "Presente Sombrio: Ser Simbiótico (Symbiotic Being) [VRGtR]",
        "Presente Sombrio: Toque do Túmulo (Touch of the Tomb) [VRGtR]"
    )
}
