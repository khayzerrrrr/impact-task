'use strict';

/* ============================================================
   Constants — mirrors the formulas in lib/.../ImpactTaskDomain.kt
   so the prototype and the domain module agree on the same rules.
   ============================================================ */

const NOW = new Date();

const TIERS = [
  { num: 1, min: 0, max: 19, name: 'Speck', shape: 'dust' },
  { num: 2, min: 20, max: 39, name: 'Blob', shape: 'blob' },
  { num: 3, min: 40, max: 59, name: 'Imp', shape: 'imp' },
  { num: 4, min: 60, max: 79, name: 'Goblin', shape: 'goblin' },
  { num: 5, min: 80, max: 99, name: 'Brute', shape: 'brute' },
  { num: 6, min: 100, max: 119, name: 'Stalker', shape: 'stalker' },
  { num: 7, min: 120, max: 139, name: 'Warden', shape: 'warden' },
  { num: 8, min: 140, max: 159, name: 'Behemoth', shape: 'behemoth' },
  { num: 9, min: 160, max: 179, name: 'Wraith', shape: 'wraith' },
  { num: 10, min: 180, max: 200, name: 'Titan', shape: 'titan' }
];

const GAINS = [
  { id: 'raga', label: 'Raga', sub: 'Fisik & kesehatan', glyph: 'R', color: '#4ade80' },
  { id: 'nalar', label: 'Nalar', sub: 'Ilmu & mental', glyph: 'N', color: '#5eead4' },
  { id: 'karya', label: 'Karya', sub: 'Karier & keahlian', glyph: 'K', color: '#fbbf24' },
  { id: 'harta', label: 'Harta', sub: 'Finansial', glyph: 'H', color: '#fb923c' },
  { id: 'ikatan', label: 'Ikatan', sub: 'Relasi & keluarga', glyph: 'I', color: '#a78bfa' },
  { id: 'jiwa', label: 'Jiwa', sub: 'Spiritual & makna', glyph: 'J', color: '#fb7185' }
];

const STATE_WEIGHT = { feral: 5, rampage: 4, awake: 3, stirring: 2, dormant: 1 };
const STATE_LABEL = { dormant: 'Tidur', stirring: 'Menggeliat', awake: 'Bangun', rampage: 'Mengamuk', feral: 'Liar' };
const STATE_TONE = { dormant: 'blue', stirring: 'amber', awake: 'cyan', rampage: 'rose', feral: 'violet' };

/* ============================================================
   Domain formulas
   ============================================================ */

function clamp(value, min, max) {
  return Math.max(min, Math.min(max, value));
}

function tierFromScore(score) {
  const clamped = clamp(score, 0, 200);
  const idx = clamp(Math.floor(clamped / 20), 0, TIERS.length - 1);
  return TIERS[idx];
}

function monsterStateFromDue(due, now) {
  if (!due) return 'dormant';
  const remainingHours = (due.getTime() - now.getTime()) / 3600000;
  if (remainingHours > 72) return 'dormant';
  if (remainingHours > 24) return 'stirring';
  if (remainingHours >= 0) return 'awake';
  if (remainingHours > -72) return 'rampage';
  return 'feral';
}

function baseExperience(difficulty, impact) {
  return impact * 1.0 + difficulty * 0.7;
}

function timeMultiplier(due, now) {
  if (!due) return 1.0;
  const remainingHours = (due.getTime() - now.getTime()) / 3600000;
  if (remainingHours >= 24) return 1.15;
  if (remainingHours >= 0) return 1.0;
  const lateDays = Math.ceil(-remainingHours / 24);
  return Math.max(0.4, 1 - 0.05 * lateDays);
}

function balanceMultiplier(targetGainId, gainLevels) {
  const levels = Object.values(gainLevels);
  if (!levels.length) return 1.0;
  const lowest = Math.min(...levels);
  return gainLevels[targetGainId] === lowest ? 1.15 : 1.0;
}

function streakMultiplier(streakDays) {
  return 1 + Math.min(0.2, 0.02 * streakDays);
}

function distributeExperience(total, allocations) {
  const entries = Object.entries(allocations).filter(([, pct]) => pct > 0);
  const sum = entries.reduce((acc, [, pct]) => acc + pct, 0);
  if (!sum) return {};
  const result = {};
  entries.forEach(([gainId, pct]) => {
    result[gainId] = Math.round((total * pct) / sum);
  });
  return result;
}

function expForLevel(n) {
  if (n <= 0) return 0;
  return Math.round(100 * Math.pow(n, 1.6));
}

function levelProgress(totalExp) {
  let level = 0;
  while (level < 200 && expForLevel(level + 1) <= totalExp) level++;
  const floorExp = expForLevel(level);
  const ceilExp = expForLevel(level + 1);
  const pct = ceilExp > floorExp ? (totalExp - floorExp) / (ceilExp - floorExp) : 0;
  return { level, floorExp, ceilExp, pct: clamp(pct, 0, 1) };
}

/* ============================================================
   Mock state
   ============================================================ */

function hoursFromNow(h) {
  return new Date(NOW.getTime() + h * 3600000);
}

