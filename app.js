// Grimoire D&D 5e - PWA Core Logic (1:1 Feature & UX Replica of Android Jetpack Compose App)

import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { getFirestore, doc, onSnapshot, setDoc } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore.js";

// Firebase Config
const firebaseConfig = {
  apiKey: "AIzaSyDummyKeyForStudioBuild1234567890",
  authDomain: "ais-dev-jnzvd7jjokryr6aznnxw36.firebaseapp.com",
  projectId: "ais-dev-jnzvd7jjokryr6aznnxw36",
  storageBucket: "ais-dev-jnzvd7jjokryr6aznnxw36.appspot.com",
  messagingSenderId: "522368345922",
  appId: "1:522368345922:web:grimoire123"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

// Application State
let currentRole = localStorage.getItem('grimoire_role') || 'PLAYER'; // 'PLAYER' or 'DM'
let currentTableCode = localStorage.getItem('grimoire_table_code') || 'MESA-BAROVIA';
let selectedPortalRole = 'PLAYER';
let activeTab = 'character';
let selectedInvCategory = 'Equipado';
let selectedCurrencyForEdit = null;

let unsubscribeSync = null;
let docRef = null;

// Initial Character & Campaign State (Identical to CharacterEntity in Kotlin)
let gameState = {
  character: {
    id: 1,
    name: "Valeros",
    raceClass: "Humano Guerreiro Nível 3",
    meta: "Soldado | Leal e Bom",
    hp: 28,
    maxHp: 28,
    ac: 16,
    init: 2,
    speed: 9,
    prof: 2,
    perc: 11,
    cp: 50,
    sp: 20,
    ep: 0,
    gp: 125,
    pp: 5,
    str: 16,
    dex: 14,
    con: 15,
    int: 10,
    wis: 12,
    cha: 8,
    spellSlots: [
      { level: 1, current: 4, max: 4 },
      { level: 2, current: 2, max: 2 },
      { level: 3, current: 0, max: 0 }
    ]
  },
  charactersList: [
    { id: 1, name: "Valeros", classLevel: "Humano Guerreiro Nvl 3", hp: "28/28" },
    { id: 2, name: "Aeloria", classLevel: "Elfo Mago Nvl 3", hp: "18/18" },
    { id: 3, name: "Kaelen", classLevel: "Anão Clérigo Nvl 3", hp: "26/26" }
  ],
  campaign: {
    title: "Sombra de Ravenloft",
    code: "MESA-BAROVIA",
    summary: "Mistos de névoa e pesadelos cobrem o reino de Barovia. Sob o olhar atento do Conde Strahd, a expedição busca sobreviver."
  },
  spells: [
    { name: "Mísseis Mágicos", level: 1, school: "Evocação", time: "1 Ação", range: "36m", desc: "Cria 3 dardos de força mágica. Cada dardo causa 1d4 + 1 de dano de força." },
    { name: "Curar Ferimentos", level: 1, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Uma criatura recupera 1d8 + modificador de habilidade em HP." },
    { name: "Escudo Mágico", level: 1, school: "Abjuração", time: "1 Reação", range: "Pessoal", desc: "Ganha +5 na CA até o início do seu próximo turno." },
    { name: "Bola de Fogo", level: 3, school: "Evocação", time: "1 Ação", range: "45m", desc: "Explosão de fogo em esfera de 6m. Causa 8d6 de dano de fogo." },
    { name: "Luz", level: 0, school: "Evocação", time: "1 Ação", range: "Toque", desc: "Objeto passa a emitir luz brilhante em raio de 6 metros." }
  ],
  items: [
    { name: "Espada Longa +1", category: "Equipado", qty: 1, weight: 1.5, properties: "1d8 cortante, Versátil", description: "Uma lâmina perfeitamente forjada com runas reluzentes." },
    { name: "Escudo de Aço", category: "Equipado", qty: 1, weight: 3.0, properties: "+2 na CA", description: "Escudo pesado com o brasão da sua ordem." },
    { name: "Armadura de Cota de Malha", category: "Equipado", qty: 1, weight: 25.0, properties: "CA 16", description: "Anéis de metal trançados fornecem excelente proteção contra lâminas." },
    { name: "Mochila de Aventureiro", category: "Mochila", qty: 1, weight: 2.0, properties: "Capacidade 15kg", description: "Contém provisões e ferramentas essenciais." },
    { name: "Poção de Cura Menor", category: "Consumíveis", qty: 3, weight: 0.5, properties: "Recupera 2d4+2 HP", description: "Líquido vermelho rubi com borbulhas mágicas." },
    { name: "Tocha", category: "Consumíveis", qty: 5, weight: 0.5, properties: "Ilumina 6m", description: "Queima por 1 hora fornecendo luz brilhante." }
  ],
  combatants: [
    { name: "Valeros (Guerreiro)", init: 18, hp: 28, maxHp: 28, type: "Jogador", active: true },
    { name: "Goblin Ladrão", init: 14, hp: 7, maxHp: 7, type: "Inimigo", active: false },
    { name: "Goblin Arqueiro", init: 9, hp: 7, maxHp: 7, type: "Inimigo", active: false }
  ],
  notes: [
    { title: "Símbolo Sagrado Encontrado", date: "12 de Agosto", content: "Localizado no altar abandonado da igreja de Barovia." },
    { title: "Pista do Moinho Velho", date: "10 de Agosto", content: "Seguir para o norte após o meio-dia." }
  ],
  sessions: [
    { title: "Sessão 1: Chegada em Barovia", date: "10 de Agosto", summary: "O grupo atravessou os portões de névoa e encontrou a carruagem misteriosa." },
    { title: "Sessão 2: A Casa da Morte", date: "12 de Agosto", summary: "Exploração dos andares superiores e descoberta dos diários secretas." }
  ]
};

// Connect to Table Session in Firestore
function connectTableSession(code) {
  if (unsubscribeSync) unsubscribeSync();
  currentTableCode = code.toUpperCase().trim();
  localStorage.setItem('grimoire_table_code', currentTableCode);

  docRef = doc(db, "table_sessions", currentTableCode);

  unsubscribeSync = onSnapshot(docRef, (docSnap) => {
    if (docSnap.exists()) {
      const data = docSnap.data();
      if (data && data.state) {
        gameState = data.state;
        renderAll();
      }
    } else {
      saveStateToFirebase();
    }
  }, (err) => {
    console.warn("Firestore fallback:", err);
  });
}

async function saveStateToFirebase() {
  if (!docRef) return;
  try {
    await setDoc(docRef, { state: gameState, lastUpdated: Date.now() });
  } catch (e) {
    console.warn("Save error:", e);
  }
}

// Render Functions

// 1. Navigation & Header
function renderHeaderAndNav() {
  const roleBadge = document.getElementById('headerRoleBadge');
  const headerSubText = document.getElementById('headerSubText');
  const headerAvatar = document.getElementById('headerAvatar');

  if (currentRole === 'DM') {
    if (roleBadge) {
      roleBadge.innerText = 'MESTRE';
      roleBadge.style.backgroundColor = 'var(--mystic-gold)';
    }
    if (headerSubText) headerSubText.innerText = gameState.campaign.title || 'Mestre da Mesa';
    if (headerAvatar) headerAvatar.innerText = '👑';
  } else {
    if (roleBadge) {
      roleBadge.innerText = 'JOGADOR';
      roleBadge.style.backgroundColor = 'var(--health-green)';
    }
    if (headerSubText) headerSubText.innerText = gameState.character.name || 'Valeros';
    if (headerAvatar) headerAvatar.innerText = (gameState.character.name || 'V').charAt(0);
  }

  // Render Bottom Nav Tabs dynamically according to Role
  const bottomNav = document.getElementById('dynamicBottomNav');
  if (!bottomNav) return;

  if (currentRole === 'DM') {
    bottomNav.innerHTML = `
      <button class="nav-item ${activeTab === 'campaign' ? 'active' : ''}" onclick="switchTab('campaign')"><i class="fa-solid fa-dungeon"></i><span>Campanha</span></button>
      <button class="nav-item ${activeTab === 'dm-characters' ? 'active' : ''}" onclick="switchTab('dm-characters')"><i class="fa-solid fa-users"></i><span>Personagens</span></button>
      <button class="nav-item ${activeTab === 'spells' ? 'active' : ''}" onclick="switchTab('spells')"><i class="fa-solid fa-wand-magic-sparkles"></i><span>Magias</span></button>
      <button class="nav-item ${activeTab === 'combat' ? 'active' : ''}" onclick="switchTab('combat')"><i class="fa-solid fa-skull"></i><span>Combate</span></button>
    `;
  } else {
    bottomNav.innerHTML = `
      <button class="nav-item ${activeTab === 'character' ? 'active' : ''}" onclick="switchTab('character')"><i class="fa-solid fa-user-shield"></i><span>Ficha</span></button>
      <button class="nav-item ${activeTab === 'spells' ? 'active' : ''}" onclick="switchTab('spells')"><i class="fa-solid fa-wand-magic-sparkles"></i><span>Magias</span></button>
      <button class="nav-item ${activeTab === 'inventory' ? 'active' : ''}" onclick="switchTab('inventory')"><i class="fa-solid fa-box-archive"></i><span>Inventário</span></button>
      <button class="nav-item ${activeTab === 'combat' ? 'active' : ''}" onclick="switchTab('combat')"><i class="fa-solid fa-skull"></i><span>Combate</span></button>
      <button class="nav-item ${activeTab === 'notes' ? 'active' : ''}" onclick="switchTab('notes')"><i class="fa-solid fa-scroll"></i><span>Notas</span></button>
    `;
  }
}

window.switchTab = function(tabName) {
  activeTab = tabName;
  document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));

  const targetTab = document.getElementById(`tab-${tabName}`);
  if (targetTab) targetTab.classList.add('active');

  renderHeaderAndNav();
};

