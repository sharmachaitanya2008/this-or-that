function showNotification(message, type) {
    var el = document.getElementById('notification');
    if (!el) return;
    el.textContent = message;
    el.className = 'notification' + (type ? ' ' + type : '') + ' visible';
    clearTimeout(el._timeout);
    el._timeout = setTimeout(function () {
        el.classList.remove('visible');
    }, 4000);
}

const API = {
    BASE: '',

    _headers() {
        const headers = { 'Content-Type': 'application/json' };
        const token = localStorage.getItem('jwt_token');
        if (token) headers['Authorization'] = `Bearer ${token}`;
        return headers;
    },

    async _fetch(url, options = {}) {
        const res = await fetch(url, {
            ...options,
            headers: { ...this._headers(), ...options.headers }
        });

        if (res.status === 401) {
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('judge_username');
            window.location.href = '/';
            throw new Error('Session expired');
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({ message: 'Request failed' }));
            throw new Error(err.message || `HTTP ${res.status}`);
        }

        if (res.status === 204) return null;

        return res.json();
    },

    login(username) {
        return this._fetch('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username })
        });
    },

    getDuel() {
        return this._fetch('/api/designs/duel');
    },

    getChallenger(excludeId) {
        const params = excludeId ? `?excludeId=${excludeId}` : '';
        return this._fetch(`/api/designs/challenger${params}`);
    },

    submitVote(winnerId, loserId) {
        return this._fetch('/api/votes', {
            method: 'POST',
            body: JSON.stringify({ winnerId, loserId })
        });
    },

    getSession() {
        return this._fetch('/api/session/current');
    },

    getProgress() {
        return this._fetch('/api/judges/progress');
    },

    getResults() {
        return this._fetch('/api/results');
    }
};