const state = {
  gains: {
    raga: { totalExp: 1500 },
    nalar: { totalExp: 700 },
    karya: { totalExp: 3200 },
    harta: { totalExp: 1100 },
    ikatan: { totalExp: 1900 },
    jiwa: { totalExp: 980 }
  },
  tasks: [
    {
      id: 't1', title: 'Bayar tagihan listrik', notes: 'Sebelum listrik diputus.',
      difficulty: 10, impact: 22, due: hoursFromNow(-4), type: 'bertenggat', estMinutes: 10,
      allocations: { harta: 80, jiwa: 20 }, subtasks: [], status: 'active', createdAt: hoursFromNow(-72)
    },
    {
      id: 't2', title: 'Presentasi klien besok', notes: 'Deck Q3 review + demo produk.',
      difficulty: 60, impact: 70, due: hoursFromNow(10), type: 'terjadwal', estMinutes: 90,
      allocations: { karya: 75, nalar: 15, ikatan: 10 },
      subtasks: [
        { title: 'Susun slide', done: true },
        { title: 'Latihan sekali', done: false },
        { title: 'Cek proyektor', done: false }
      ],
      status: 'active', createdAt: hoursFromNow(-48)
    },
    {
      id: 't3', title: 'Servis motor', notes: '',
      difficulty: 30, impact: 40, due: hoursFromNow(40), type: 'bertenggat', estMinutes: 60,
      allocations: { harta: 60, raga: 40 },
      subtasks: [{ title: 'Ganti oli', done: false }, { title: 'Cek rem', done: false }],
      status: 'active', createdAt: hoursFromNow(-20)
    },
    {
      id: 't4', title: 'Belajar bahasa Jepang', notes: 'Lanjut Genki bab 4.',
      difficulty: 95, impact: 95, due: null, type: 'lentur', estMinutes: 45,
      allocations: { nalar: 80, karya: 10, jiwa: 10 }, subtasks: [], status: 'active', createdAt: hoursFromNow(-200)
    },
    {
      id: 't5', title: 'Lari pagi 5K', notes: '',
      difficulty: 20, impact: 35, due: hoursFromNow(3), type: 'terjadwal', estMinutes: 30,
      allocations: { raga: 90, ikatan: 10 }, subtasks: [], status: 'active', createdAt: hoursFromNow(-10)
    },
    {
      id: 't6', title: 'Kirim kabar ke orang tua', notes: '',
      difficulty: 15, impact: 45, due: hoursFromNow(30), type: 'bertenggat', estMinutes: 15,
      allocations: { ikatan: 90, jiwa: 10 }, subtasks: [], status: 'active', createdAt: hoursFromNow(-30)
    },
    {
      id: 't7', title: 'Journaling malam', notes: '',
      difficulty: 10, impact: 20, due: hoursFromNow(1), type: 'bertenggat', estMinutes: 15,
      allocations: { jiwa: 85, nalar: 15 }, subtasks: [], status: 'active', createdAt: hoursFromNow(-5)
    },
    {
      id: 't8', title: 'Nabung dana darurat bulan ini', notes: '',
      difficulty: 25, impact: 65, due: hoursFromNow(90), type: 'bertenggat', estMinutes: 20,
      allocations: { harta: 90, jiwa: 10 }, subtasks: [], status: 'active', createdAt: hoursFromNow(-60)
    }
  ],
  bestiary: {
    1: { defeated: 12, best: 2 }, 2: { defeated: 9, best: 4 }, 3: { defeated: 6, best: 12 },
    4: { defeated: 4, best: 25 }, 5: { defeated: 3, best: 40 }, 6: { defeated: 1, best: 55 },
    7: { defeated: 0, best: null }, 8: { defeated: 0, best: null }, 9: { defeated: 0, best: null }, 10: { defeated: 0, best: null }
  },
  stats: {
    avgOverrun: 1.8, sampleSize: 24,
    completionByTier: [96, 90, 82, 74, 60, 45, 30, 18, 8, 2],
    currentStreak: 6, longestStreak: 14,
    calendar: [1,1,1,0,1,1,1, 1,0,1,1,1,1,0, 1,1,1,1,0,1,1, 1,1,1,1,1,1,1]
  },
  settings: {
    quietStart: '22:00', quietEnd: '07:00', notifBudget: 6, expDecay: true, theme: 'dark', language: 'id'
  },
  ui: {
    authenticated: false,
    taskFilter: 'all',
    createForm: { primaryGain: 'raga', secondaryGain: '', secondaryPct: 0, tertiaryGain: '', tertiaryPct: 0, split: false, difficulty: 50, impact: 50, type: 'bertenggat' },
    timer: null
  }
};

let timerHandle = null;
let nextId = state.tasks.length + 1;

/* ============================================================
   DOM helpers
   ============================================================ */

const viewRoot = document.getElementById('view-root');
const bottomNav = document.getElementById('bottom-nav');
const toastEl = document.getElementById('toast');
let toastTimeout = null;

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, ch => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[ch]));
}

function toast(message) {
  toastEl.textContent = message;
  toastEl.classList.add('show');
  clearTimeout(toastTimeout);
  toastTimeout = setTimeout(() => toastEl.classList.remove('show'), 2400);
}

function gainById(id) {
  return GAINS.find(g => g.id === id);
}

function taskById(id) {
  return state.tasks.find(t => t.id === id);
}

function gainLevelsMap() {
  const map = {};
  GAINS.forEach(g => { map[g.id] = levelProgress(state.gains[g.id].totalExp).level; });
  return map;
}

function formatDue(due) {
  if (!due) return 'Tanpa tenggat';
  const diffH = (due.getTime() - NOW.getTime()) / 3600000;
  if (diffH < 0) {
    const hrs = Math.round(-diffH);
    return hrs < 24 ? `Terlewat ${hrs} jam` : `Terlewat ${Math.round(hrs / 24)} hari`;
  }
  if (diffH < 24) return `${Math.round(diffH)} jam lagi`;
  return `${Math.round(diffH / 24)} hari lagi`;
}

function icon(name, size = 18) {
  const paths = {
    back: '<path d="M15 5 8 12l7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    gear: '<circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.8" fill="none"/><path d="M12 3v2.2M12 18.8V21M21 12h-2.2M5.2 12H3M18.4 5.6l-1.55 1.55M7.15 16.85l-1.55 1.55M18.4 18.4l-1.55-1.55M7.15 7.15 5.6 5.6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" fill="none"/>',
    check: '<path d="M5 12.5 10 17l9-10" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    timer: '<circle cx="12" cy="13" r="8" stroke="currentColor" stroke-width="1.8" fill="none"/><path d="M12 9v4l3 2M9 2h6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" fill="none"/>',
    trash: '<path d="M4 7h16M9 7V4.8c0-.4.4-.8.8-.8h4.4c.4 0 .8.4.8.8V7M6.5 7l.8 12.2c0 .5.5.8 1 .8h7.4c.5 0 .9-.3 1-.8L17.5 7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    download: '<path d="M12 3v12m0 0-4-4m4 4 4-4M5 19h14" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    lock: '<rect x="5" y="10.5" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.7" fill="none"/><path d="M8 10.5V8a4 4 0 0 1 8 0v2.5" stroke="currentColor" stroke-width="1.7" fill="none"/>',
    flame: '<path d="M12 2c1 3-2.5 4-2.5 7.2A2.5 2.5 0 0 0 12 11.7a2.5 2.5 0 0 0 2.5-2.5c0-1-.5-1.6-.5-1.6 2 1 3.5 3.6 3.5 6.1a5.5 5.5 0 1 1-11 0c0-3.6 2.5-5 3.5-7 .5-1 1.5-2.5 2-4.7Z" stroke="currentColor" stroke-width="1.5" fill="none"/>',
    google: '<path d="M21 12.2c0-.7-.06-1.4-.18-2H12v3.8h5.1a4.4 4.4 0 0 1-1.9 2.9v2.4h3a9 9 0 0 0 2.8-6.6Z" fill="#4285F4"/><path d="M12 21c2.4 0 4.5-.8 6-2.2l-3-2.4c-.8.6-1.9.9-3 .9-2.3 0-4.3-1.6-5-3.7H4v2.4A9 9 0 0 0 12 21Z" fill="#34A853"/><path d="M7 13.6a5.4 5.4 0 0 1 0-3.4V7.8H4a9 9 0 0 0 0 8.2l3-2.4Z" fill="#FBBC05"/><path d="M12 6.6c1.3 0 2.5.4 3.4 1.3l2.6-2.6A9 9 0 0 0 4 7.8l3 2.4c.7-2.1 2.7-3.6 5-3.6Z" fill="#EA4335"/>'
  };
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" aria-hidden="true">${paths[name] || ''}</svg>`;
}