// 2. Character Sheet Render
function renderCharacter() {
  const c = gameState.character;
  if (!c) return;

  document.getElementById('charName').innerText = c.name;
  document.getElementById('charClass').innerText = c.raceClass;
  document.getElementById('charMeta').innerText = c.meta || 'Aventureiro';

  document.getElementById('hpDisplay').innerText = `${c.hp} / ${c.maxHp} HP`;
  const pct = Math.max(0, Math.min(100, (c.hp / c.maxHp) * 100));
  document.getElementById('hpBarFill').style.width = `${pct}%`;

  document.getElementById('statAc').innerText = c.ac;
  document.getElementById('statInit').innerText = (c.init >= 0 ? '+' : '') + c.init;
  document.getElementById('statSpeed').innerText = `${c.speed}m`;
  document.getElementById('statProf').innerText = (c.prof >= 0 ? '+' : '') + c.prof;
  document.getElementById('statPerc').innerText = c.perc;

  const getMod = (val) => {
    const mod = Math.floor((val - 10) / 2);
    return (mod >= 0 ? '+' : '') + mod;
  };

  const attrs = [
    { label: "FOR", val: c.str },
    { label: "DES", val: c.dex },
    { label: "CON", val: c.con },
    { label: "INT", val: c.int },
    { label: "SAB", val: c.wis },
    { label: "CAR", val: c.cha }
  ];

  const grid = document.getElementById('attrGrid');
  if (grid) {
    grid.innerHTML = attrs.map(a => `
      <div class="attr-card">
        <div class="attr-label">${a.label}</div>
        <div class="attr-val">${a.val}</div>
        <div class="attr-mod">${getMod(a.val)}</div>
      </div>
    `).join('');
  }

  // Currencies
  if (document.getElementById('currPc')) document.getElementById('currPc').innerText = c.cp || 0;
  if (document.getElementById('currPs')) document.getElementById('currPs').innerText = c.sp || 0;
  if (document.getElementById('currPe')) document.getElementById('currPe').innerText = c.ep || 0;
  if (document.getElementById('currPo')) document.getElementById('currPo').innerText = c.gp || 0;
  if (document.getElementById('currPp')) document.getElementById('currPp').innerText = c.pp || 0;
}

