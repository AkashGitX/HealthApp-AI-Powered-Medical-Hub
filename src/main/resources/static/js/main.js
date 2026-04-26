/* =============================================
   HealthApp — Main JavaScript
   JWT auth helpers + API fetch wrappers
   ============================================= */

// ── Token storage ─────────────────────────────
const Auth = {
  setToken:    (t)  => localStorage.setItem('jwt_token', t),
  getToken:    ()   => localStorage.getItem('jwt_token'),
  setRole:     (r)  => localStorage.setItem('user_role', r),
  getRole:     ()   => localStorage.getItem('user_role'),
  setName:     (n)  => localStorage.setItem('user_name', n),
  getName:     ()   => localStorage.getItem('user_name'),
  isLoggedIn:  ()   => !!localStorage.getItem('jwt_token'),
  logout: () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
    localStorage.removeItem('user_name');
    window.location.href = '/';
  }
};

// ── Base API fetch with JWT header ─────────────
// Returns raw Response so callers can check status / parse as needed.
async function apiFetch(url, options = {}) {
  const token = Auth.getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };
  const res = await fetch(url, { ...options, headers });
  if (res.status === 401) { Auth.logout(); return null; }
  return res;
}

// ── Convenience wrapper: fetch + auto-parse JSON ───────────────
// Returns the parsed JSON body (or null on error / 401).
async function apiFetchJson(url, options = {}) {
  try {
    const res = await apiFetch(url, options);
    if (!res) return null;
    return await res.json();
  } catch (err) {
    console.error('apiFetchJson error for', url, err);
    return null;
  }
}

// ── Multipart fetch (file upload) ──────────────
async function apiUpload(url, formData) {
  const token = Auth.getToken();
  const headers = token ? { 'Authorization': `Bearer ${token}` } : {};
  const res = await fetch(url, { method: 'POST', headers, body: formData });
  if (res.status === 401) { Auth.logout(); return null; }
  return res;
}

// ── Auth: login ────────────────────────────────
async function doLogin(email, password) {
  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  const data = await res.json();
  if (res.ok) {
    Auth.setToken(data.token);
    Auth.setRole(data.role);
    Auth.setName(data.fullName);
    window.location.href = data.redirectUrl;
  }
  return { ok: res.ok, data };
}

// ── Auth: register ─────────────────────────────
async function doRegister(payload) {
  const res = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  const data = await res.json();
  if (res.ok) {
    Auth.setToken(data.token);
    Auth.setRole(data.role);
    Auth.setName(data.fullName);
    window.location.href = data.redirectUrl;
  }
  return { ok: res.ok, data };
}

// ── Appointments ────────────────────────────────
async function bookAppointment(payload) {
  const res = await apiFetch('/api/appointments', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  return res ? res.json() : null;
}

async function getMyAppointments() {
  const res = await apiFetch('/api/appointments/my');
  return res ? res.json() : null;
}

async function updateAppointmentStatus(id, status) {
  const res = await apiFetch(`/api/appointments/${id}/status?status=${status}`, {
    method: 'PATCH'
  });
  return res ? res.json() : null;
}

// ── Doctor ─────────────────────────────────────
async function getAllDoctors() {
  const res = await fetch('/api/doctor/all');
  return res.json();
}

async function saveDoctorProfile(payload) {
  const res = await apiFetch('/api/doctor/profile', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  return res ? res.json() : null;
}

async function toggleAvailability() {
  const res = await apiFetch('/api/doctor/availability', { method: 'PATCH' });
  return res ? res.json() : null;
}

// ── Service Provider ───────────────────────────
async function saveProviderProfile(payload) {
  const res = await apiFetch('/api/provider/profile', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  return res ? res.json() : null;
}

// ── AI ─────────────────────────────────────────
async function analyzeReport(file) {
  const fd = new FormData();
  fd.append('file', file);
  const res = await apiUpload('/api/ai/analyze-report', fd);
  return res ? res.json() : null;
}

async function suggestMedicines(file) {
  const fd = new FormData();
  fd.append('file', file);
  const res = await apiUpload('/api/ai/suggest-medicines', fd);
  return res ? res.json() : null;
}

// ── Mental Health test submit ──────────────────
async function submitMentalHealthScore(score) {
  const res = await apiFetch(`/api/tests/mental-health?score=${score}`, {
    method: 'POST'
  });
  return res ? res.json() : null;
}

// ── Payment ────────────────────────────────────
async function createPaymentIntent(appointmentId, amount) {
  const res = await apiFetch('/api/payment/create-intent', {
    method: 'POST',
    body: JSON.stringify({ appointmentId, amount, currency: 'inr' })
  });
  return res ? res.json() : null;
}

// ── UI helpers ─────────────────────────────────
function showAlert(containerId, message, type = 'danger') {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
    ${message}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>`;
}

function setLoading(btnId, loading, text = 'Please wait...') {
  const btn = document.getElementById(btnId);
  if (!btn) return;
  if (loading) {
    btn.disabled = true;
    btn._originalText = btn.innerHTML;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>${text}`;
  } else {
    btn.disabled = false;
    btn.innerHTML = btn._originalText || text;
  }
}

// ── On page load: update navbar ────────────────
document.addEventListener('DOMContentLoaded', () => {
  const nameEl = document.getElementById('navUserName');
  if (nameEl && Auth.getName()) {
    nameEl.textContent = Auth.getName();
  }
});
