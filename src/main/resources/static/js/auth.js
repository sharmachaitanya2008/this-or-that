(function() {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const loginBtn = document.getElementById('loginBtn');
    const loginError = document.getElementById('loginError');

    if (!loginForm) return;

    const token = localStorage.getItem('jwt_token');
    if (token) {
        window.location.href = '/arena.html';
        return;
    }

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = usernameInput.value.trim();
        if (!username) {
            showError('Please enter your name, judge.');
            usernameInput.classList.add('error');
            return;
        }

        setLoading(true);
        hideError();

        try {
            const data = await API.login(username);
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('judge_username', data.username);
            localStorage.setItem('judge_id', data.judgeId);
            window.location.href = '/arena.html';
        } catch (err) {
            showError(err.message || 'Failed to enter the chamber.');
            usernameInput.classList.add('error');
        } finally {
            setLoading(false);
        }
    });

    usernameInput.addEventListener('input', () => {
        usernameInput.classList.remove('error');
        hideError();
    });

    function setLoading(loading) {
        loginBtn.disabled = loading;
        loginBtn.textContent = loading ? 'Entering...' : 'Begin Judging';
    }

    function showError(msg) {
        loginError.textContent = msg;
        loginError.classList.add('visible');
    }

    function hideError() {
        loginError.textContent = '';
        loginError.classList.remove('visible');
    }
})();