window.adjustHp = function(delta) {
  const c = gameState.character;
  c.hp = Math.max(0, Math.min(c.maxHp, c.hp + delta));
  renderCharacter();
  saveStateToFirebase();
};

// 3. Inventory Screen Render (Matches InventoryScreen.kt)
function renderInventory() {
  const items = gameState.items || [];

  // Group items by Category: EQUIPADO, MOCHILA, CONSUMÍVEIS
  const categories = ["Equipado", "Mochila", "Consumíveis"];
  const totalWeight = items.reduce((acc, item) => acc + (parseFloat(item.weight || 1.0) * parseInt(item.qty || 1)), 0);

  const weightElem = document.getElementById('inventoryWeight');
  if (weightElem) {
    weightElem.innerHTML = `Carga Total: <strong>${totalWeight.toFixed(1)} kg</strong> / 120 kg`;
  }

  const container = document.getElementById('inventoryGroupsList');
  if (!container) return;

  container.innerHTML = categories.map(cat => {
    const groupItems = items.filter(i => (i.category || "Mochila").toLowerCase() === cat.toLowerCase());
    if (groupItems.length === 0) return '';

    return `
      <div style="margin-bottom: 16px;">
        <div style="font-size: 12px; font-weight: 800; color: var(--mystic-gold); letter-spacing: 1px; margin-bottom: 8px; text-transform: uppercase;">
          ${cat.toUpperCase()}
        </div>
        ${groupItems.map(item => `
          <div class="item-card">
            <div>
              <div style="font-size: 15px; font-weight: 700; color: #fff;">${item.name}</div>
              <div style="font-size: 12px; color: var(--arcane-purple-light);">${item.properties || ''}</div>
              <div style="font-size: 11px; color: var(--text-secondary);">${item.weight || 1.0} kg</div>
            </div>
            <div class="item-qty-badge">
              <button class="qty-control-btn" onclick="adjustItemQty('${item.name}', -1)">-</button>
              <span style="font-size: 13px; font-weight: 800; padding: 0 8px; color: #fff;">${item.qty}</span>
              <button class="qty-control-btn" onclick="adjustItemQty('${item.name}', 1)">+</button>
            </div>
          </div>
        `).join('')}
      </div>
    `;
  }).join('');
}