/* ============================================================
   Shared fragments
   ============================================================ */

function monsterFragment(task, { size = 132, extraClass = '' } = {}) {
  const score = clamp(task.difficulty + task.impact, 0, 200);
  const tier = tierFromScore(score);
  const monsterState = monsterStateFromDue(task.due, NOW);
  const scale = 0.82 + (score / 200) * 0.8;
  const angle = (score / 200) * 14 - 7;
  const tierIdx = tier.num - 1;
  const running = state.ui.timer && state.ui.timer.taskId === task.id;
  return `
    <div class="monster-wrap" style="--monster-size:${size}px">
      <span class="monster-ring"></span>
      <div class="monster shape-${tier.shape} ${monsterState} ${running ? 'restrained' : ''} ${extraClass}"
        style="--monster-scale:${scale.toFixed(3)};--monster-tilt:${angle.toFixed(1)}deg;
               --horn-left-rotation:${(-14 - score / 18).toFixed(1)}deg;--horn-right-rotation:${(14 + score / 18).toFixed(1)}deg;
               --eye-size:${(22 - tierIdx * 0.9).toFixed(1)}px;--eye-offset:${(34 - tierIdx * 0.8).toFixed(1)}px;
               --snout-width:${(42 + tierIdx * 3.5).toFixed(1)}px;--snout-height:${(18 + tierIdx * 0.8).toFixed(1)}px;">
        <span class="horn left"></span><span class="horn right"></span>
        <span class="eye left"></span><span class="eye right"></span>
        <span class="snout"></span>
        ${running ? '<span class="restrain-badge">⛓</span>' : ''}
      </div>
    </div>`;
}

function stateBadge(monsterState) {
  const tone = STATE_TONE[monsterState];
  return `<span class="state-badge tone-${tone}">${STATE_LABEL[monsterState]}</span>`;
}

function tierBadge(tier) {
  return `<span class="tier-badge">Tier ${tier.num} · ${tier.name}</span>`;
}

function allocationBars(allocations) {
  return `<div class="gain-allocation">${Object.entries(allocations).map(([gid, pct]) => {
    const g = gainById(gid);
    return `
      <div class="allocation-row">
        <div class="allocation-label-row">
          <span><span class="dot" style="background:${g.color}"></span>${g.label}</span>
          <strong>${pct}%</strong>
        </div>
        <div class="allocation-bar"><span style="width:${pct}%;background:linear-gradient(90deg, ${g.color}, ${g.color}99)"></span></div>
      </div>`;
  }).join('')}</div>`;
}

function radarShapeStyle(levelsByGain) {
  const maxLevel = Math.max(1, ...Object.values(levelsByGain));
  const values = GAINS.map(g => 30 + (levelsByGain[g.id] / maxLevel) * 65);
  const points = values.map((v, i) => {
    const angle = (i / 6) * Math.PI * 2 - Math.PI / 2;
    const radius = 8 + v * 0.5;
    const x = 50 + Math.cos(angle) * radius;
    const y = 50 + Math.sin(angle) * radius;
    return `${x}% ${y}%`;
  }).join(', ');
  return `clip-path: polygon(${points});`;
}

function radarChart(levelsByGain) {
  return `
    <div class="radar-chart">
      <span class="ring ring-1"></span><span class="ring ring-2"></span>
      <span class="ring ring-3"></span><span class="ring ring-4"></span>
      <span class="radar-shape" style="${radarShapeStyle(levelsByGain)}"></span>
      <span class="radar-core"></span>
    </div>
    <div class="gains-grid">
      ${GAINS.map(g => `<span><span class="dot" style="background:${g.color}"></span>${g.label} <b>${levelsByGain[g.id]}</b></span>`).join('')}
    </div>`;
}

function taskListItem(task, priority) {
  const tier = tierFromScore(task.difficulty + task.impact);
  const monsterState = monsterStateFromDue(task.due, NOW);
  const primaryGainId = Object.entries(task.allocations).sort((a, b) => b[1] - a[1])[0][0];
  const g = gainById(primaryGainId);
  return `
    <div class="task-item tone-${STATE_TONE[monsterState]}" data-action="open-task" data-id="${task.id}">
      <div class="task-tag" style="background:${g.color}1f;border-color:${g.color}33;color:${g.color}">${g.glyph}</div>
      <div class="task-main">
        <div>${escapeHtml(task.title)}</div>
        <div class="task-meta">
          <span>${g.label}</span>
          <span>Tier ${tier.num}</span>
          <span>${formatDue(task.due)}</span>
        </div>
      </div>
      <div class="task-priority">${STATE_LABEL[monsterState]}</div>
    </div>`;
}

/* ============================================================
   View: Login
   ============================================================ */

function viewLogin() {
  return `
    <div class="login-view">
      <div class="login-badge">${icon('flame', 26)}</div>
      <h1 class="login-title">Impact<span class="dot-accent">.</span></h1>
      <p class="login-sub">Task manager di mana setiap pekerjaan terhubung ke enam aspek hidupmu, dan melahirkan monster yang makin ganas kalau ditunda.</p>

      <button class="google-btn" type="button" data-action="login">
        ${icon('google', 20)} <span>Lanjutkan dengan Google</span>
      </button>

      <div class="login-divider"><span>atau</span></div>

      <form class="login-form" data-action="login-form">
        <label><span>Email</span><input type="email" placeholder="kamu@email.com" value="rezafahrul30@gmail.com" /></label>
        <label><span>Kata sandi</span><input type="password" placeholder="••••••••" value="••••••••" /></label>
        <button type="submit" class="primary-action">Masuk</button>
      </form>

      <p class="login-foot">Dengan masuk, progres Gains &amp; EXP kamu tersimpan otomatis di semua perangkat.</p>
    </div>`;
}

/* ============================================================
   View: Home
   ============================================================ */

