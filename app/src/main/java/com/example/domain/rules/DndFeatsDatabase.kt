package com.example.domain.rules

data class PresetFeat(
    val name: String,
    val prerequisite: String,
    val summary: String,
    val description: String,
    val category: String = "Talento Geral"
)

object DndFeatsDatabase {
    val ALL_PRESET_FEATS = listOf(
        PresetFeat(
            name = "Convocador de Guerra (War Caster)",
            prerequisite = "Capacidade de conjurar ao menos uma magia",
            summary = "Vantagem na Concentração e conjuração como Reação em Ataques de Oportunidade.",
            description = "• Você tem vantagem em testes de resistência de Constituição realizados para manter a concentração em uma magia quando sofre dano.\n• Você pode executar os componentes somáticos de magias mesmo estando com armas ou escudo em uma ou ambas as mãos.\n• Quando o movimento de uma criatura hostil engatilhar um ataque de oportunidade contra você, você pode usar sua reação para conjurar uma magia na criatura em vez de realizar um ataque de oportunidade."
        ),
        PresetFeat(
            name = "Alerta (Alert)",
            prerequisite = "Nenhum",
            summary = "+5 na Iniciativa, não pode ser surpreendido e inimigos não ganham vantagem por estarem escondidos.",
            description = "• Você ganha um bônus de +5 em suas jogadas de iniciativa.\n• Você não pode ser surpreendido enquanto estiver consciente.\n• Outras criaturas não ganham vantagem nas jogadas de ataque contra você pelo fato de estarem escondidas de você."
        ),
        PresetFeat(
            name = "Mestre em Armas Grandes (Great Weapon Master)",
            prerequisite = "Nenhum",
            summary = "Ataque Bônus ao cricar/abater e opção de -5 no ataque para +10 de dano.",
            description = "• Em seu turno, quando você atingir um acerto crítico com uma arma corpo a corpo ou reduzir os PV de uma criatura a 0 com ela, você pode realizar um ataque com arma corpo a corpo adicional como Ação Bônus.\n• Antes de fazer um ataque corpo a corpo com uma arma pesada na qual seja proficiente, você pode escolher sofrer uma penalidade de -5 na jogada de ataque. Se o ataque acertar, adicione +10 ao dano."
        ),
        PresetFeat(
            name = "Atirador de Elite (Sharpshooter)",
            prerequisite = "Nenhum",
            summary = "Ataca a longa distância sem desvantagem, ignora cobertura e opção de -5 ataque para +10 dano à distância.",
            description = "• Atacar a longa distância não impõe desvantagem em suas jogadas de ataque com armas à distância.\n• Seus ataques com armas à distância ignoram meia cobertura e três-quartos de cobertura.\n• Antes de fazer um ataque com arma à distância na qual seja proficiente, você pode escolher sofrer -5 na jogada de ataque. Se acertar, você adiciona +10 ao dano do ataque."
        ),
        PresetFeat(
            name = "Sortudo (Lucky)",
            prerequisite = "Nenhum",
            summary = "3 Pontos de Sorte por descanso longo para rerrolar d20s ou forçar rerrolagem de inimigos.",
            description = "• Você tem 3 pontos de sorte. Você pode gastar 1 ponto de sorte para rolar um d20 adicional sempre que fizer um teste de habilidade, jogada de ataque ou teste de resistência, escolhendo qual d20 usar.\n• Você também pode gastar 1 ponto de sorte quando uma jogada de ataque for feita contra você para rolar um d20 e escolher se o ataque usa o d20 do atacante ou o seu."
        ),
        PresetFeat(
            name = "Resiliente (Resilient)",
            prerequisite = "Nenhum",
            summary = "+1 em um atributo à sua escolha e ganha proficiência nos Testes de Resistência desse atributo.",
            description = "• Escolha um valor de habilidade. Você aumenta esse valor em 1, até o máximo de 20.\n• Você ganha proficiência em testes de resistência usando a habilidade escolhida."
        ),
        PresetFeat(
            name = "Sentinela (Sentinel)",
            prerequisite = "Nenhum",
            summary = "Reduz deslocamento a 0 com ataque de oportunidade e ataca quando aliados próximos são atingidos.",
            description = "• Quando você atinge uma criatura com um ataque de oportunidade, o deslocamento dela se torna 0 pelo resto do turno.\n• Criaturas dentro do seu alcance provocam ataques de oportunidade de você mesmo se usarem a ação Desengajar.\n• Quando uma criatura a 1,5m de você fizer um ataque contra um alvo diferente de você, você pode usar sua reação para fazer um ataque com arma corpo a corpo contra aquela criatura."
        ),
        PresetFeat(
            name = "Mestre em Escudos (Shield Master)",
            prerequisite = "Nenhum",
            summary = "Empurrar com ação bônus, adicionar bônus do escudo em testes de DES e reação para anular dano em área.",
            description = "• Se realizar a ação Atacar no seu turno, você pode usar uma ação bônus para empurrar uma criatura a 1,5m de você com seu escudo.\n• Se você não estiver incapacitado, pode adicionar o bônus de CA do seu escudo a qualquer teste de resistência de Destreza contra magias ou efeitos direcionados a você.\n• Se for alvo de um efeito que permita um teste de resistência de Destreza para sofrer apenas metade do dano, você pode usar sua reação para não sofrer dano algum se passar."
        ),
        PresetFeat(
            name = "Mestre em Armadura Pesada (Heavy Armor Master)",
            prerequisite = "Proficiência com Armadura Pesada",
            summary = "+1 em FOR e reduz dano de concussão, cortante e perfurante não-mágico em 3.",
            description = "• Seu valor de Força aumenta em 1, até o máximo de 20.\n• Enquanto estiver vestindo armadura pesada, o dano de concussão, perfurante e cortante que você sofre de armas não-mágicas é reduzido em 3."
        ),
        PresetFeat(
            name = "Robusto (Tough)",
            prerequisite = "Nenhum",
            summary = "+2 Pontos de Vida por nível acumulado e a cada nível futuro.",
            description = "• O seu máximo de pontos de vida aumenta em um valor igual a duas vezes o seu nível no momento em que você adquire este talento.\n• Sempre que você ganhar um nível posteriormente, seu máximo de pontos de vida aumenta em 2 pontos adicionais."
        ),
        PresetFeat(
            name = "Toque das Fadas (Fey Touched)",
            prerequisite = "Nenhum",
            summary = "+1 em INT, SAB ou CAR, aprende Passo Nebuloso e 1 magia de 1º nível de Adivinhação/Encantamento.",
            description = "• Aumenta sua Inteligência, Sabedoria ou Carisma em 1, até o máximo de 20.\n• Você aprende a magia Passo Nebuloso (Misty Step) e uma magia de 1º nível à sua escolha da escola de Adivinhação ou Encantamento. Você pode conjurar cada uma dessas magias uma vez sem gastar espaço de magia por descanso longo."
        ),
        PresetFeat(
            name = "Líder Inspirador (Inspiring Leader)",
            prerequisite = "Carisma 13 ou mais",
            summary = "Concede PV Temporários iguais ao seu Nível + Modificador de Carisma para até 6 aliados.",
            description = "• Você pode gastar 10 minutos inspirando seus companheiros. Escolha até 6 criaturas amigáveis (incluindo você) a até 9 metros de você.\n• Cada criatura ganha pontos de vida temporários iguais ao seu nível + seu modificador de Carisma. Cada criatura só pode se beneficiar deste talento uma vez por descanso curto ou longo."
        ),
        PresetFeat(
            name = "Móvel (Mobile)",
            prerequisite = "Nenhum",
            summary = "+3 metros de deslocamento, Ação de Disparada ignora terreno difícil e evita ataques de oportunidade ao atacar.",
            description = "• Seu deslocamento aumenta em 3 metros.\n• Quando você usa a ação Disparar (Dash), terreno difícil não custa movimento extra naquele turno.\n• Quando você faz um ataque corpo a corpo contra uma criatura, você não provoca ataques de oportunidade dessa criatura pelo resto do turno, tendo acertado o ataque ou não."
        ),
        PresetFeat(
            name = "Iniciador Mágico (Magic Initiate)",
            prerequisite = "Nenhum",
            summary = "Aprende 2 truques e 1 magia de 1º nível de uma classe à sua escolha (Mago, Clérigo, Bruxo, etc.).",
            description = "• Escolha uma classe: Bardo, Clérigo, Druida, Feiticeiro, Bruxo ou Mago.\n• Você aprende dois truques da lista de magias dessa classe.\n• Além disso, aprende uma magia de 1º nível dessa mesma lista. Você pode conjurá-la no seu nível mais baixo sem gastar espaço de magia uma vez por descanso longo."
        ),
        PresetFeat(
            name = "Ator (Actor)",
            prerequisite = "Carisma 13 ou mais",
            summary = "+1 em CAR, vantagem em enganar/atuar e imita voz ou sons de outras criaturas.",
            description = "• Seu valor de Carisma aumenta em 1, até o máximo de 20.\n• Você tem vantagem em testes de Carisma (Enganação) e Carisma (Atuação) quando estiver tentando se passar por outra pessoa.\n• Você pode imitar a fala de outra pessoa ou os sons feitos por outras criaturas. Você precisa ter ouvido a pessoa falar ou a criatura fazer o som por pelo menos 1 minuto."
        ),
        PresetFeat(
            name = "Matador de Conjuradores (Mage Slayer)",
            prerequisite = "Nenhum",
            summary = "Reação para atacar quem conjurar magias a 1,5m, desvantagem na concentração do inimigo e vantagem na resistência.",
            description = "• Quando uma criatura a 1,5m de você conjurar uma magia, você pode usar sua reação para fazer um ataque corpo a corpo contra ela.\n• Quando você causa dano a uma criatura que esteja se concentrando em uma magia, ela tem desvantagem no teste de resistência de Constituição.\n• Você tem vantagem em testes de resistência contra magias conjuradas por criaturas a até 1,5m de você."
        ),
        PresetFeat(
            name = "Observador (Observant)",
            prerequisite = "INT ou SAB 13 ou mais",
            summary = "+1 em INT ou SAB, lê lábios e ganha +5 na Percepção Passiva e Investigação Passiva.",
            description = "• Aumenta seu valor de Inteligência ou Sabedoria em 1, até o máximo de 20.\n• Se puder ver a boca de uma criatura enquanto ela fala um idioma que você entende, você pode ler seus lábios.\n• Você ganha um bônus de +5 em suas pontuações passivas de Percepção Passiva e Investigação Passiva."
        ),
        // --- XGtE Racial Feats ---
        PresetFeat(
            name = "Precisão Élfica (Elven Accuracy) [XGtE]",
            prerequisite = "Elfo ou Meio-Elfo",
            summary = "+1 em DES, INT, SAB ou CAR e rola 3 d20s ao ter Vantagem em ataques desses atributos.",
            description = "• Aumenta sua Destreza, Inteligência, Sabedoria ou Carisma em 1, até o máximo de 20.\n• Sempre que você tiver vantagem em uma jogada de ataque usando Destreza, Inteligência, Sabedoria ou Carisma, você pode rerrolar um dos dados uma vez."
        ),
        PresetFeat(
            name = "Fortitude Anã (Dwarven Fortitude) [XGtE]",
            prerequisite = "Anão",
            summary = "+1 em CON e pode gastar 1 Dado de Vida para se curar ao realizar a ação Esquivar.",
            description = "• Seu valor de Constituição aumenta em 1, até o máximo de 20.\n• Sempre que você realiza a ação Esquivar (Dodge) em combate, pode gastar um dos seus Dados de Vida para recuperar PV (rolando o dado + mod de CON)."
        ),
        PresetFeat(
            name = "Sorte Abundante (Bountiful Luck) [XGtE]",
            prerequisite = "Halfling",
            summary = "Permite que aliados a até 9m rerrolem 1s em d20s usando sua Reação.",
            description = "• Quando um aliado a 9m de você rolar um 1 em um d20 para uma jogada de ataque, teste de habilidade ou teste de resistência, você pode usar sua reação para permitir que ele rerrole o d20."
        ),
        PresetFeat(
            name = "Prodígio (Prodigy) [XGtE]",
            prerequisite = "Meio-Elfo, Meio-Orc ou Humano",
            summary = "Ganha 1 Perícia, 1 Ferramenta, 1 Idioma e Especialização (Expertise) em 1 perícia.",
            description = "• Você ganha uma proficiência em perícia à sua escolha, uma proficiência em ferramenta, um idioma à sua escolha e Especialização em uma perícia que você já seja proficiente (dobra bônus de proficiência)."
        ),
        // --- TCoE Feats ---
        PresetFeat(
            name = "Toque das Sombras (Shadow Touched) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em INT, SAB ou CAR, aprende Invisibilidade e 1 magia de 1º nível de Ilusão/Necromancia.",
            description = "• Aumenta sua Inteligência, Sabedoria ou Carisma em 1, até o máximo de 20.\n• Você aprende a magia Invisibilidade e uma magia de 1º nível de Ilusão ou Necromancia. Pode conjurar cada uma 1x sem gastar espaço de magia por descanso longo."
        ),
        PresetFeat(
            name = "Telecinético (Telekinetic) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em INT, SAB ou CAR, truque Mão Mágica invisível (18m) e empurra criaturas a 1,5m como Ação Bônus.",
            description = "• Aumenta sua Inteligência, Sabedoria ou Carisma em 1, até o máximo de 20.\n• Aprende o truque Mão Mágica (invisível e alcance dobrado para 18m).\n• Como Ação Bônus, pode tentar empurrar telepaticamente uma criatura a até 9m por 1,5m em sua direção ou para longe (teste de RES de Força)."
        ),
        PresetFeat(
            name = "Telepático (Telepathic) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em INT, SAB ou CAR, comunicação telepática a 18m e aprende a magia Ler Pensamentos.",
            description = "• Aumenta sua Inteligência, Sabedoria ou Carisma em 1, até o máximo de 20.\n• Pode se comunicar telepaticamente com qualquer criatura a até 18m que você possa ver.\n• Aprende a magia Ler Pensamentos (Detect Thoughts), podendo conjurá-la 1x sem gastar espaço por descanso longo."
        ),
        PresetFeat(
            name = "Especialista em Perícias (Skill Expert) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em um Atributo, 1 nova Perícia e Especialização (Expertise) em 1 Perícia.",
            description = "• Aumenta um valor de habilidade à sua escolha em 1, até o máximo de 20.\n• Você ganha proficiência em uma perícia à sua escolha.\n• Você ganha Especialização em uma perícia na qual já é proficiente (dobra seu bônus de proficiência nela)."
        ),
        PresetFeat(
            name = "Adept da Metamagia (Metamagic Adept) [TCoE]",
            prerequisite = "Capacidade de conjurar ao menos uma magia",
            summary = "Aprende 2 opções de Metamagia do Feiticeiro e ganha 2 Pontos de Feitiçaria.",
            description = "• Você aprende duas opções de Metamagia da classe Feiticeiro.\n• Você ganha 2 pontos de feitiçaria para gastar nessas opções (recuperados ao finalizar um descanso longo)."
        ),
        PresetFeat(
            name = "Adept Misterioso (Eldritch Adept) [TCoE]",
            prerequisite = "Capacidade de conjurar ao menos uma magia",
            summary = "Aprende 1 Invocação Mística (Eldritch Invocation) da classe Bruxo.",
            description = "• Você aprende uma Invocação Mística da classe Bruxo à sua escolha. Se a invocação tiver pré-requisito, você só pode escolhê-la se for um Bruxo que o satisfaça."
        ),
        PresetFeat(
            name = "Esmagador (Crusher) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em FOR ou CON, empurra 1,5m 1x/turno ao causar dano de concussão e acertos críticos concedem vantagem.",
            description = "• Aumenta sua Força ou Constituição em 1, até o máximo de 20.\n• Uma vez por turno, ao causar dano de concussão, você pode mover o alvo 1,5m para um espaço desocupado.\n• Quando atinge um acerto crítico causando dano de concussão, ataques contra o alvo têm vantagem até o início do seu próximo turno."
        ),
        PresetFeat(
            name = "Perfurador (Piercer) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em FOR ou DES, rerrola 1 dado de dano perfurante 1x/turno e adiciona +1 dado no crítico.",
            description = "• Aumenta sua Força ou Destreza em 1, até o máximo de 20.\n• Uma vez por turno, ao causar dano perfurante, você pode rerrolar um dos dados de dano do ataque.\n• Quando atinge um acerto crítico causando dano perfurante, você pode rolar um dado de dano adicional."
        ),
        PresetFeat(
            name = "Cortador (Slasher) [TCoE]",
            prerequisite = "Nenhum",
            summary = "+1 em FOR ou DES, reduz deslocamento do alvo em 3m ao causar dano cortante e críticos impõem desvantagem.",
            description = "• Aumenta sua Força ou Destreza em 1, até o máximo de 20.\n• Uma vez por turno, ao causar dano cortante, você pode reduzir o deslocamento do alvo em 3 metros até o início do seu próximo turno.\n• Quando atinge um acerto crítico causando dano cortante, o alvo tem desvantagem em todas as jogadas de ataque até o início do seu próximo turno."
        ),
        // --- VRGtR Dark Gifts ---
        PresetFeat(
            name = "Presente Sombrio: Sombra Viva (Living Shadow) [VRGtR]",
            prerequisite = "Pacto de Ravenloft / VRGtR",
            summary = "Aumenta o alcance de ataques corpo a corpo e interações para 3 metros por meio de uma sombra animada.",
            description = "• Sua sombra ganha vida própria. O alcance dos seus ataques corpo a corpo, magias de toque e interações aumenta em 3 metros por meio da extensão da sua sombra.\n• Maldição: Em momentos de estresse ou desacordo, a sombra pode agir de forma independente e assustar aliados."
        ),
        PresetFeat(
            name = "Presente Sombrio: Caminhante das Névoas (Mist Walker) [VRGtR]",
            prerequisite = "Pacto de Ravenloft / VRGtR",
            summary = "Concede a magia Passo Nebuloso e imunidade a se perder nas Névoas dos Domínios do Pavor.",
            description = "• Você pode conjurar Passo Nebuloso (Misty Step) um número de vezes igual ao seu bônus de proficiência por descanso longo.\n• Você sabe navegar instintivamente através das Névoas que conectam os domínios de terror."
        )
    )
}
