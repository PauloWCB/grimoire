import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { getFirestore, doc, onSnapshot, setDoc } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore.js";

// Firebase Config provided by user
const firebaseConfig = {
  apiKey: "AIzaSyDD1X_h9GXl6mZppZZ6l_A2tUdSq14iTE8",
  authDomain: "grimoire-ee2b4.firebaseapp.com",
  projectId: "grimoire-ee2b4",
  storageBucket: "grimoire-ee2b4.firebasestorage.app",
  messagingSenderId: "146338216554",
  appId: "1:146338216554:web:a4d8321ea1d27390e5e813"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

// Initial State Data
let gameState = {
  character: {
    name: "Valeros",
    class: "Guerreiro Nível 3 (Humano)",
    hp: 28,
    maxHp: 28,
    ac: 16,
    initMod: 2,
    speed: "9m",
    stats: {
      "FOR": { val: 16, mod: "+3" },
      "DES": { val: 14, mod: "+2" },
      "CON": { val: 15, mod: "+2" },
      "INT": { val: 10, mod: "+0" },
      "SAB": { val: 12, mod: "+1" },
      "CAR": { val: 8, mod: "-1" }
    },
    spellSlots: [
      { level: 1, current: 4, max: 4 },
      { level: 2, current: 2, max: 2 }
    ]
  },
  combatants: [
    { name: "Valeros (Jogador)", init: 18, hp: 28, type: "Jogador" },
    { name: "Lorde Goblin", init: 14, hp: 22, type: "Inimigo" },
    { name: "Goblin Arqueiro", init: 9, hp: 11, type: "Inimigo" }
  ],
  currentTurnIndex: 0,
  diceLogs: []
};

// Real-time Firestore Sync
const docRef = doc(db, "table_sessions", "mesa_principal");

// Listen for updates from Firebase Firestore in real-time
onSnapshot(docRef, (docSnap) => {
  if (docSnap.exists()) {
    const data = docSnap.data();
    if (data && data.state) {
      gameState = data.state;
      renderAll();
      document.getElementById('syncStatus').style.background = 'rgba(129, 199, 132, 0.15)';
      document.getElementById('syncText').innerText = 'Sincronizado ao Vivo';
    }
  } else {
    // Save initial state if first time
    saveStateToFirebase();
  }
}, (err) => {
  console.warn("Firestore mode offline/local fallback:", err);
  document.getElementById('syncText').innerText = 'Modo Local (Offline)';
});

async function saveStateToFirebase() {
  try {
    await setDoc(docRef, { state: gameState, lastUpdated: Date.now() });
  } catch (e) {
    console.error("Erro ao salvar no Firestore:", e);
  }
}

// Sample Spells D&D 5e
const SPELLS_DATABASE = [
  { name: "Mísseis Mágicos", level: 1, school: "Evocação", time: "1 Ação", range: "36 metros", desc: "Cria três dardos de força mágica. Cada dardo atinge uma criatura à sua escolha e causa 1d4 + 1 de dano de força." },
  { name: "Curar Ferimentos", level: 1, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Uma criatura que você tocar recupera pontos de vida iguais a 1d8 + seu modificador de habilidade de conjuração." },
  { name: "Escudo Mágico", level: 1, school: "Abjuração", time: "1 Reação", range: "Pessoal", desc: "Uma barreira invisível de força surge e concede +5 na CA até o início do seu próximo turno." },
  { name: "Bola de Fogo", level: 3, school: "Evocação", time: "1 Ação", range: "45 metros", desc: "Uma explosão de fogo em uma esfera de 6m de raio. Cada criatura deve fazer um teste de resistência de Destreza, sofrendo 8d6 de dano de fogo." },
  { name: "Passos sem Pegadas", level: 2, school: "Ilusão", time: "1 Ação", range: "Pessoal", desc: "Uma sombra e silêncio envolvem você e seus aliados, concedendo +10 em testes de Furtividade." },
  { name: "Luz", level: 0, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Você toca um objeto e ele passa a emitir luz brilhante em um raio de 6 metros." }
];

// Navigation
window.switchTab = function(tabName, btnElem) {
  document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  
  document.getElementById(`tab-${tabName}`).classList.add('active');
  if (btnElem) btnElem.classList.add('active');
};

// Render Functions
function renderAll() {
  renderCharacter();
  renderSpells();
  renderDiceLog();
  renderCombat();
}

function renderCharacter() {
  const c = gameState.character;
  document.getElementById('charName').innerText = c.name;
  document.getElementById('charClass').innerText = c.class;
  document.getElementById('currentHp').innerText = c.hp;
  document.getElementById('maxHp').innerText = c.maxHp;
  document.getElementById('statAc').innerText = c.ac;
  document.getElementById('statInit').innerText = (c.initMod >= 0 ? '+' : '') + c.initMod;
  document.getElementById('statSpeed').innerText = c.speed;

  // HP Bar
  const pct = Math.max(0, Math.min(100, (c.hp / c.maxHp) * 100));
  document.getElementById('hpBarFill').style.width = pct + '%';

  // Stats Grid
  const grid = document.getElementById('statsGrid');
  grid.innerHTML = '';
  for (const [k, v] of Object.entries(c.stats)) {
    grid.innerHTML += `
      <div class="stat-box">
        <div class="stat-label">${k}</div>
        <div class="stat-value">${v.val}</div>
        <div class="stat-mod">${v.mod}</div>
      </div>
    `;
  }

  // Spell Slots Grid
  const slotsGrid = document.getElementById('spellSlotsGrid');
  if (slotsGrid) {
    slotsGrid.innerHTML = '';
    c.spellSlots.forEach((s, idx) => {
      slotsGrid.innerHTML += `
        <div class="stat-box" onclick="useSpellSlot(${idx})" style="cursor: pointer;">
          <div class="stat-label">${s.level}º Nível</div>
          <div class="stat-value" style="color: var(--md-sys-color-primary);">${s.current}/${s.max}</div>
          <div style="font-size: 10px; color: var(--md-sys-color-on-surface-variant);">Toque p/ Usar</div>
        </div>
      `;
    });
  }
}

window.adjustHp = function(amount) {
  gameState.character.hp = Math.max(0, Math.min(gameState.character.maxHp, gameState.character.hp + amount));
  renderCharacter();
  saveStateToFirebase();
};

window.useSpellSlot = function(idx) {
  if (gameState.character.spellSlots[idx].current > 0) {
    gameState.character.spellSlots[idx].current--;
    renderCharacter();
    saveStateToFirebase();
  }
};

window.triggerRest = function(type) {
  if (type === 'short') {
    gameState.character.hp = Math.min(gameState.character.maxHp, gameState.character.hp + 8);
    alert(' Descanso Curto concluído! Você recuperou 8 HP.');
  } else {
    gameState.character.hp = gameState.character.maxHp;
    gameState.character.spellSlots.forEach(s => s.current = s.max);
    alert(' Descanso Longo concluído! HP e Espaços de Magia totalmente restaurados.');
  }
  renderCharacter();
  saveStateToFirebase();
};

function renderSpells() {
  const container = document.getElementById('spellsList');
  if (!container) return;
  
  const query = (document.getElementById('spellSearch')?.value || '').toLowerCase();
  container.innerHTML = '';

  SPELLS_DATABASE.filter(s => s.name.toLowerCase().includes(query) || s.school.toLowerCase().includes(query)).forEach(spell => {
    container.innerHTML += `
      <div class="spell-item">
        <div class="spell-header">
          <span class="spell-title">${spell.name}</span>
          <span class="spell-level">${spell.level === 0 ? 'Truque' : spell.level + 'º Nível'}</span>
        </div>
        <div class="spell-meta">🏫 ${spell.school} • ⏱️ ${spell.time} • 🎯 ${spell.range}</div>
        <div style="font-size: 12px; margin-top: 6px; color: var(--md-sys-color-on-surface); line-height: 1.4;">${spell.desc}</div>
      </div>
    `;
  });
}

window.filterSpells = function() {
  renderSpells();
};

// Dice Rolling
window.rollDice = function(sides) {
  const mod = parseInt(document.getElementById('diceMod')?.value || 0, 10);
  const raw = Math.floor(Math.random() * sides) + 1;
  const total = raw + mod;

  let critText = '';
  if (sides === 20 && raw === 20) critText = '🔥 SUCESSO CRÍTICO (20 NATULAR)!';
  else if (sides === 20 && raw === 1) critText = '💀 FALHA CRÍTICA (1 NATURAL)!';

  document.getElementById('lastRollResult').innerText = total;
  document.getElementById('lastRollDetails').innerText = `d${sides} (${raw}) ${mod >= 0 ? '+' : ''}${mod} = ${total} ${critText}`;

  const logEntry = {
    user: gameState.character.name,
    dice: `d${sides}`,
    raw: raw,
    mod: mod,
    total: total,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  };

  if (!gameState.diceLogs) gameState.diceLogs = [];
  gameState.diceLogs.unshift(logEntry);
  if (gameState.diceLogs.length > 20) gameState.diceLogs.pop();

  renderDiceLog();
  saveStateToFirebase();
};

function renderDiceLog() {
  const list = document.getElementById('diceLogList');
  if (!list) return;
  list.innerHTML = '';
  (gameState.diceLogs || []).forEach(log => {
    list.innerHTML += `
      <div class="log-item">
        <div>
          <strong>${log.user}</strong> rolou <span class="log-roll">${log.dice}</span>
        </div>
        <div>
          <span style="font-weight: 800; font-size: 15px; color: #fff;">${log.total}</span>
          <span style="font-size: 10px; color: var(--md-sys-color-on-surface-variant);">(${log.time})</span>
        </div>
      </div>
    `;
  });
}

// Combat Tracker
function renderCombat() {
  const list = document.getElementById('combatList');
  if (!list) return;
  list.innerHTML = '';

  gameState.combatants.forEach((c, idx) => {
    const isActive = idx === gameState.currentTurnIndex;
    list.innerHTML += `
      <div class="combat-item ${isActive ? 'active-turn' : ''}">
        <div class="combat-init">${c.init}</div>
        <div class="combat-info">
          <div class="combat-name">${c.name} ${isActive ? '⚡ (Turno Atual)' : ''}</div>
          <div class="combat-type">${c.type} • HP: ${c.hp}</div>
        </div>
        <button class="btn-icon" style="width: 32px; height: 32px; font-size: 12px; background: rgba(242,184,181,0.2); color: #f2b8b5;" onclick="removeCombatant(${idx})">
          <i class="fa-solid fa-trash"></i>
        </button>
      </div>
    `;
  });
}

window.nextCombatTurn = function() {
  if (gameState.combatants.length === 0) return;
  gameState.currentTurnIndex = (gameState.currentTurnIndex + 1) % gameState.combatants.length;
  renderCombat();
  saveStateToFirebase();
};

window.resetCombat = function() {
  gameState.currentTurnIndex = 0;
  renderCombat();
  saveStateToFirebase();
};

window.openAddCombatantModal = function() {
  document.getElementById('combatModal').classList.add('open');
};

window.closeModal = function(id) {
  document.getElementById(id).classList.remove('open');
};

window.addCombatant = function() {
  const name = document.getElementById('combName').value || 'Combatente';
  const init = parseInt(document.getElementById('combInit').value || 10, 10);
  const hp = parseInt(document.getElementById('combHp').value || 10, 10);
  const type = document.getElementById('combType').value;

  gameState.combatants.push({ name, init, hp, type });
  gameState.combatants.sort((a, b) => b.init - a.init);

  closeModal('combatModal');
  renderCombat();
  saveStateToFirebase();
};

window.removeCombatant = function(idx) {
  gameState.combatants.splice(idx, 1);
  if (gameState.currentTurnIndex >= gameState.combatants.length) {
    gameState.currentTurnIndex = 0;
  }
  renderCombat();
  saveStateToFirebase();
};

// DM Tools
window.generateRandomNpc = function() {
  const names = ["Eldrin Ventofrio", "Gideon Braço-de-Ferro", "Lyra Cantolua", "Balthazar Sorriso-Negro", "Kaelen da Névoa"];
  const races = ["Meio-Elfo", "Anão da Montanha", "Tiefling", "Humano", "Halfling Coração-Valente"];
  const traits = ["Suspeita de estranhos, mas adora ouro.", "Fala muito rápido quando nervosa.", "Manca da perna esquerda e guarda um segredo.", "Coleciona dentes de criaturas mágicas."];

  const name = names[Math.floor(Math.random() * names.length)];
  const race = races[Math.floor(Math.random() * races.length)];
  const trait = traits[Math.floor(Math.random() * traits.length)];

  document.getElementById('npcName').innerText = `${name} (${race})`;
  document.getElementById('npcDetails').innerText = `Traço de Personalidade: "${trait}"`;
  document.getElementById('npcDisplay').style.display = 'block';
};

// Initial Render on load
document.addEventListener('DOMContentLoaded', () => {
  renderAll();
});