function viewHome() {
  const levels = gainLevelsMap();
  const active = state.tasks.filter(t => t.status === 'active');
  const sorted = [...active].sort((a, b) => priorityScoreOf(b) - priorityScoreOf(a));
  const loose = active.filter(t => ['rampage', 'feral'].includes(monsterStateFromDue(t.due, NOW)));
  const topTasks = sorted.slice(0, 3);
  const lowestGainId = GAINS.reduce((a, b) => levels[a.id] <= levels[b.id] ? a : b).id;
  const lowestGain = gainById(lowestGainId);

  return `
    <div class="view-header home-header">
      <div>
        <div class="eyebrow">Jumat · 29 Agu</div>
        <h1>Impact<span class="dot-accent">.</span></h1>
      </div>
      <button class="icon-btn" type="button" data-action="go" data-route="settings" aria-label="Setelan">${icon('gear', 20)}</button>
    </div>

    <section class="quick-pills">
      <div class="pill ${loose.length ? 'danger' : ''}"><span class="pulse ${loose.length ? '' : 'success'}"></span>${loose.length} monster mengamuk</div>
      <div class="pill"><span class="pulse success"></span>6 Gains terlacak</div>
    </section>

    ${loose.length ? `
    <section class="rampage-banner" data-action="open-task" data-id="${loose[0].id}">
      <div>
        <div class="eyebrow">Butuh perhatian sekarang</div>
        <strong>${escapeHtml(loose[0].title)}</strong>
        <span>${STATE_LABEL[monsterStateFromDue(loose[0].due, NOW)]} · ${formatDue(loose[0].due)}</span>
      </div>
      ${monsterFragment(loose[0], { size: 64 })}
    </section>` : ''}

    <section class="balance-card">
      <div class="card-title-row">
        <div><div class="eyebrow">Gains</div><h3>Balance</h3></div>
        <span class="live-pill"><span class="live-dot"></span>live</span>
      </div>
      <div class="radar-panel">${radarChart(levels)}</div>
      <p class="balance-hint"><span class="dot" style="background:${lowestGain.color}"></span><b>${lowestGain.label}</b> paling tertinggal — task ke sana dapat bonus <b>+15% EXP</b> minggu ini.</p>
    </section>

    <section class="tasks">
      <div class="card-title-row">
        <div><div class="eyebrow">Prioritas</div><h3>Task hari ini</h3></div>
        <button class="filter-btn" type="button" data-action="go" data-route="tasks">Semua</button>
      </div>
      <div class="task-list">${topTasks.map(t => taskListItem(t)).join('') || emptyState('Tidak ada task aktif.')}</div>
    </section>`;
}

function priorityScoreOf(task) {
  const tier = tierFromScore(task.difficulty + task.impact);
  const st = monsterStateFromDue(task.due, NOW);
  return STATE_WEIGHT[st] * 1000 + tier.num * 10;
}

function emptyState(message) {
  return `<div class="empty-state">${escapeHtml(message)}</div>`;
}

/* ============================================================
   View: Tasks
   ============================================================ */

function viewTasks() {
  const filter = state.ui.taskFilter;
  let list = state.tasks.filter(t => t.status === 'active');
  if (filter !== 'all') list = list.filter(t => Boolean(t.allocations[filter]));
  list = list.sort((a, b) => {
    const diff = priorityScoreOf(b) - priorityScoreOf(a);
    if (diff !== 0) return diff;
    if (!a.due && !b.due) return 0;
    if (!a.due) return 1;
    if (!b.due) return -1;
    return a.due - b.due;
  });

  const chips = [{ id: 'all', label: 'Semua' }, ...GAINS.map(g => ({ id: g.id, label: g.label }))];

  return `
    <div class="view-header">
      <div><div class="eyebrow">Prioritas otomatis</div><h1>Daftar task</h1></div>
    </div>

    <div class="chip-row">
      ${chips.map(c => `<button class="chip ${filter === c.id ? 'active' : ''}" type="button" data-action="filter-tasks" data-filter="${c.id}">${c.label}</button>`).join('')}
    </div>

    <div class="task-list roomy">${list.map(t => taskListItem(t)).join('') || emptyState('Tidak ada task pada filter ini.')}</div>`;
}

/* ============================================================
   View: Create task
   ============================================================ */

function viewCreate() {
  const f = state.ui.createForm;
  const score = clamp(f.difficulty + f.impact, 0, 200);
  const tier = tierFromScore(score);
  const baseExp = Math.round(baseExperience(f.difficulty, f.impact));
  const previewTask = { difficulty: f.difficulty, impact: f.impact, due: null };

  const typeMeta = {
    lentur: { label: 'Lentur', hint: 'Hanya estimasi durasi. Monster tidur sampai kamu beri tanggal.' },
    bertenggat: { label: 'Bertenggat', hint: 'Estimasi durasi + tanggal jatuh tempo.' },
    terjadwal: { label: 'Terjadwal', hint: 'Tanggal + jam mulai. Alarm tepat waktu untuk tier 7+.' }
  };

  return `
    <div class="view-header">
      <button class="icon-btn" type="button" data-action="go" data-route="tasks" aria-label="Kembali">${icon('back')}</button>
      <div><div class="eyebrow">Layar terpenting</div><h1>Buat task</h1></div>
    </div>

    <form class="create-form" data-action="submit-create">
      <label class="field"><span>Judul task</span><input required name="title" type="text" placeholder="mis. Kirim invoice klien" /></label>
      <label class="field"><span>Catatan (opsional)</span><textarea name="notes" rows="2" placeholder="Detail tambahan..."></textarea></label>

      <section class="hero-card preview-card">
        <div class="hero-header">
          <div><div class="eyebrow">Preview monster</div><h2>${tier.name}</h2></div>
          ${tierBadge(tier)}
        </div>
        <div class="hero-body">
          ${monsterFragment(previewTask, { size: 118 })}
          <div class="mini-stats">
            <div><span>Score</span><strong id="preview-score">${score}</strong></div>
            <div><span>EXP dasar</span><strong id="preview-exp">${baseExp}</strong></div>
          </div>
        </div>

        <div class="slider-block">
          <div class="slider-row"><label for="difficulty">Kesulitan</label><strong id="difficulty-value">${f.difficulty}</strong></div>
          <input id="difficulty" name="difficulty" type="range" min="0" max="100" value="${f.difficulty}" style="--_pct:${f.difficulty}%" />
          <div class="slider-anchors"><span>20 otomatis</span><span>50 fokus</span><span>80 ditunda</span><span>100 menakutkan</span></div>
        </div>
        <div class="slider-block">
          <div class="slider-row"><label for="impact">Dampak</label><strong id="impact-value">${f.impact}</strong></div>
          <input id="impact" name="impact" type="range" min="0" max="100" value="${f.impact}" style="--_pct:${f.impact}%" />
          <div class="slider-anchors"><span>20 tak terasa</span><span>50 minggu ini</span><span>80 tahun ini</span><span>100 ubah arah</span></div>
        </div>
      </section>

      <div class="field">
        <span>Jenis waktu</span>
        <div class="segmented" role="tablist">
          ${Object.entries(typeMeta).map(([id, m]) => `<button type="button" class="seg ${f.type === id ? 'active' : ''}" data-action="set-type" data-type="${id}">${m.label}</button>`).join('')}
        </div>
        <p class="field-hint">${typeMeta[f.type].hint}</p>
      </div>

      ${f.type !== 'lentur' ? `
      <div class="field-row">
        <label class="field"><span>Tanggal</span><input name="dueDate" type="date" value="${defaultDateValue()}" /></label>
        <label class="field"><span>${f.type === 'terjadwal' ? 'Jam mulai' : 'Jam (opsional)'}</span><input name="dueTime" type="time" value="18:00" /></label>
      </div>` : `<label class="field"><span>Estimasi durasi (menit)</span><input name="estMinutes" type="number" min="5" step="5" value="30" /></label>`}

      <div class="field">
        <span>Primary Gain</span>
        <div class="gain-picker">
          ${GAINS.map(g => `<button type="button" class="gain-pick ${f.primaryGain === g.id ? 'active' : ''}" data-action="pick-primary-gain" data-gain="${g.id}" style="--gain-color:${g.color}">${g.glyph}<small>${g.label}</small></button>`).join('')}
        </div>
        <button type="button" class="link-btn" data-action="toggle-split">${f.split ? 'Sembunyikan pembagian' : 'Bagi ke Gains lain'}</button>
      </div>

      ${f.split ? splitFields(f) : ''}

      <button type="submit" class="primary-action">Buat task</button>
    </form>`;
}