window.adjustItemQty = function(name, delta) {
  const item = gameState.items.find(i => i.name === name);
  if (item) {
    item.qty = Math.max(0, item.qty + delta);
    if (item.qty === 0) {
      gameState.items = gameState.items.filter(i => i.name !== name);
    }
    renderInventory();
    saveStateToFirebase();
  }
};

window.openAddItemModal = function() {
  document.getElementById('itemName').value = '';
  document.getElementById('itemCat').value = 'Mochila';
  document.getElementById('itemWeight').value = '1.0';
  document.getElementById('itemQty').value = '1';
  document.getElementById('itemProps').value = '';
  document.getElementById('itemDesc').value = '';
  document.getElementById('itemModal').classList.add('open');
};

window.addItem = function() {
  const name = document.getElementById('itemName').value.trim();
  const cat = document.getElementById('itemCat').value;
  const weight = parseFloat(document.getElementById('itemWeight').value) || 1.0;
  const qty = parseInt(document.getElementById('itemQty').value) || 1;
  const props = document.getElementById('itemProps').value.trim();
  const desc = document.getElementById('itemDesc').value.trim();

  if (!name) {
    alert('Por favor, digite o nome do item.');
    return;
  }

  gameState.items.push({ name, category: cat, weight, qty, properties: props, description: desc });
  closeModal('itemModal');
  renderInventory();
  saveStateToFirebase();
};

// 4. Currencies Edit Modal
window.openCurrencyModal = function(type) {
  selectedCurrencyForEdit = type;
  const c = gameState.character;
  const labels = { PC: 'Cobre (PC)', PP: 'Prata (PP)', PE: 'Electrum (PE)', PO: 'Ouro (PO)', PL: 'Platina (PL)' };
  const values = { PC: c.cp, PP: c.sp, PE: c.ep, PO: c.gp, PL: c.pp };

  document.getElementById('currencyModalTitle').innerText = `Editar saldo de ${labels[type] || type}`;
  document.getElementById('currencyAmountInput').value = values[type] || 0;
  document.getElementById('currencyModal').classList.add('open');
};

