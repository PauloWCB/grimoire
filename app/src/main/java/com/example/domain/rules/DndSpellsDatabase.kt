package com.example.domain.rules

data class PresetSpell(
    val name: String,
    val level: Int,
    val school: String,
    val castingTime: String,
    val range: String,
    val damageOrEffect: String,
    val components: String,
    val description: String
)

object DndSpellsDatabase {
    val ALL_PRESET_SPELLS = listOf(
        PresetSpell(
            name = "Bola de Fogo (Fireball)",
            level = 3,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "45m (150 ft)",
            damageOrEffect = "8d6 Dano de Fogo",
            components = "V, S, M",
            description = "Uma brilhante centelha salta dos seus dedos e se expande numa explosão de fogo com um estrondo rugindo. Cada criatura numa esfera de 6m de raio centrada no ponto deve realizar um teste de resistência de Destreza. Sofre 8d6 dano de fogo se falhar, ou metade se passar."
        ),
        PresetSpell(
            name = "Raio de Fogo (Fire Bolt)",
            level = 0,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "1d10 Dano de Fogo",
            components = "V, S",
            description = "Você arremessa um raio de fogo em uma criatura ou objeto dentro do alcance. Faça um ataque à distância com magia. Se acertar, o alvo sofre 1d10 de dano de fogo."
        ),
        PresetSpell(
            name = "Muralha de Fogo (Wall of Fire)",
            level = 4,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "5d8 Dano de Fogo",
            components = "V, S, M",
            description = "Você cria uma parede de fogo sobre uma superfície sólida dentro do alcance. Você pode fazer a parede de até 18m de comprimento ou um anel de até 6m de diâmetro. Causa 5d8 de dano de fogo."
        ),
        PresetSpell(
            name = "Escudo Mágico (Shield)",
            level = 1,
            school = "Adivinhação/Abjuração",
            castingTime = "1 Reação",
            range = "Pessoal",
            damageOrEffect = "+5 na CA",
            components = "V, S",
            description = "Uma barreira invisível de força mágica surge e o protege. Até o início do seu próximo turno, você tem um bônus de +5 na CA, incluindo contra o ataque que o engatilhou, e não sofre dano de Míssil Mágico."
        ),
        PresetSpell(
            name = "Armadura de Mago (Mage Armor)",
            level = 1,
            school = "Abjuração",
            castingTime = "1 Ação",
            range = "Toque",
            damageOrEffect = "CA Base = 13 + Mod DES",
            components = "V, S, M",
            description = "Você toca uma criatura disposta que não esteja vestindo armadura. A CA base do alvo se torna 13 + seu modificador de Destreza. A magia encerra se o alvo vestir armadura ou se você a dissipar."
        ),
        PresetSpell(
            name = "Míssil Mágico (Magic Missile)",
            level = 1,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "3x (1d4 + 1) Dano de Força",
            components = "V, S",
            description = "Você cria três dardos brilhantes de força mágica. Cada dardo atinge uma criatura à sua escolha que você possa ver dentro do alcance. Um dardo causa 1d4 + 1 de dano de força e atinge automaticamente."
        ),
        PresetSpell(
            name = "Curar Ferimentos (Cure Wounds)",
            level = 1,
            school = "Evocação/Restauração",
            castingTime = "1 Ação",
            range = "Toque",
            damageOrEffect = "1d8 + Mod Conjurador Cura",
            components = "V, S",
            description = "Uma criatura que você tocar recupera um número de pontos de vida igual a 1d8 + seu modificador de habilidade de conjuração. Esta magia não tem efeito em constructos ou mortos-vivos."
        ),
        PresetSpell(
            name = "Palavra Curativa (Healing Word)",
            level = 1,
            school = "Evocação",
            castingTime = "1 Ação Bônus",
            range = "18m (60 ft)",
            damageOrEffect = "1d4 + Mod Conjurador Cura",
            components = "V",
            description = "À medida que você sussurra uma palavra mágica, uma criatura à sua escolha que você possa ver dentro do alcance recupera PV iguais a 1d4 + seu modificador de habilidade de conjuração."
        ),
        PresetSpell(
            name = "Passo Nebuloso (Misty Step)",
            level = 2,
            school = "Conjuração",
            castingTime = "1 Ação Bônus",
            range = "Pessoal",
            damageOrEffect = "Teleporte 9m (30 ft)",
            components = "V",
            description = "Brevemente envolto por névoa prateada, você se teleporta até 9 metros para um espaço desocupado que você possa ver."
        ),
        PresetSpell(
            name = "Invisibilidade (Invisibility)",
            level = 2,
            school = "Ilusão",
            castingTime = "1 Ação",
            range = "Toque",
            damageOrEffect = "Condição: Invisível",
            components = "V, S, M",
            description = "Uma criatura que você tocar se torna invisível até que a magia acabe (Concentração, até 1 hora). Qualquer coisa que o alvo esteja vestindo ou carregando fica invisível. A magia encerra se o alvo atacar ou conjurar uma magia."
        ),
        PresetSpell(
            name = "Contrafeitiço (Counterspell)",
            level = 3,
            school = "Abjuração",
            castingTime = "1 Reação",
            range = "18m (60 ft)",
            damageOrEffect = "Anula Magia",
            components = "S",
            description = "Você tenta interromper uma criatura no processo de conjurar uma magia. Se a criatura estiver conjurando uma magia de 3º nível ou inferior, a magia falha e não produz efeito."
        ),
        PresetSpell(
            name = "Dissipar Magia (Dispel Magic)",
            level = 3,
            school = "Abjuração",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "Remove Efeitos Mágicos",
            components = "V, S",
            description = "Escolha uma criatura, objeto ou efeito mágico dentro do alcance. Qualquer magia de 3º nível ou inferior no alvo encerra."
        ),
        PresetSpell(
            name = "Voo (Fly)",
            level = 3,
            school = "Transmutação",
            castingTime = "1 Ação",
            range = "Toque",
            damageOrEffect = "Deslocamento de Voo 18m",
            components = "V, S, M",
            description = "Você toca uma criatura disposta. O alvo ganha um deslocamento de voo de 18 metros pela duração (Concentração, até 10 minutos)."
        ),
        PresetSpell(
            name = "Velocidade (Haste)",
            level = 3,
            school = "Transmutação",
            castingTime = "1 Ação",
            range = "9m (30 ft)",
            damageOrEffect = "+2 CA, Dobra Deslocamento, +1 Ação",
            components = "V, S, M",
            description = "Escolha uma criatura disposta. Pela duração, o deslocamento do alvo é dobrado, ganha +2 na CA, vantagem em testes de resistência de Destreza e uma ação adicional em cada um dos seus turnos."
        ),
        PresetSpell(
            name = "Imobilizar Pessoa (Hold Person)",
            level = 2,
            school = "Encantamento",
            castingTime = "1 Ação",
            range = "18m (60 ft)",
            damageOrEffect = "Condição: Paralisado",
            components = "V, S, M",
            description = "Escolha um humanoide que você possa ver dentro do alcance. O alvo deve passar em um teste de resistência de Sabedoria ou ficará paralisado pela duração (Concentração, até 1 minuto)."
        ),
        PresetSpell(
            name = "Onda Trovão (Thunderwave)",
            level = 1,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "Pessoal (Cubo de 4.5m)",
            damageOrEffect = "2d8 Dano Trovão + Empurrão",
            components = "V, S",
            description = "Uma onda de força trovejante se descarrega a partir de você. Cada criatura num cubo de 4.5m deve fazer um teste de resistência de Constituição. Se falhar, sofre 2d8 de dano trovejante e é empurrada 3m."
        ),
        PresetSpell(
            name = "Mãos Flamejantes (Burning Hands)",
            level = 1,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "Pessoal (Cone de 4.5m)",
            damageOrEffect = "3d6 Dano de Fogo",
            components = "V, S",
            description = "Uma folha de chamas escorre das suas pontas dos dedos estendidas. Cada criatura num cone de 4.5m deve realizar um teste de resistência de Destreza, sofrendo 3d6 de dano de fogo se falhar."
        ),
        PresetSpell(
            name = "Chama Sagrada (Sacred Flame)",
            level = 0,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "18m (60 ft)",
            damageOrEffect = "1d8 Dano Radiante",
            components = "V, S",
            description = "Radiação semelhante a fogo desce sobre uma criatura que você possa ver dentro do alcance. O alvo deve passar em um teste de resistência de Destreza ou sofrerá 1d8 de dano radiante."
        ),
        PresetSpell(
            name = "Explosão Mística (Eldritch Blast)",
            level = 0,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "1d10 Dano de Força",
            components = "V, S",
            description = "Um feixe de energia crepitante lança-se em direção a uma criatura dentro do alcance. Faça um ataque à distância com magia. Se acertar, o alvo sofre 1d10 de dano de força."
        ),
        PresetSpell(
            name = "Seta Ácida de Melf (Melf's Acid Arrow)",
            level = 2,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "27m (90 ft)",
            damageOrEffect = "4d4 Dano Ácido + 2d4 Ácido",
            components = "V, S, M",
            description = "Uma flecha verde cintilante lança-se para um alvo dentro do alcance e explode em ácido. Faça um ataque com magia. Se acertar, causa 4d4 de dano ácido imediatamente e 2d4 no final do próximo turno do alvo."
        ),
        PresetSpell(
            name = "Relâmpago (Lightning Bolt)",
            level = 3,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "Pessoal (Linha de 30m)",
            damageOrEffect = "8d6 Dano Elétrico",
            components = "V, S, M",
            description = "Um feixe de eletricidade formando uma linha de 30m de comprimento e 1.5m de largura dispara de você numa direção à sua escolha. Causa 8d6 de dano elétrico."
        ),
        PresetSpell(
            name = "Bênção (Bless)",
            level = 1,
            school = "Encantamento",
            castingTime = "1 Ação",
            range = "9m (30 ft)",
            damageOrEffect = "+1d4 em Ataques e Resistências",
            components = "V, S, M",
            description = "Você abençoa até três criaturas dentro do alcance. Sempre que um alvo fizer uma jogada de ataque ou teste de resistência, adicione 1d4 ao resultado."
        ),
        PresetSpell(
            name = "Santuário (Sanctuary)",
            level = 1,
            school = "Abjuração",
            castingTime = "1 Ação Bônus",
            range = "9m (30 ft)",
            damageOrEffect = "Proteção contra Ataques",
            components = "V, S, M",
            description = "Você protege uma criatura dentro do alcance contra ataques. Qualquer criatura que alvejar a criatura protegida deve fazer um teste de resistência de Sabedoria ou escolher outro alvo."
        ),
        PresetSpell(
            name = "Luz (Light)",
            level = 0,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "Toque",
            damageOrEffect = "Ilumina objeto (6m luz clara)",
            components = "V, M",
            description = "Você toca um objeto que não tenha mais de 3 metros em qualquer dimensão. O objeto emite luz clara num raio de 6 metros e luz meia-sombra por mais 6 metros."
        ),
        PresetSpell(
            name = "Reviver (Revivify)",
            level = 3,
            school = "Necromancia",
            castingTime = "1 Ação",
            range = "Toque",
            damageOrEffect = "Ressuscita com 1 PV",
            components = "V, S, M (Diamante 300po)",
            description = "Você toca uma criatura que tenha morrido no último minuto. Essa criatura retorna à vida com 1 ponto de vida."
        ),
        PresetSpell(
            name = "Desejo (Wish)",
            level = 9,
            school = "Conjuração",
            castingTime = "1 Ação",
            range = "Pessoal",
            damageOrEffect = "Efeito supremo de alteração da realidade",
            components = "V",
            description = "Desejo é a magia mortal mais poderosa que uma criatura pode conjurar. Ao simplesmente pronunciá-la, você pode alterar os próprios alicerces da realidade conforme o seu desejo."
        ),
        // --- XGtE Expansion Spells ---
        PresetSpell(
            name = "Absorver Elementos (Absorb Elements) [XGtE]",
            level = 1,
            school = "Abjuração",
            castingTime = "1 Reação",
            range = "Pessoal",
            damageOrEffect = "Resistência a dano elementar + 1d6 no próximo ataque",
            components = "S",
            description = "A magia captura parte da energia recebida, reduzindo o seu efeito sobre você e armazenando-a para o seu próximo ataque corpo a corpo. Concede resistência contra o tipo de dano engrenado até o início do seu próximo turno."
        ),
        PresetSpell(
            name = "Badalar dos Mortos (Toll the Dead) [XGtE]",
            level = 0,
            school = "Necromancia",
            castingTime = "1 Ação",
            range = "18m (60 ft)",
            damageOrEffect = "1d8 / 1d12 Dano Necrótico",
            components = "V, S",
            description = "Você aponta para uma criatura dentro do alcance. O som de um sino doloroso ressoa ao redor dela. O alvo deve passar num teste de Sabedoria ou sofrerá 1d8 de dano necrótico. Se o alvo estiver ferido (com PV abaixo do máximo), o dano aumenta para 1d12."
        ),
        PresetSpell(
            name = "Raio de Caos (Chaos Bolt) [XGtE]",
            level = 1,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "2d8 + 1d6 Dano Elemental Aleatório",
            components = "V, S",
            description = "Você lança uma massa ondulante de energia caótica contra uma criatura dentro do alcance. Faça um ataque à distância com magia. O tipo de dano é determinado pelos dados de d8 rolados."
        ),
        PresetSpell(
            name = "Lâmina de Sombras (Shadow Blade) [XGtE]",
            level = 2,
            school = "Ilusão",
            castingTime = "1 Ação Bônus",
            range = "Pessoal",
            damageOrEffect = "2d8 Dano Psíquico",
            components = "V, S",
            description = "Você tece sombras entrelaçadas para criar uma espada de escuridão solidificada na sua mão. Ela causa 2d8 de dano psíquico e tem a propriedade Finesse e Leve."
        ),
        PresetSpell(
            name = "Espírito Curativo (Healing Spirit) [XGtE]",
            level = 2,
            school = "Conjuração",
            castingTime = "1 Ação Bônus",
            range = "18m (60 ft)",
            damageOrEffect = "1d6 Cura por Turno",
            components = "V, S",
            description = "Você invoca um espírito de cura imaterial num ponto desocupado que você possa ver dentro do alcance. Sempre que você ou uma criatura entrar no espaço do espírito, ele recupera 1d6 PV."
        ),
        PresetSpell(
            name = "Radiação Doentia (Sickening Radiance) [XGtE]",
            level = 4,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "4d10 Dano Radiante + Exaustão",
            components = "V, S",
            description = "Luz esverdeada emite numa esfera de 9m de raio. Criaturas que iniciarem o turno na área devem passar em teste de Constituição ou sofrerão 4d10 de dano radiante e 1 nível de exaustão."
        ),
        PresetSpell(
            name = "Surtos Sinápticos (Synaptic Static) [XGtE]",
            level = 5,
            school = "Encantamento",
            castingTime = "1 Ação",
            range = "36m (120 ft)",
            damageOrEffect = "8d6 Dano Psíquico + -1d6 em Ataques",
            components = "V, S",
            description = "Você causa uma explosão psíquica numa esfera de 6m de raio. Criaturas atingidas devem fazer teste de Inteligência ou sofrerão 8d6 de dano psíquico e terão -1d6 em jogadas de ataque e testes por 1 minuto."
        ),
        // --- TCoE Expansion Spells ---
        PresetSpell(
            name = "Infusão Caústica de Tasha (Tasha's Caustic Brew) [TCoE]",
            level = 1,
            school = "Evocação",
            castingTime = "1 Ação",
            range = "Pessoal (Linha de 9m)",
            damageOrEffect = "2d4 Dano Ácido por Turno",
            components = "V, S, M",
            description = "Um fluxo de ácido escorre de suas mãos cobrindo uma linha de 9m x 1.5m. Criaturas atingidas sofrem 2d4 de dano ácido no início de cada um dos seus turnos até usarem uma ação para raspar o ácido."
        ),
        PresetSpell(
            name = "Açoite Mental de Tasha (Tasha's Mind Whip) [TCoE]",
            level = 2,
            school = "Encantamento",
            castingTime = "1 Ação",
            range = "27m (90 ft)",
            damageOrEffect = "3d6 Dano Psíquico + Limita Ações",
            components = "V",
            description = "Você envia um chicote psíquico mental. O alvo faz teste de Inteligência. Se falhar, sofre 3d6 de dano psíquico e no próximo turno só poderá realizar uma Ação, Movimento ou Ação Bônus (apenas uma)."
        ),
        PresetSpell(
            name = "Invocação de Fadas (Summon Fey) [TCoE]",
            level = 3,
            school = "Conjuração",
            castingTime = "1 Ação",
            range = "27m (90 ft)",
            damageOrEffect = "Invoca Espírito Feérico de Combate",
            components = "V, S, M",
            description = "Você invoca um espírito feérico que assume uma forma corpórea à sua escolha (Alegre, Focada ou Magoada) e luta sob seu comando por até 1 hora (Concentração)."
        ),
        PresetSpell(
            name = "Invocação de Mortos-Vivos (Summon Undead) [TCoE]",
            level = 3,
            school = "Necromancia",
            castingTime = "1 Ação",
            range = "27m (90 ft)",
            damageOrEffect = "Invoca Necrófago, Fantasma ou Esqueleto",
            components = "V, S, M",
            description = "Você invoca um espírito morto-vivo que assume uma forma física corpórea e ataca seus inimigos em combate por até 1 hora."
        ),
        PresetSpell(
            name = "Lâmina do Desastre (Blade of Disaster) [TCoE]",
            level = 9,
            school = "Conjuração",
            castingTime = "1 Ação Bônus",
            range = "18m (60 ft)",
            damageOrEffect = "4d12 Dano de Força (Crítico 18-20 = 12d12)",
            components = "V, S",
            description = "Você cria uma lâmina fenda de rasgo dimensional. Como ação bônus, você pode movê-la 9m e realizar 2 ataques causando 4d12 de dano de força cada. Críticos no 18-20 causam 12d12 de dano."
        )
    )
}