function splitFields(f) {
  const others = id => GAINS.filter(g => g.id !== f.primaryGain && g.id !== id);
  return `
    <div class="split-block">
      <div class="split-row">
        <select data-action="set-secondary-gain">
          <option value="">Gain kedua…</option>
          ${others(f.tertiaryGain).map(g => `<option value="${g.id}" ${f.secondaryGain === g.id ? 'selected' : ''}>${g.label}</option>`).join('')}
        </select>
        <input type="range" min="0" max="40" value="${f.secondaryPct}" style="--_pct:${(f.secondaryPct / 40) * 100}%" data-action="set-secondary-pct" ${!f.secondaryGain ? 'disabled' : ''} />
        <strong>${f.secondaryGain ? f.secondaryPct + '%' : '—'}</strong>
      </div>
      <div class="split-row">
        <select data-action="set-tertiary-gain">
          <option value="">Gain ketiga…</option>
          ${others(f.secondaryGain).map(g => `<option value="${g.id}" ${f.tertiaryGain === g.id ? 'selected' : ''}>${g.label}</option>`).join('')}
        </select>
        <input type="range" min="0" max="30" value="${f.tertiaryPct}" style="--_pct:${(f.tertiaryPct / 30) * 100}%" data-action="set-tertiary-pct" ${!f.tertiaryGain ? 'disabled' : ''} />
        <strong>${f.tertiaryGain ? f.tertiaryPct + '%' : '—'}</strong>
      </div>
      <p class="field-hint">Sisa persentase otomatis masuk ke primary Gain.</p>
    </div>`;
}

function defaultDateValue() {
  const d = new Date(NOW.getTime() + 24 * 3600000);
  return d.toISOString().slice(0, 10);
}

function currentAllocations() {
  const f = state.ui.createForm;
  const secondary = f.split && f.secondaryGain && f.secondaryPct > 0 ? f.secondaryPct : 0;
  const tertiary = f.split && f.tertiaryGain && f.tertiaryPct > 0 ? f.tertiaryPct : 0;
  const used = secondary + tertiary;
  const alloc = { [f.primaryGain]: clamp(100 - used, 10, 100) };
  if (secondary) alloc[f.secondaryGain] = secondary;
  if (tertiary) alloc[f.tertiaryGain] = tertiary;
  return alloc;
}

/* ============================================================
   View: Task detail
   ============================================================ */

function viewTaskDetail(id) {
  const task = taskById(id);
  if (!task) return emptyState('Task tidak ditemukan.') + backLink('tasks');

  const tier = tierFromScore(task.difficulty + task.impact);
  const monsterState = monsterStateFromDue(task.due, NOW);
  const doneCount = task.subtasks.filter(s => s.done).length;
  const hp = task.subtasks.length ? Math.round((1 - doneCount / task.subtasks.length) * 100) : 100;
  const exp = Math.round(baseExperience(task.difficulty, task.impact) * timeMultiplier(task.due, NOW));
  const running = state.ui.timer && state.ui.timer.taskId === task.id;
  const allSubtasksDone = task.subtasks.length > 0 && doneCount === task.subtasks.length;

  return `
    <div class="view-header">
      <button class="icon-btn" type="button" data-action="go" data-route="tasks" aria-label="Kembali">${icon('back')}</button>
      <div><div class="eyebrow">Task detail</div><h1>${escapeHtml(task.title)}</h1></div>
    </div>

    <section class="hero-card">
      <div class="hero-header">
        <div><div class="eyebrow">Threat</div><h2>${tier.name}</h2></div>
        ${tierBadge(tier)}
      </div>
      <div class="hero-body detail-hero">
        ${monsterFragment(task, { size: 150 })}
        <div class="hp-block">
          <div class="hp-row"><span>HP</span><strong>${hp}%</strong></div>
          <div class="hp-bar"><span style="width:${hp}%"></span></div>
          ${stateBadge(monsterState)}
        </div>
      </div>

      <div class="detail-metrics">
        <div><span>Due</span><strong>${formatDue(task.due)}</strong></div>
        <div><span>EXP</span><strong>${exp}</strong></div>
        <div><span>Estimasi</span><strong>${task.estMinutes}m</strong></div>
      </div>

      <div class="timer-row">
        <span>${running ? formatElapsed(state.ui.timer.startedAt) : 'Sesi fokus belum mulai'}</span>
        <button class="filter-btn ${running ? 'danger' : ''}" type="button" data-action="${running ? 'stop-timer' : 'start-timer'}" data-id="${task.id}">
          ${icon('timer', 14)} ${running ? 'Selesai sesi' : 'Mulai'}
        </button>
      </div>
    </section>

    ${task.notes ? `<section class="module-card"><div class="eyebrow">Catatan</div><p class="notes-text">${escapeHtml(task.notes)}</p></section>` : ''}

    <section class="module-card">
      <div class="card-title-row"><div><div class="eyebrow">Checklist</div><h3>Subtask</h3></div></div>
      <div class="subtask-list">
        ${task.subtasks.map((s, i) => `
          <label class="subtask-item ${s.done ? 'done' : ''}">
            <input type="checkbox" ${s.done ? 'checked' : ''} data-action="toggle-subtask" data-id="${task.id}" data-index="${i}" />
            <span>${escapeHtml(s.title)}</span>
          </label>`).join('') || emptyState('Belum ada subtask.')}
      </div>
      <form class="subtask-form" data-action="add-subtask" data-id="${task.id}">
        <input type="text" name="subtaskTitle" placeholder="Tambah langkah…" />
        <button type="submit" class="filter-btn">Tambah</button>
      </form>
      ${allSubtasksDone ? `<button class="primary-action ghost" type="button" data-action="complete-task" data-id="${task.id}">Semua langkah selesai — tandai task ini selesai?</button>` : ''}
    </section>

    <section class="module-card">
      <div class="card-title-row"><div><div class="eyebrow">Pembagian</div><h3>Gain allocation</h3></div></div>
      ${allocationBars(task.allocations)}
    </section>

    <div class="detail-actions">
      <button class="primary-action" type="button" data-action="complete-task" data-id="${task.id}">${icon('check', 16)} Tandai selesai</button>
      <button class="ghost-action" type="button" data-action="cancel-task" data-id="${task.id}">Batalkan</button>
    </div>`;
}