window.saveCurrencyFromModal = function() {
  if (!selectedCurrencyForEdit) return;
  const val = parseInt(document.getElementById('currencyAmountInput').value) || 0;
  const c = gameState.character;

  if (selectedCurrencyForEdit === 'PC') c.cp = val;
  if (selectedCurrencyForEdit === 'PP') c.sp = val;
  if (selectedCurrencyForEdit === 'PE') c.ep = val;
  if (selectedCurrencyForEdit === 'PO') c.gp = val;
  if (selectedCurrencyForEdit === 'PL') c.pp = val;

  closeModal('currencyModal');
  renderCharacter();
  saveStateToFirebase();
};

// 5. Spells Render
function renderSpells() {
  const slotsContainer = document.getElementById('spellSlotsGrid');
  if (slotsContainer) {
    slotsContainer.innerHTML = (gameState.character.spellSlots || []).map(s => `
      <div style="background: var(--arcane-surface-variant); border: 1px solid var(--arcane-border); padding: 10px; border-radius: 10px; text-align: center;">
        <div style="font-size: 11px; font-weight: 800; color: var(--arcane-purple-light);">${s.level}º Nível</div>
        <div style="font-size: 16px; font-weight: 800; color: var(--mystic-gold); margin: 4px 0;">${s.current}/${s.max}</div>
        <button class="btn-action" style="font-size: 10px; padding: 4px 8px;" onclick="useSpellSlot(${s.level})">Gastar</button>
      </div>
    `).join('');
  }

  const spellsList = document.getElementById('spellsList');
  if (spellsList) {
    spellsList.innerHTML = (gameState.spells || []).map(spell => `
      <div style="background: var(--arcane-surface-raised); border: 1px solid var(--arcane-border); padding: 12px; border-radius: 12px; margin-bottom: 8px;">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="font-size: 15px; font-weight: 700; color: #fff;">${spell.name}</div>
          <span style="font-size: 11px; font-weight: 800; color: var(--mystic-gold);">${spell.level === 0 ? 'Truque' : spell.level + 'º Nível'}</span>
        </div>
        <div style="font-size: 12px; color: var(--arcane-purple-light); margin-top: 2px;">${spell.school} • ${spell.time} • ${spell.range}</div>
        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 6px; line-height: 1.4;">${spell.desc}</div>
      </div>
    `).join('');
  }
}

window.useSpellSlot = function(level) {
  const slot = (gameState.character.spellSlots || []).find(s => s.level === level);
  if (slot && slot.current > 0) {
    slot.current--;
    renderSpells();
    saveStateToFirebase();
  }
};

// 6. Portal & Login Screen Logic (Identical to LoginPortalScreen.kt)
window.selectPortalRole = function(role) {
  selectedPortalRole = role;

  const cardPlayer = document.getElementById('roleCardPlayer');
  const cardDm = document.getElementById('roleCardDm');
  const pPlayerSection = document.getElementById('portalPlayerSection');
  const pDmSection = document.getElementById('portalDmSection');
  const enterBtn = document.getElementById('portalEnterBtn');

  if (role === 'PLAYER') {
    cardPlayer.classList.add('active');
    cardDm.classList.remove('active');
    pPlayerSection.style.display = 'block';
    pDmSection.style.display = 'none';
    enterBtn.innerHTML = `ENTRAR COMO JOGADOR <i class="fa-solid fa-chevron-right" style="margin-left: 6px;"></i>`;
    enterBtn.style.backgroundColor = 'var(--health-green)';
  } else {
    cardPlayer.classList.remove('active');
    cardDm.classList.add('active');
    pPlayerSection.style.display = 'none';
    pDmSection.style.display = 'block';
    enterBtn.innerHTML = `ENTRAR COMO MESTRE DA MESA <i class="fa-solid fa-chevron-right" style="margin-left: 6px;"></i>`;
    enterBtn.style.backgroundColor = 'var(--mystic-gold)';
  }
};

function renderPortalCharList() {
  const container = document.getElementById('portalCharList');
  if (!container) return;

  const chars = gameState.charactersList || [];
  container.innerHTML = chars.map((c, idx) => `
    <div class="portal-char-item ${idx === 0 ? 'selected' : ''}" onclick="selectCharInPortal(${c.id}, this)">
      <div style="width: 32px; height: 32px; border-radius: 50%; background: var(--arcane-purple); color: #fff; font-weight: 800; display: flex; align-items: center; justify-content: center;">
        ${c.name.charAt(0)}
      </div>
      <div>
        <div style="font-size: 14px; font-weight: 800; color: #fff;">${c.name}</div>
        <div style="font-size: 11px; color: var(--text-secondary);">${c.classLevel}</div>
      </div>
    </div>
  `).join('');
}

