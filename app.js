const tierMap = [
  { max: 19, name: 'Speck', state: 'Dormant', accent: '#8ad8ff' },
  { max: 39, name: 'Blob', state: 'Dormant', accent: '#8ef0d4' },
  { max: 59, name: 'Imp', state: 'Stirring', accent: '#ffb07f' },
  { max: 79, name: 'Goblin', state: 'Awake', accent: '#ffc66a' },
  { max: 99, name: 'Brute', state: 'Awake', accent: '#ff9d7d' },
  { max: 119, name: 'Stalker', state: 'Rampage', accent: '#b397ff' },
  { max: 139, name: 'Warden', state: 'Rampage', accent: '#7fd5ff' },
  { max: 159, name: 'Behemoth', state: 'Rampage', accent: '#72e0a7' },
  { max: 179, name: 'Wraith', state: 'Feral', accent: '#e7b2ff' },
  { max: 200, name: 'Titan', state: 'Feral', accent: '#ff7d7d' }
];

const gainMeta = {
  Raga: { glyph: 'R', color: '#7fe5b8' },
  Nalar: { glyph: 'N', color: '#8ad8ff' },
  Karya: { glyph: 'K', color: '#ffc66a' },
  Harta: { glyph: 'H', color: '#ffd07d' },
  Ikatan: { glyph: 'I', color: '#d9a8ff' },
  Jiwa: { glyph: 'J', color: '#ff9ab0' }
};

let tasks = [
  { title: 'Bayar tagihan listrik', icon: 'T', priority: 'High', state: 'Alert', gain: 'Harta', due: 'Tonight', allocation: { Harta: 70, Jiwa: 20, Raga: 10 }, threat: 'Tier 4' },
  { title: 'Menulis proposal klien', icon: 'P', priority: 'Critical', state: 'Alert', gain: 'Karya', due: 'Tomorrow', allocation: { Karya: 75, Nalar: 15, Ikatan: 10 }, threat: 'Tier 5' },
  { title: 'Lari pagi', icon: 'R', priority: 'Medium', state: 'Calm', gain: 'Raga', due: 'This week', allocation: { Raga: 65, Ikatan: 25, Jiwa: 10 }, threat: 'Tier 3' },
  { title: 'Belajar bahasa Jepang', icon: 'B', priority: 'Low', state: 'Dormant', gain: 'Nalar', due: 'Later', allocation: { Nalar: 80, Karya: 10, Jiwa: 10 }, threat: 'Tier 2' }
];

const difficultyInput = document.getElementById('difficulty');
const impactInput = document.getElementById('impact');
const difficultyValue = document.getElementById('difficulty-value');
const impactValue = document.getElementById('impact-value');
const monsterName = document.getElementById('monster-name');
const tierPill = document.getElementById('tier-pill');
const monsterAvatar = document.getElementById('monster-avatar');
const monsterState = document.getElementById('monster-state');
const monsterScore = document.getElementById('monster-score');
const monsterExp = document.getElementById('monster-exp');
const radarShape = document.getElementById('radar-shape');
const taskList = document.getElementById('task-list');
const detailGain = document.getElementById('detail-gain');
const detailState = document.getElementById('detail-state');
const detailTitle = document.getElementById('detail-title');
const detailDue = document.getElementById('detail-due');
const detailThreat = document.getElementById('detail-threat');
const detailExp = document.getElementById('detail-exp');
const gainAllocation = document.getElementById('gain-allocation');
const toggleDetail = document.getElementById('toggle-detail');
const gainOverview = document.getElementById('gain-overview');
const quickAddForm = document.getElementById('quick-add-form');
const quickAddToggle = document.getElementById('quick-add-toggle');
const newTaskTitle = document.getElementById('new-task-title');
const newTaskGain = document.getElementById('new-task-gain');
const newTaskDue = document.getElementById('new-task-due');

let selectedTaskIndex = 0;

function getThreatTier(score) {
  const clamped = Math.max(0, Math.min(200, score));
  const tierIndex = Math.min(tierMap.length - 1, Math.floor(clamped / 20));
  return tierMap[tierIndex];
}

function applyMonsterMorph(tier, score) {
  const accent = tier.accent;
  const tierIndex = tierMap.indexOf(tier);
  const stateClass = tier.state.toLowerCase().replace(/\s+/g, '-');
  const shapeClass = [
    'dust', 'blob', 'imp', 'goblin', 'brute', 'stalker', 'warden', 'behemoth', 'wraith', 'titan'
  ][Math.min(tierIndex, 9)];
  const scale = 0.82 + (score / 200) * 0.8;
  const angle = (score / 200) * 14 - 7;

  monsterAvatar.className = `monster shape-${shapeClass} ${stateClass}`;
  monsterAvatar.style.setProperty('--monster-scale', scale.toFixed(3));
  monsterAvatar.style.setProperty('--monster-tilt', `${angle.toFixed(1)}deg`);
  monsterAvatar.style.setProperty('--monster-glow', `${accent}66`);
  monsterAvatar.style.setProperty('--horn-left-rotation', `${-14 - (score / 18)}deg`);
  monsterAvatar.style.setProperty('--horn-right-rotation', `${14 + (score / 18)}deg`);
  monsterAvatar.style.setProperty('--eye-size', `${22 - tierIndex * 0.9}px`);
  monsterAvatar.style.setProperty('--eye-offset', `${34 - tierIndex * 0.8}px`);
  monsterAvatar.style.setProperty('--snout-width', `${42 + tierIndex * 3.5}px`);
  monsterAvatar.style.setProperty('--snout-height', `${18 + tierIndex * 0.8}px`);
  monsterAvatar.style.background = `linear-gradient(180deg, ${accent} 0%, #ef7a4f 100%)`;
  monsterAvatar.style.boxShadow = `0 18px 30px ${accent}66, inset 0 0 0 2px rgba(255,255,255,0.1)`;
}