function formatElapsed(startedAt) {
  const s = Math.max(0, Math.floor((Date.now() - startedAt) / 1000));
  const mm = String(Math.floor(s / 60)).padStart(2, '0');
  const ss = String(s % 60).padStart(2, '0');
  return `Berjalan ${mm}:${ss}`;
}

function backLink(route) {
  return `<button class="link-btn" type="button" data-action="go" data-route="${route}">Kembali</button>`;
}

/* ============================================================
   View: Gain detail
   ============================================================ */

function viewGainDetail(id) {
  const g = gainById(id);
  if (!g) return emptyState('Gain tidak ditemukan.') + backLink('home');
  const { level, floorExp, ceilExp, pct } = levelProgress(state.gains[g.id].totalExp);
  const contributing = state.tasks.filter(t => t.allocations[g.id]).sort((a, b) => (b.allocations[g.id] || 0) - (a.allocations[g.id] || 0)).slice(0, 5);

  return `
    <div class="view-header">
      <button class="icon-btn" type="button" data-action="go" data-route="home" aria-label="Kembali">${icon('back')}</button>
      <div><div class="eyebrow">Detail Gains</div><h1 style="color:${g.color}">${g.label}</h1></div>
    </div>

    <section class="hero-card">
      <div class="level-hero">
        <span class="level-icon" style="background:${g.color}22;color:${g.color}">${g.glyph}</span>
        <div>
          <div class="eyebrow">${g.sub}</div>
          <h2>Level ${level}</h2>
        </div>
      </div>
      <div class="level-bar"><span style="width:${(pct * 100).toFixed(0)}%;background:${g.color}"></span></div>
      <div class="level-caption"><span>${state.gains[g.id].totalExp} EXP</span><span>${ceilExp} EXP untuk lv ${level + 1}</span></div>
    </section>

    <section class="module-card">
      <div class="card-title-row"><div><div class="eyebrow">30 hari terakhir</div><h3>Momentum EXP</h3></div></div>
      <div class="sparkline">${sparkBars(g.id)}</div>
    </section>

    <section class="module-card">
      <div class="card-title-row"><div><div class="eyebrow">Kontributor</div><h3>Task terkait</h3></div></div>
      <div class="task-list">${contributing.map(t => taskListItem(t)).join('') || emptyState('Belum ada task untuk Gain ini.')}</div>
    </section>`;
}

function sparkBars(gainId) {
  let seed = gainId.split('').reduce((a, c) => a + c.charCodeAt(0), 0);
  const rand = () => { seed = (seed * 9301 + 49297) % 233280; return seed / 233280; };
  return Array.from({ length: 14 }, () => `<span style="height:${18 + rand() * 82}%"></span>`).join('');
}

/* ============================================================
   View: Bestiary
   ============================================================ */

function viewBestiary() {
  return `
    <div class="view-header"><div><div class="eyebrow">Riwayat kemenangan</div><h1>Bestiary</h1></div></div>
    <div class="bestiary-grid">
      ${TIERS.map(tier => {
        const entry = state.bestiary[tier.num];
        const locked = entry.defeated === 0;
        return `
          <div class="beast-card ${locked ? 'locked' : ''}">
            <div class="beast-avatar shape-${tier.shape} ${locked ? '' : 'dormant'}" style="--monster-scale:0.62;--monster-tilt:0deg;--horn-left-rotation:-14deg;--horn-right-rotation:14deg;--eye-size:18px;--eye-offset:28px;--snout-width:34px;--snout-height:14px;">
              <span class="horn left"></span><span class="horn right"></span>
              <span class="eye left"></span><span class="eye right"></span>
              <span class="snout"></span>
            </div>
            ${locked ? `<span class="beast-lock">${icon('lock', 14)}</span>` : ''}
            <strong>${tier.name}</strong>
            <small>Tier ${tier.num}</small>
            <div class="beast-stats">
              <span>${locked ? 'Belum ditemui' : `Dikalahkan ${entry.defeated}×`}</span>
              ${entry.best ? `<span>Rekor ${entry.best}m</span>` : ''}
            </div>
          </div>`;
      }).join('')}
    </div>`;
}

/* ============================================================
   View: Stats
   ============================================================ */

function viewStats() {
  const s = state.stats;
  return `
    <div class="view-header"><div><div class="eyebrow">Insight</div><h1>Statistik</h1></div></div>

    <section class="stat-tile">
      <div class="stat-tile-icon">${icon('timer', 20)}</div>
      <div>
        <strong>${s.avgOverrun}×</strong>
        <span>rata-rata lebih lama dari estimasi, dari ${s.sampleSize} task</span>
      </div>
    </section>

    <section class="module-card">
      <div class="card-title-row"><div><div class="eyebrow">Per tier</div><h3>Tingkat penyelesaian</h3></div></div>
      <div class="bar-list">
        ${TIERS.map((tier, i) => `
          <div class="bar-list-row">
            <span>T${tier.num}</span>
            <div class="bar-track"><span style="width:${s.completionByTier[i]}%"></span></div>
            <strong>${s.completionByTier[i]}%</strong>
          </div>`).join('')}
      </div>
    </section>

    <section class="module-card">
      <div class="card-title-row">
        <div><div class="eyebrow">Konsistensi</div><h3>Streak</h3></div>
        <span class="live-pill">${icon('flame', 12)} ${s.currentStreak} hari</span>
      </div>
      <div class="calendar-grid">${s.calendar.map(d => `<span class="${d ? 'on' : ''}"></span>`).join('')}</div>
      <p class="field-hint">Rekor terpanjang: ${s.longestStreak} hari beruntun.</p>
    </section>

    <section class="module-card">
      <div class="card-title-row"><div><div class="eyebrow">Keseimbangan</div><h3>Level per Gain</h3></div></div>
      <div class="bar-list">
        ${GAINS.map(g => {
          const level = levelProgress(state.gains[g.id].totalExp).level;
          const pct = clamp((level / 15) * 100, 4, 100);
          return `
          <div class="bar-list-row">
            <span style="color:${g.color}">${g.label}</span>
            <div class="bar-track"><span style="width:${pct}%;background:${g.color}"></span></div>
            <strong>Lv ${level}</strong>
          </div>`;
        }).join('')}
      </div>
    </section>`;
}