window.selectCharInPortal = function(id, elem) {
  document.querySelectorAll('.portal-char-item').forEach(el => el.classList.remove('selected'));
  if (elem) elem.classList.add('selected');

  const found = gameState.charactersList.find(c => c.id === id);
  if (found) {
    gameState.character.name = found.name;
    gameState.character.raceClass = found.classLevel;
  }
};

window.handleEnterAppFromPortal = function() {
  currentRole = selectedPortalRole;
  localStorage.setItem('grimoire_role', currentRole);

  const codeInput = document.getElementById('tableCodeInput');
  if (codeInput && codeInput.value.trim()) {
    currentTableCode = codeInput.value.trim().toUpperCase();
  }

  connectTableSession(currentTableCode);

  document.getElementById('portalOverlay').classList.add('hidden');
  activeTab = (currentRole === 'DM') ? 'campaign' : 'character';
  renderAll();
};

window.openPortalScreen = function() {
  document.getElementById('portalOverlay').classList.remove('hidden');
};

window.generateNewTableCode = function() {
  const randomCode = 'MESA-' + Math.random().toString(36).substring(2, 7).toUpperCase();
  document.getElementById('tableCodeInput').value = randomCode;
};

// 7. Modals & Settings
window.openSettingsModal = function() {
  document.getElementById('settingsModal').classList.add('open');
};

window.closeModal = function(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove('open');
};

window.toggleAccordion = function(bodyId) {
  const body = document.getElementById(bodyId);
  if (body) body.classList.toggle('open');
};

window.exportDataJson = function() {
  const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(gameState));
  const downloadAnchor = document.createElement('a');
  downloadAnchor.setAttribute("href", dataStr);
  downloadAnchor.setAttribute("download", `grimoire_backup_${currentTableCode}.json`);
  document.body.appendChild(downloadAnchor);
  downloadAnchor.click();
  downloadAnchor.remove();
};

window.importDataJson = function() {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'application/json';
  input.onchange = e => {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onload = event => {
      try {
        gameState = JSON.parse(event.target.result);
        saveStateToFirebase();
        renderAll();
        alert('Ficha/Mesa importada com sucesso!');
      } catch (err) {
        alert('Erro ao importar JSON.');
      }
    };
    reader.readAsText(file);
  };
  input.click();
};

// Combat Functions
window.nextCombatTurn = function() {
  const combatants = gameState.combatants || [];
  if (combatants.length === 0) return;

  let currentIdx = combatants.findIndex(c => c.active);
  if (currentIdx !== -1) combatants[currentIdx].active = false;

  let nextIdx = (currentIdx + 1) % combatants.length;
  combatants[nextIdx].active = true;

  renderCombat();
  saveStateToFirebase();
};

function renderCombat() {
  const container = document.getElementById('combatList');
  if (!container) return;

  const combatants = gameState.combatants || [];
  container.innerHTML = combatants.map(c => `
    <div style="background: var(--arcane-surface-variant); border: 1px solid ${c.active ? 'var(--mystic-gold)' : 'var(--arcane-border)'}; border-radius: 12px; padding: 12px; margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center;">
      <div>
        <div style="font-size: 15px; font-weight: 700; color: ${c.active ? 'var(--mystic-gold)' : '#fff'};">
          ${c.active ? '⚡ ' : ''}${c.name}
        </div>
        <div style="font-size: 12px; color: var(--text-secondary);">Iniciativa: <strong>${c.init}</strong> • ${c.type}</div>
      </div>
      <div style="font-size: 14px; font-weight: 800; color: var(--health-green);">${c.hp}/${c.maxHp} HP</div>
    </div>
  `).join('');
}

// Master All Render
function renderAll() {
  renderHeaderAndNav();
  renderCharacter();
  renderInventory();
  renderSpells();
  renderCombat();
  renderPortalCharList();
}

// Boot Application
function bootApp() {
  renderAll();

  // If table code exists, connect
  if (currentTableCode) {
    connectTableSession(currentTableCode);
  }
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', bootApp);
} else {
  bootApp();
}
