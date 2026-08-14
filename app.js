import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { getFirestore, doc, onSnapshot, setDoc } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore.js";

// Your web app's Firebase configuration
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

// User & Session Portal State
let currentUser = JSON.parse(localStorage.getItem('grimoire_user') || 'null');
let currentTableCode = localStorage.getItem('grimoire_table_code') || '';
let unsubscribeSync = null;
let docRef = null;

// Initial State (Matching Android Character & Models)
let gameState = {
  character: {
    name: "Valeros",
    class: "Humano Guerreiro Nível 3",
    meta: "Soldado | Leal e Bom",
    hp: 28,
    maxHp: 28,
    ac: 16,
    initMod: 2,
    speed: "9m",
    profBonus: 2,
    passivePerc: 11,
    stats: {
      "FOR": { val: 16, mod: "+3", class: "str" },
      "DES": { val: 14, mod: "+2", class: "dex" },
      "CON": { val: 15, mod: "+2", class: "con" },
      "INT": { val: 10, mod: "+0", class: "int" },
      "SAB": { val: 12, mod: "+1", class: "wis" },
      "CAR": { val: 8, mod: "-1", class: "cha" }
    },
    currencies: { pp: 5, po: 125, pe: 0, ps: 20, pc: 50 },
    spellSlots: [
      { level: 1, current: 4, max: 4 },
      { level: 2, current: 2, max: 2 }
    ]
  },
  spells: [
    { name: "Mísseis Mágicos", level: 1, school: "Evocação", time: "1 Ação", range: "36m", desc: "Cria 3 dardos de força mágica. Cada dardo causa 1d4 + 1 de dano de força." },
    { name: "Curar Ferimentos", level: 1, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Uma criatura recupera 1d8 + modificador de habilidade em HP." },
    { name: "Escudo Mágico", level: 1, school: "Abjuração", time: "1 Reação", range: "Pessoal", desc: "Ganha +5 na CA até o início do seu próximo turno." },
    { name: "Bola de Fogo", level: 3, school: "Evocação", time: "1 Ação", range: "45m", desc: "Explosão de fogo em esfera de 6m. Causa 8d6 de dano de fogo." },
    { name: "Luz", level: 0, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Objeto passa a emitir luz brilhante em raio de 6 metros." }
  ],
  items: [
    { name: "Espada Longa +1", category: "Equipado", qty: 1, weight: 1.5 },
    { name: "Escudo de Aço", category: "Equipado", qty: 1, weight: 3.0 },
    { name: "Armadura de Cota de Malha", category: "Equipado", qty: 1, weight: 25.0 },
    { name: "Mochila de Aventureiro", category: "Mochila", qty: 1, weight: 2.0 },
    { name: "Saco de Dormir & Tochas", category: "Mochila", qty: 1, weight: 5.0 },
    { name: "Poção de Cura Maior", category: "Consumíveis", qty: 3, weight: 0.5 }
  ],
  combatants: [
    { name: "Valeros (Jogador)", init: 18, hp: 28, type: "Jogador" },
    { name: "Lorde Goblin", init: 14, hp: 22, type: "Inimigo" },
    { name: "Goblin Arqueiro", init: 9, hp: 11, type: "Inimigo" }
  ],
  currentTurnIndex: 0,
  diceLogs: [],
  notes: [
    { title: "Símbolo de Strahd", text: "Encontrado no altar antigo no Castelo Ravenloft.", date: "Hoje" },
    { title: "Missão: Salvar a Vila", text: "Procurar o ferreiro desaparecido perto da floresta das névoas.", date: "Ontem" }
  ],
  sessions: [
    { title: "Sessão 1: Chegada em Barovia", date: "10 de Agosto", summary: "O grupo atravessou os portões de névoa e encontrou a carruagem misteriosa." },
    { title: "Sessão 2: A Casa da Morte", date: "12 de Agosto", summary: "Exploração dos andares superiores e descoberta dos diários secretas." }
  ],
  dmNotes: ""
};

let selectedInvCategory = "Equipado";

// Connect to Table Session in Firestore
function connectTableSession(code) {
  if (unsubscribeSync) unsubscribeSync();
  currentTableCode = code.toUpperCase().trim();
  localStorage.setItem('grimoire_table_code', currentTableCode);

  docRef = doc(db, "table_sessions", currentTableCode);

  const headerSub = document.getElementById('headerSubText');
  if (headerSub) headerSub.innerText = `Mesa: ${currentTableCode}`;

  unsubscribeSync = onSnapshot(docRef, (docSnap) => {
    if (docSnap.exists()) {
      const data = docSnap.data();
      if (data && data.state) {
        gameState = data.state;
        renderAll();
        const statusText = document.getElementById('syncText');
        if (statusText) statusText.innerText = `Ao Vivo (${currentTableCode})`;
      }
    } else {
      saveStateToFirebase();
    }
  }, (err) => {
    console.warn("Firestore fallback:", err);
    const statusText = document.getElementById('syncText');
    if (statusText) statusText.innerText = 'Modo Offline';
  });
}

async function saveStateToFirebase() {
  if (!docRef) return;
  try {
    await setDoc(docRef, { state: gameState, lastUpdated: Date.now() });
  } catch (e) {
    console.error("Erro ao salvar Firestore:", e);
  }
}

// Portal & Login Functions
window.switchPortalTab = function(tab) {
  const tabLogin = document.getElementById('pTabLogin');
  const tabTable = document.getElementById('pTabTable');
  const btnLogin = document.getElementById('pTabLoginBtn');
  const btnTable = document.getElementById('pTabTableBtn');

  if (tab === 'login') {
    tabLogin.style.display = 'block';
    tabTable.style.display = 'none';
    btnLogin.classList.add('active');
    btnTable.classList.remove('active');
  } else {
    tabLogin.style.display = 'none';
    tabTable.style.display = 'block';
    btnLogin.classList.remove('active');
    btnTable.classList.add('active');
  }
};

window.handleUserAuth = function() {
  const email = document.getElementById('loginEmail').value.trim();
  const name = document.getElementById('loginName').value.trim();
  const role = document.getElementById('loginRole').value;

  if (!email || !name) {
    alert('Por favor, informe seu E-mail e Nome do Jogador.');
    return;
  }

  currentUser = { email, name, role };
  localStorage.setItem('grimoire_user', JSON.stringify(currentUser));

  updateUserHeader();
  window.switchPortalTab('table');
};

window.handleJoinTable = function() {
  const code = document.getElementById('tableCodeInput').value.trim();
  if (!code) {
    alert('Por favor, digite o Código da Mesa.');
    return;
  }

  if (!currentUser) {
    alert('Por favor, entre ou crie sua conta primeiro na aba 1.');
    window.switchPortalTab('login');
    return;
  }

  connectTableSession(code);

  const overlay = document.getElementById('portalOverlay');
  if (overlay) overlay.classList.add('hidden');
};

window.generateNewTableCode = function() {
  const randomCode = 'MESA-' + Math.random().toString(36).substring(2, 7).toUpperCase();
  document.getElementById('tableCodeInput').value = randomCode;
};

window.openPortalScreen = function() {
  const overlay = document.getElementById('portalOverlay');
  if (overlay) overlay.classList.remove('hidden');
};

function updateUserHeader() {
  const nameText = document.getElementById('userNameText');
  if (nameText && currentUser) {
    nameText.innerText = `${currentUser.name} (${currentUser.role || 'Jogador'})`;
  }
}

// Check Portal state on startup
function initPortalState() {
  if (currentUser) {
    updateUserHeader();
    document.getElementById('loginEmail').value = currentUser.email || '';
    document.getElementById('loginName').value = currentUser.name || '';
  }

  if (currentTableCode) {
    document.getElementById('tableCodeInput').value = currentTableCode;
    connectTableSession(currentTableCode);
    if (currentUser) {
      document.getElementById('portalOverlay').classList.add('hidden');
    }
  } else {
    document.getElementById('portalOverlay').classList.remove('hidden');
  }
}

// Navigation
window.switchTab = function(tabName, btnElem) {
  document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

  const targetTab = document.getElementById(`tab-${tabName}`);
  if (targetTab) targetTab.classList.add('active');
  if (btnElem) btnElem.classList.add('active');
};

// Render Functions
function renderAll() {
  renderCharacter();
  renderSpells();
  renderInventory();
  renderCombat();
  renderDiceLog();
  renderCampaign();
  renderNotes();
}

function renderCharacter() {
  const c = gameState.character;
  if (!c) return;

  document.getElementById('charName').innerText = c.name;
  document.getElementById('charClass').innerText = c.class;
  document.getElementById('hpDisplay').innerText = `${c.hp} / ${c.maxHp} HP`;
  document.getElementById('statAc').innerText = c.ac;
  document.getElementById('statInit').innerText = (c.initMod >= 0 ? '+' : '') + c.initMod;
  document.getElementById('statSpeed').innerText = c.speed;
  document.getElementById('statProf').innerText = '+' + (c.profBonus || 2);
  document.getElementById('statPerc').innerText = c.passivePerc || 11;

  // HP Bar
  const pct = Math.max(0, Math.min(100, (c.hp / c.maxHp) * 100));
  document.getElementById('hpBarFill').style.width = pct + '%';

  // Stats Grid
  const attrGrid = document.getElementById('attrGrid');
  if (attrGrid) {
    attrGrid.innerHTML = '';
    for (const [k, v] of Object.entries(c.stats)) {
      attrGrid.innerHTML += `
        <div class="attr-box ${v.class || ''}">
          <div class="attr-label">${k}</div>
          <div class="attr-val">${v.val}</div>
          <div class="attr-mod">${v.mod}</div>
        </div>
      `;
    }
  }

  // Currencies
  const cur = c.currencies || {};
  document.getElementById('currPp').innerText = cur.pp || 0;
  document.getElementById('currPo').innerText = cur.po || 0;
  document.getElementById('currPe').innerText = cur.pe || 0;
  document.getElementById('currPs').innerText = cur.ps || 0;
  document.getElementById('currPc').innerText = cur.pc || 0;
}

window.adjustHp = function(amount) {
  gameState.character.hp = Math.max(0, Math.min(gameState.character.maxHp, gameState.character.hp + amount));
  renderCharacter();
  saveStateToFirebase();
};

window.triggerRest = function(type) {
  if (type === 'short') {
    gameState.character.hp = Math.min(gameState.character.maxHp, gameState.character.hp + 8);
    alert('☕ Descanso Curto concluído! Você recuperou 8 HP.');
  } else {
    gameState.character.hp = gameState.character.maxHp;
    if (gameState.character.spellSlots) {
      gameState.character.spellSlots.forEach(s => s.current = s.max);
    }
    alert('⛺ Descanso Longo concluído! HP e Espaços de Magia restaurados.');
  }
  renderCharacter();
  saveStateToFirebase();
};

// Spells
const SPELLS_DATA = [
  { name: "Mísseis Mágicos", level: 1, school: "Evocação", time: "1 Ação", range: "36m", desc: "Cria 3 dardos de força mágica. Cada dardo causa 1d4 + 1 de dano de força." },
  { name: "Curar Ferimentos", level: 1, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Uma criatura recupera 1d8 + modificador de habilidade em HP." },
  { name: "Escudo Mágico", level: 1, school: "Abjuração", time: "1 Reação", range: "Pessoal", desc: "Ganha +5 na CA até o início do seu próximo turno." },
  { name: "Bola de Fogo", level: 3, school: "Evocação", time: "1 Ação", range: "45m", desc: "Explosão de fogo em esfera de 6m. Causa 8d6 de dano de fogo." },
  { name: "Luz", level: 0, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Objeto passa a emitir luz brilhante em raio de 6 metros." }
];

function renderSpells() {
  const slotsGrid = document.getElementById('spellSlotsGrid');
  if (slotsGrid && gameState.character.spellSlots) {
    slotsGrid.innerHTML = '';
    gameState.character.spellSlots.forEach((s, idx) => {
      slotsGrid.innerHTML += `
        <div class="stat-box" onclick="useSpellSlot(${idx})" style="cursor: pointer;">
          <div class="stat-box-label">${s.level}º Nível</div>
          <div class="stat-box-value">${s.current}/${s.max}</div>
          <div style="font-size: 9px; color: var(--text-muted); margin-top: 2px;">Usar</div>
        </div>
      `;
    });
  }

  const spellsList = document.getElementById('spellsList');
  if (!spellsList) return;
  const query = (document.getElementById('spellSearch')?.value || '').toLowerCase();
  spellsList.innerHTML = '';

  SPELLS_DATA.filter(s => s.name.toLowerCase().includes(query)).forEach(s => {
    spellsList.innerHTML += `
      <div style="background-color: var(--arcane-surface-variant); padding: 12px; border-radius: 12px; margin-bottom: 8px; border: 1px solid var(--arcane-border);">
        <div style="display: flex; justify-content: space-between; font-weight: 700; font-size: 14px; color: #fff;">
          <span>${s.name}</span>
          <span style="font-size: 11px; background: var(--arcane-purple-dark); padding: 2px 8px; border-radius: 8px; color: var(--arcane-purple-light);">${s.level === 0 ? 'Truque' : s.level + 'º Nível'}</span>
        </div>
        <div style="font-size: 11px; color: var(--text-secondary); margin: 4px 0;">🏫 ${s.school} • ⏱️ ${s.time} • 🎯 ${s.range}</div>
        <div style="font-size: 12px; color: var(--text-primary); line-height: 1.4;">${s.desc}</div>
      </div>
    `;
  });
}

window.useSpellSlot = function(idx) {
  if (gameState.character.spellSlots[idx].current > 0) {
    gameState.character.spellSlots[idx].current--;
    renderSpells();
    saveStateToFirebase();
  }
};

window.filterSpells = function() {
  renderSpells();
};

// Inventory
window.switchInvCategory = function(cat, btn) {
  selectedInvCategory = cat;
  document.querySelectorAll('.sub-tab-btn').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  renderInventory();
};

function renderInventory() {
  const container = document.getElementById('itemList');
  if (!container) return;
  container.innerHTML = '';

  const filtered = (gameState.items || []).filter(i => i.category === selectedInvCategory);
  
  let totalW = (gameState.items || []).reduce((acc, i) => acc + (i.weight * i.qty), 0);
  document.getElementById('inventoryWeight').innerHTML = `Carga Total: <strong>${totalW.toFixed(1)} kg</strong> / 120 kg`;

  if (filtered.length === 0) {
    container.innerHTML = `<div style="text-align: center; color: var(--text-muted); font-size: 12px; padding: 20px;">Nenhum item nesta categoria.</div>`;
    return;
  }

  filtered.forEach((item, idx) => {
    container.innerHTML += `
      <div style="display: flex; justify-content: space-between; align-items: center; background-color: var(--arcane-surface-variant); padding: 12px; border-radius: 12px; margin-bottom: 8px; border: 1px solid var(--arcane-border);">
        <div>
          <div style="font-weight: 700; font-size: 14px; color: #fff;">${item.name}</div>
          <div style="font-size: 11px; color: var(--text-secondary);">${item.weight} kg cada</div>
        </div>
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="font-weight: 800; font-size: 14px; color: var(--mystic-gold);">x${item.qty}</span>
        </div>
      </div>
    `;
  });
}

// Combat
function renderCombat() {
  const container = document.getElementById('combatList');
  if (!container) return;
  container.innerHTML = '';

  (gameState.combatants || []).forEach((c, idx) => {
    const isActive = idx === gameState.currentTurnIndex;
    container.innerHTML += `
      <div style="display: flex; align-items: center; justify-content: space-between; background-color: var(--arcane-surface-variant); padding: 12px; border-radius: 12px; margin-bottom: 8px; border-left: 4px solid ${isActive ? 'var(--health-green)' : 'transparent'}; border: 1px solid var(--arcane-border);">
        <div style="font-size: 18px; font-weight: 900; color: var(--mystic-gold); width: 30px;">${c.init}</div>
        <div style="flex: 1; padding: 0 10px;">
          <div style="font-weight: 700; font-size: 14px; color: #fff;">${c.name} ${isActive ? '⚡' : ''}</div>
          <div style="font-size: 11px; color: var(--text-secondary);">${c.type} • HP: ${c.hp}</div>
        </div>
        <button class="btn-action" style="padding: 4px 8px; font-size: 11px; color: var(--health-red);" onclick="removeCombatant(${idx})">Remover</button>
      </div>
    `;
  });
}

window.nextCombatTurn = function() {
  if (!gameState.combatants || gameState.combatants.length === 0) return;
  gameState.currentTurnIndex = (gameState.currentTurnIndex + 1) % gameState.combatants.length;
  renderCombat();
  saveStateToFirebase();
};

window.resetCombat = function() {
  gameState.currentTurnIndex = 0;
  renderCombat();
  saveStateToFirebase();
};

window.addCombatant = function() {
  const name = document.getElementById('combName').value || 'Combatente';
  const init = parseInt(document.getElementById('combInit').value || 10, 10);
  const hp = parseInt(document.getElementById('combHp').value || 10, 10);
  const type = document.getElementById('combType').value;

  if (!gameState.combatants) gameState.combatants = [];
  gameState.combatants.push({ name, init, hp, type });
  gameState.combatants.sort((a, b) => b.init - a.init);

  closeModal('combatModal');
  renderCombat();
  saveStateToFirebase();
};

window.removeCombatant = function(idx) {
  gameState.combatants.splice(idx, 1);
  if (gameState.currentTurnIndex >= gameState.combatants.length) gameState.currentTurnIndex = 0;
  renderCombat();
  saveStateToFirebase();
};

// Dice Roller
window.rollDice = function(sides) {
  const mod = parseInt(document.getElementById('diceMod')?.value || 0, 10);
  const raw = Math.floor(Math.random() * sides) + 1;
  const total = raw + mod;

  let details = `d${sides} (${raw}) ${mod >= 0 ? '+' : ''}${mod} = ${total}`;
  if (sides === 20 && raw === 20) details += ' 🔥 SUCESSO CRÍTICO!';
  if (sides === 20 && raw === 1) details += ' 💀 FALHA CRÍTICA!';

  document.getElementById('lastRollResult').innerText = total;
  document.getElementById('lastRollDetails').innerText = details;

  const log = {
    user: gameState.character.name,
    dice: `d${sides}`,
    total: total,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  };

  if (!gameState.diceLogs) gameState.diceLogs = [];
  gameState.diceLogs.unshift(log);
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
      <div style="display: flex; justify-content: space-between; background: rgba(255,255,255,0.03); padding: 8px 12px; border-radius: 8px; margin-bottom: 6px; font-size: 12px;">
        <div><strong>${log.user}</strong> rolou <span style="color: var(--arcane-purple-light); font-weight: 700;">${log.dice}</span></div>
        <div><strong style="color: var(--mystic-gold); font-size: 14px;">${log.total}</strong> <span style="color: var(--text-muted); font-size: 10px;">(${log.time})</span></div>
      </div>
    `;
  });
}

// Campaign & Notes
function renderCampaign() {
  const container = document.getElementById('sessionLogs');
  if (!container) return;
  container.innerHTML = '';
  (gameState.sessions || []).forEach(s => {
    container.innerHTML += `
      <div style="background-color: var(--arcane-surface-variant); padding: 12px; border-radius: 12px; margin-bottom: 8px; border: 1px solid var(--arcane-border);">
        <div style="font-weight: 700; color: #fff; font-size: 14px;">${s.title}</div>
        <div style="font-size: 11px; color: var(--arcane-purple-light); margin: 2px 0;">📅 ${s.date}</div>
        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 4px;">${s.summary}</div>
      </div>
    `;
  });
}

function renderNotes() {
  const container = document.getElementById('notesList');
  if (!container) return;
  container.innerHTML = '';
  (gameState.notes || []).forEach(n => {
    container.innerHTML += `
      <div style="background-color: var(--arcane-surface-variant); padding: 12px; border-radius: 12px; margin-bottom: 8px; border: 1px solid var(--arcane-border);">
        <div style="font-weight: 700; color: var(--mystic-gold); font-size: 14px;">${n.title}</div>
        <div style="font-size: 12px; color: var(--text-primary); margin-top: 4px; line-height: 1.4;">${n.text}</div>
      </div>
    `;
  });
}

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

// Modals
window.openEditCharModal = function() {
  const c = gameState.character;
  document.getElementById('editNameInput').value = c.name;
  document.getElementById('editClassInput').value = c.class;
  document.getElementById('editHpInput').value = c.maxHp;
  document.getElementById('editAcInput').value = c.ac;
  document.getElementById('charModal').classList.add('open');
};

window.saveEditChar = function() {
  gameState.character.name = document.getElementById('editNameInput').value || 'Valeros';
  gameState.character.class = document.getElementById('editClassInput').value || 'Aventureiro';
  gameState.character.maxHp = parseInt(document.getElementById('editHpInput').value || 28, 10);
  gameState.character.ac = parseInt(document.getElementById('editAcInput').value || 16, 10);
  closeModal('charModal');
  renderCharacter();
  saveStateToFirebase();
};

window.openAddCombatantModal = function() { document.getElementById('combatModal').classList.add('open'); };
window.openAddItemModal = function() { document.getElementById('itemModal').classList.add('open'); };

window.addItem = function() {
  const name = document.getElementById('itemName').value || 'Novo Item';
  const category = document.getElementById('itemCat').value;
  const qty = parseInt(document.getElementById('itemQty').value || 1, 10);
  const weight = parseFloat(document.getElementById('itemWeight').value || 1.0);

  if (!gameState.items) gameState.items = [];
  gameState.items.push({ name, category, qty, weight });
  closeModal('itemModal');
  renderInventory();
  saveStateToFirebase();
};

window.closeModal = function(id) {
  document.getElementById(id).classList.remove('open');
};

// Init
function bootApp() {
  renderAll();
  initPortalState();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', bootApp);
} else {
  bootApp();
}