/* ============================================================
   View: Settings
   ============================================================ */

function viewSettings() {
  const s = state.settings;
  return `
    <div class="view-header">
      <button class="icon-btn" type="button" data-action="go" data-route="home" aria-label="Kembali">${icon('back')}</button>
      <div><div class="eyebrow">Kendali penuh</div><h1>Setelan</h1></div>
    </div>

    <section class="module-card">
      <div class="eyebrow">Notifikasi</div>
      <div class="settings-row">
        <span>Jam tenang</span>
        <div class="time-pair"><input type="time" value="${s.quietStart}" data-action="set-setting" data-key="quietStart" /><span>–</span><input type="time" value="${s.quietEnd}" data-action="set-setting" data-key="quietEnd" /></div>
      </div>
      <div class="settings-row">
        <span>Jatah notifikasi / hari</span>
        <strong id="notif-budget-value">${s.notifBudget}</strong>
      </div>
      <input type="range" min="1" max="6" value="${s.notifBudget}" style="--_pct:${((s.notifBudget - 1) / 5) * 100}%" data-action="set-notif-budget" />
    </section>

    <section class="module-card">
      <div class="eyebrow">Progres</div>
      <div class="settings-row">
        <span>EXP menyusut saat Liar</span>
        <button class="switch ${s.expDecay ? 'on' : ''}" type="button" data-action="toggle-decay" role="switch" aria-checked="${s.expDecay}"><span></span></button>
      </div>
    </section>

    <section class="module-card">
      <div class="eyebrow">Tampilan</div>
      <div class="settings-row">
        <span>Bahasa</span>
        <div class="segmented compact">
          <button class="seg ${s.language === 'id' ? 'active' : ''}" type="button" data-action="set-lang" data-lang="id">ID</button>
          <button class="seg ${s.language === 'en' ? 'active' : ''}" type="button" data-action="set-lang" data-lang="en">EN</button>
        </div>
      </div>
    </section>

    <section class="module-card">
      <div class="eyebrow">Akun</div>
      <button class="filter-btn full" type="button" data-action="export-data">${icon('download', 14)} Ekspor data</button>
      <button class="filter-btn full danger" type="button" data-action="delete-account">${icon('trash', 14)} Hapus akun</button>
      <button class="filter-btn full" type="button" data-action="logout">Keluar</button>
    </section>`;
}

/* ============================================================
   Router
   ============================================================ */

const NAV_MAP = { home: 'home', tasks: 'tasks', create: 'create', task: 'tasks', bestiary: 'bestiary', gain: 'home', stats: 'stats', settings: null, login: null };