function updateMonster() {
  const difficulty = Number(difficultyInput.value);
  const impact = Number(impactInput.value);
  const score = difficulty + impact;
  const tier = getThreatTier(score);
  const tierNumber = tierMap.indexOf(tier) + 1;
  const baseExp = (impact * 1.0) + (difficulty * 0.7);

  difficultyValue.textContent = difficulty;
  impactValue.textContent = impact;
  difficultyInput.style.setProperty('--_pct', `${difficulty}%`);
  impactInput.style.setProperty('--_pct', `${impact}%`);
  monsterName.textContent = tier.name;
  tierPill.textContent = `Tier ${tierNumber}`;
  monsterState.textContent = tier.state;
  monsterScore.textContent = String(score);
  monsterExp.textContent = String(Math.round(baseExp));

  applyMonsterMorph(tier, score);

  const polygonValues = [
    64 + (difficulty / 100) * 24,
    52 + (impact / 100) * 20,
    66 + (difficulty / 100) * 16,
    58 + (impact / 100) * 18,
    72 + (difficulty / 100) * 10,
    48 + (impact / 100) * 22,
  ];

  radarShape.style.clipPath = `polygon(${polygonValues.map((value, index) => {
    const angle = (index / 6) * Math.PI * 2 - Math.PI / 2;
    const radius = 50 + value * 0.5;
    const x = 50 + Math.cos(angle) * radius;
    const y = 50 + Math.sin(angle) * radius;
    return `${x}% ${y}%`;
  }).join(', ')})`;
}

function renderGainAllocation(task) {
  const entries = Object.entries(task.allocation || {});
  gainAllocation.innerHTML = entries.map(([gain, value]) => {
    return `
      <div class="allocation-row">
        <div class="allocation-label-row">
          <span>${gain}</span>
          <strong>${value}%</strong>
        </div>
        <div class="allocation-bar">
          <span style="width: ${value}%"></span>
        </div>
      </div>
    `;
  }).join('');
}

function renderTaskDetail() {
  const task = tasks[selectedTaskIndex] || tasks[0];
  detailGain.textContent = task.gain;
  detailState.textContent = task.state;
  detailTitle.textContent = task.title;
  detailDue.textContent = task.due;
  detailThreat.textContent = task.threat;
  detailExp.textContent = String(Math.round((Number(impactInput.value) * 1.0) + (Number(difficultyInput.value) * 0.7)));
  renderGainAllocation(task);
}

function renderGainOverview() {
  const entries = Object.entries(gainMeta);
  gainOverview.innerHTML = entries.map(([name, meta]) => `
    <div class="gain-chip">
      <span class="gain-icon" style="background:${meta.color}22; color:${meta.color};">${meta.glyph}</span>
      <div>
        <strong>${name}</strong>
        <small>Level 12</small>
      </div>
      <span class="gain-score">68%</span>
    </div>
  `).join('');
}

function renderTasks() {
  taskList.innerHTML = tasks.map((task, index) => {
    const alertClass = task.state === 'Alert' ? 'alert' : '';
    const isSelected = index === selectedTaskIndex ? 'selected' : '';
    return `
      <div class="task-item ${alertClass} ${isSelected}" data-index="${index}">
        <div class="task-tag">${task.icon}</div>
        <div class="task-main">
          <div>${task.title}</div>
          <div class="task-meta">
            <span>${task.gain}</span>
            <span>${task.state}</span>
            <span>${task.due}</span>
          </div>
        </div>
        <div class="task-priority">${task.priority}</div>
      </div>
    `;
  }).join('');

  taskList.querySelectorAll('.task-item').forEach(item => {
    item.addEventListener('click', () => {
      selectedTaskIndex = Number(item.dataset.index);
      renderTasks();
      renderTaskDetail();
    });
  });
}

[difficultyInput, impactInput].forEach(input => {
  input.addEventListener('input', () => {
    updateMonster();
    renderTaskDetail();
  });
});

toggleDetail.addEventListener('click', () => {
  const detailModule = document.getElementById('task-detail-module');
  detailModule.classList.toggle('collapsed');
  toggleDetail.textContent = detailModule.classList.contains('collapsed') ? 'Closed' : 'Open';
});

quickAddToggle.addEventListener('click', () => {
  const quickAddModule = document.getElementById('quick-add-module');
  quickAddModule.classList.toggle('collapsed');
  quickAddToggle.textContent = quickAddModule.classList.contains('collapsed') ? 'Hide' : 'Add';
});

quickAddForm.addEventListener('submit', event => {
  event.preventDefault();
  const title = newTaskTitle.value.trim() || 'Task baru';
  const gain = newTaskGain.value || 'Nalar';
  const due = newTaskDue.value || 'Tonight';

  const secondaryGains = Object.keys(gainMeta).filter(name => name !== gain).slice(0, 2);
  const allocation = { [gain]: 70 };
  if (secondaryGains[0]) allocation[secondaryGains[0]] = 20;
  if (secondaryGains[1]) allocation[secondaryGains[1]] = 10;

  tasks.unshift({
    title,
    icon: gainMeta[gain]?.glyph || 'T',
    priority: 'Medium',
    state: 'Calm',
    gain,
    due,
    allocation,
    threat: 'Tier 3'
  });

  selectedTaskIndex = 0;
  renderTasks();
  renderTaskDetail();
  quickAddForm.reset();
  newTaskTitle.value = 'Membaca buku 20 menit';
});

updateMonster();
renderGainOverview();
renderTasks();
renderTaskDetail();
