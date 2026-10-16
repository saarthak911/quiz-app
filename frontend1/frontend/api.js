// Shared helpers used by every page. Plain JS, no build step, no framework.

const API_BASE = 'http://localhost:8081/api';

const Auth = {
  getToken() { return localStorage.getItem('quizapp_token'); },
  getUsername() { return localStorage.getItem('quizapp_username'); },
  getRole() { return localStorage.getItem('quizapp_role'); },
  isLoggedIn() { return !!this.getToken(); },
  isAdmin() { return this.getRole() === 'ADMIN'; },
  save(authResponse) {
    localStorage.setItem('quizapp_token', authResponse.token);
    localStorage.setItem('quizapp_username', authResponse.username);
    localStorage.setItem('quizapp_role', authResponse.role);
  },
  clear() {
    localStorage.removeItem('quizapp_token');
    localStorage.removeItem('quizapp_username');
    localStorage.removeItem('quizapp_role');
  },
  requireLogin() {
    if (!this.isLoggedIn()) {
      window.location.href = 'index.html';
    }
  },
  requireAdmin() {
    this.requireLogin();
    if (!this.isAdmin()) {
      window.location.href = 'dashboard.html';
    }
  }
};

// Thin wrapper around fetch() that attaches the JWT and throws a readable
// error on non-2xx responses so callers can just try/catch.
async function apiCall(path, { method = 'GET', body = null, auth = true } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (auth && Auth.getToken()) {
    headers['Authorization'] = `Bearer ${Auth.getToken()}`;
  }

  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null
  });

  // Sessions expire or tokens get invalidated -> send back to login.
  if (res.status === 401 && auth) {
    Auth.clear();
    window.location.href = 'index.html';
    return;
  }

  let data = null;
  const text = await res.text();
  if (text) {
    try { data = JSON.parse(text); } catch (e) { data = null; }
  }

  if (!res.ok) {
    const message = (data && data.error) ? data.error : `Request failed (${res.status})`;
    throw new Error(message);
  }

  return data;
}

function renderNav(activePage) {
  const nav = document.getElementById('appNav');
  if (!nav) return;

  if (!Auth.isLoggedIn()) {
    nav.innerHTML = '';
    return;
  }

  const links = [
    { href: 'dashboard.html', label: 'Quizzes' }
  ];
  if (Auth.isAdmin()) {
    links.push({ href: 'admin.html', label: 'Admin' });
  }

  nav.innerHTML =
    `<span>${Auth.getUsername()} &middot; ${Auth.getRole().toLowerCase()}</span>` +
    links.map(l => `<a href="${l.href}">${l.label}</a>`).join('') +
    `<button class="linklike" id="logoutBtn">Log out</button>`;

  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      Auth.clear();
      window.location.href = 'index.html';
    });
  }
}

function showAlert(containerId, message, type = 'error') {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = `<div class="alert ${type}">${escapeHtml(message)}</div>`;
}

function clearAlert(containerId) {
  const el = document.getElementById(containerId);
  if (el) el.innerHTML = '';
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}