function parseHash() {
  const raw = location.hash.replace(/^#\/?/, '');
  const [name, param] = raw.split('/');
  return { name: name || 'home', param };
}

function render() {
  if (timerHandle) { clearInterval(timerHandle); timerHandle = null; }

  let { name, param } = parseHash();
  if (!state.ui.authenticated) name = 'login';

  let html;
  switch (name) {
    case 'login': html = viewLogin(); break;
    case 'home': html = viewHome(); break;
    case 'tasks': html = viewTasks(); break;
    case 'create': html = viewCreate(); break;
    case 'task': html = viewTaskDetail(param); break;
    case 'gain': html = viewGainDetail(param); break;
    case 'bestiary': html = viewBestiary(); break;
    case 'stats': html = viewStats(); break;
    case 'settings': html = viewSettings(); break;
    default: html = viewHome();
  }

  viewRoot.innerHTML = html;
  viewRoot.scrollTop = 0;
  viewRoot.classList.remove('fade-in');
  void viewRoot.offsetWidth;
  viewRoot.classList.add('fade-in');

  document.body.classList.toggle('is-login', name === 'login');
  bottomNav.hidden = name === 'login';
  bottomNav.querySelectorAll('.nav-btn').forEach(btn => {
    btn.classList.toggle('active', NAV_MAP[name] === btn.dataset.nav);
  });

  if (name === 'task' && state.ui.timer && state.ui.timer.taskId === param) {
    const label = viewRoot.querySelector('.timer-row span');
    timerHandle = setInterval(() => {
      if (label) label.textContent = formatElapsed(state.ui.timer.startedAt);
    }, 1000);
  }
}

function go(route) {
  location.hash = `#/${route}`;
}

window.addEventListener('hashchange', render);

/* ============================================================
   Actions (event delegation)
   ============================================================ */

document.addEventListener('click', event => {
  const navBtn = event.target.closest('.nav-btn');
  if (navBtn) { go(navBtn.dataset.nav); return; }

  const el = event.target.closest('[data-action]');
  if (!el) return;
  const action = el.dataset.action;
  const f = state.ui.createForm;

  switch (action) {
    case 'login':
      state.ui.authenticated = true;
      toast('Masuk sebagai rezafahrul30@gmail.com');
      go('home');
      break;
    case 'go':
      go(el.dataset.route);
      break;
    case 'open-task':
      go(`task/${el.dataset.id}`);
      break;
    case 'filter-tasks':
      state.ui.taskFilter = el.dataset.filter;
      render();
      break;
    case 'set-type':
      f.type = el.dataset.type;
      render();
      break;
    case 'pick-primary-gain':
      f.primaryGain = el.dataset.gain;
      if (f.secondaryGain === f.primaryGain) f.secondaryGain = '';
      if (f.tertiaryGain === f.primaryGain) f.tertiaryGain = '';
      render();
      break;
    case 'toggle-split':
      f.split = !f.split;
      render();
      break;
    case 'toggle-subtask': {
      const task = taskById(el.dataset.id);
      task.subtasks[Number(el.dataset.index)].done = el.checked;
      render();
      break;
    }
    case 'start-timer':
      state.ui.timer = { taskId: el.dataset.id, startedAt: Date.now() };
      render();
      break;
    case 'stop-timer':
      state.ui.timer = null;
      toast('Sesi fokus disimpan.');
      render();
      break;
    case 'complete-task':
      completeTask(el.dataset.id);
      break;
    case 'cancel-task':
      cancelTask(el.dataset.id);
      break;
    case 'export-data':
      exportData();
      break;
    case 'delete-account':
      if (confirm('Hapus akun dan semua data secara permanen?')) {
        toast('Permintaan hapus akun tercatat (demo).');
      }
      break;
    case 'logout':
      state.ui.authenticated = false;
      go('login');
      break;
    case 'toggle-decay':
      state.settings.expDecay = !state.settings.expDecay;
      render();
      break;
    case 'set-lang':
      state.settings.language = el.dataset.lang;
      render();
      break;
    default:
      break;
  }
});

document.addEventListener('submit', event => {
  const el = event.target.closest('[data-action]');
  if (!el) return;
  event.preventDefault();
  const action = el.dataset.action;

  if (action === 'login-form') {
    state.ui.authenticated = true;
    toast('Masuk sebagai rezafahrul30@gmail.com');
    go('home');
  }

  if (action === 'submit-create') {
    const data = new FormData(el);
    const f = state.ui.createForm;
    const title = (data.get('title') || '').toString().trim();
    if (!title) return;

    let due = null;
    if (f.type !== 'lentur') {
      const dateStr = data.get('dueDate');
      const timeStr = data.get('dueTime') || '23:59';
      if (dateStr) due = new Date(`${dateStr}T${timeStr}`);
    }

    const task = {
      id: `t${nextId++}`,
      title,
      notes: (data.get('notes') || '').toString().trim(),
      difficulty: f.difficulty,
      impact: f.impact,
      due,
      type: f.type,
      estMinutes: Number(data.get('estMinutes')) || 30,
      allocations: currentAllocations(),
      subtasks: [],
      status: 'active',
      createdAt: new Date()
    };

    state.tasks.unshift(task);
    state.ui.createForm = { primaryGain: 'raga', secondaryGain: '', secondaryPct: 0, tertiaryGain: '', tertiaryPct: 0, split: false, difficulty: 50, impact: 50, type: 'bertenggat' };
    toast('Task baru dibuat.');
    go(`task/${task.id}`);
  }

  if (action === 'add-subtask') {
    const data = new FormData(el);
    const title = (data.get('subtaskTitle') || '').toString().trim();
    if (!title) return;
    taskById(el.dataset.id).subtasks.push({ title, done: false });
    render();
  }
});

document.addEventListener('input', event => {
  const el = event.target;
  const f = state.ui.createForm;

  if (el.id === 'difficulty' && el.closest('.create-form')) {
    f.difficulty = Number(el.value);
    updateCreatePreview();
    return;
  }
  if (el.id === 'impact' && el.closest('.create-form')) {
    f.impact = Number(el.value);
    updateCreatePreview();
    return;
  }

  const action = el.dataset.action;
  if (!action) return;

  switch (action) {
    case 'set-secondary-gain': f.secondaryGain = el.value; render(); break;
    case 'set-tertiary-gain': f.tertiaryGain = el.value; render(); break;
    case 'set-secondary-pct': {
      f.secondaryPct = Number(el.value);
      el.style.setProperty('--_pct', `${(f.secondaryPct / 40) * 100}%`);
      el.nextElementSibling.textContent = f.secondaryGain ? `${f.secondaryPct}%` : '—';
      break;
    }
    case 'set-tertiary-pct': {
      f.tertiaryPct = Number(el.value);
      el.style.setProperty('--_pct', `${(f.tertiaryPct / 30) * 100}%`);
      el.nextElementSibling.textContent = f.tertiaryGain ? `${f.tertiaryPct}%` : '—';
      break;
    }
    case 'set-notif-budget': {
      state.settings.notifBudget = Number(el.value);
      el.style.setProperty('--_pct', `${((state.settings.notifBudget - 1) / 5) * 100}%`);
      document.getElementById('notif-budget-value').textContent = state.settings.notifBudget;
      break;
    }
    case 'set-setting': state.settings[el.dataset.key] = el.value; break;
    default: break;
  }
});

function updateCreatePreview() {
  const f = state.ui.createForm;
  const score = clamp(f.difficulty + f.impact, 0, 200);
  const tier = tierFromScore(score);
  const baseExp = Math.round(baseExperience(f.difficulty, f.impact));

  document.getElementById('difficulty-value').textContent = f.difficulty;
  document.getElementById('impact-value').textContent = f.impact;

  document.getElementById('difficulty').style.setProperty('--_pct', `${f.difficulty}%`);
  document.getElementById('impact').style.setProperty('--_pct', `${f.impact}%`);

  const previewCard = document.querySelector('.preview-card');
  previewCard.querySelector('h2').textContent = tier.name;
  previewCard.querySelector('.tier-badge').textContent = `Tier ${tier.num} · ${tier.name}`;
  previewCard.querySelector('.hero-body').outerHTML = `<div class="hero-body">${monsterFragment({ difficulty: f.difficulty, impact: f.impact, due: null }, { size: 118 })}
    <div class="mini-stats"><div><span>Score</span><strong>${score}</strong></div><div><span>EXP dasar</span><strong>${baseExp}</strong></div></div></div>`;
}

/* ============================================================
   Task lifecycle
   ============================================================ */

function completeTask(id) {
  const task = taskById(id);
  if (!task) return;
  const total = Math.round(baseExperience(task.difficulty, task.impact) * timeMultiplier(task.due, NOW) * streakMultiplier(state.stats.currentStreak));
  const levels = gainLevelsMap();
  const perGainBase = distributeExperience(total, task.allocations);
  let awarded = 0;
  Object.entries(perGainBase).forEach(([gainId, amount]) => {
    const mult = balanceMultiplier(gainId, levels);
    const finalAmount = Math.round(amount * mult);
    state.gains[gainId].totalExp += finalAmount;
    awarded += finalAmount;
  });

  const tier = tierFromScore(task.difficulty + task.impact);
  const beast = state.bestiary[tier.num];
  beast.defeated += 1;

  task.status = 'done';
  state.ui.timer = null;
  toast(`Monster dikalahkan! +${awarded} EXP`);
  go('tasks');
}

function cancelTask(id) {
  const task = taskById(id);
  if (!task) return;
  task.status = 'cancelled';
  state.ui.timer = null;
  toast('Task dibatalkan, monster dilepaskan.');
  go('tasks');
}

function exportData() {
  const payload = { gains: state.gains, tasks: state.tasks, bestiary: state.bestiary, settings: state.settings };
  const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'impact-task-export.json';
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
  toast('Data diekspor.');
}

/* ============================================================
   Init
   ============================================================ */

render();
