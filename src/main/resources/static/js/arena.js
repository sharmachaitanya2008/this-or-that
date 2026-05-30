(function() {
    const token = localStorage.getItem('jwt_token');
    const username = localStorage.getItem('judge_username');

    if (!token) {
        window.location.href = '/';
        return;
    }

    const duelContainer = document.getElementById('duelContainer');
    const completionScreen = document.getElementById('completionScreen');
    const championCard = document.getElementById('championCard');
    const challengerCard = document.getElementById('challengerCard');

    const championImg = championCard.querySelector('.design-card-image');
    const challengerImg = challengerCard.querySelector('.design-card-image');
    const championTitle = document.getElementById('championTitle');
    const challengerTitle = document.getElementById('challengerTitle');
    const championDesc = document.getElementById('championDescription');
    const challengerDesc = document.getElementById('challengerDescription');
    const voteChampion = document.getElementById('voteChampion');
    const voteChallenger = document.getElementById('voteChallenger');

    const judgeName = document.getElementById('judgeName');
    const votesCast = document.getElementById('votesCast');
    const progressPercent = document.getElementById('progressPercent');

    let abortController = null;

    let state = {
        champion: null,
        challenger: null,
        voting: false,
        sessionId: null
    };

    judgeName.textContent = username || '—';

    function init() {
        restoreSession();
    }

    async function restoreSession() {
        try {
            const session = await API.getSession();
            state.sessionId = session.sessionId;
            updateStats(session.votesCast, session.progressPercentage, session.completed);

            if (session.completed) {
                showCompletion();
                return;
            }

            if (session.champion && session.challenger) {
                renderDuel(session.champion, session.challenger);
            } else {
                await loadInitialDuel();
            }
        } catch (err) {
            showNotification('Failed to restore session: ' + err.message, 'error');
            await loadInitialDuel();
        }
    }

    async function loadInitialDuel() {
        try {
            const duel = await API.getDuel();
            renderDuel(duel.champion, duel.challenger);
        } catch (err) {
            showNotification('Could not load duel: ' + err.message, 'error');
        }
    }

    function renderDuel(champion, challenger) {
        state.champion = champion;
        state.challenger = challenger;

        renderCard(championCard, champion, 'champion');
        renderCard(challengerCard, challenger, 'challenger');

        voteChampion.disabled = false;
        voteChallenger.disabled = false;

        championCard.classList.remove('loser');
        challengerCard.classList.remove('loser');
        championCard.classList.remove('winner');
        challengerCard.classList.remove('winner');
    }

    function renderCard(card, design, side) {
        const img = card.querySelector('.design-card-image');
        const title = card.querySelector('.design-card-title');
        const desc = card.querySelector('.design-card-description');

        img.classList.add('loading');
        img.classList.remove('loaded');
        img.alt = '';

        if (design.imageUrl) {
            img.src = design.imageUrl;
            img.onload = () => {
                img.classList.remove('loading');
                img.classList.add('loaded');
                img.alt = design.title;
            };
            img.onerror = () => {
                img.classList.remove('loading');
                img.classList.add('loaded');
                img.alt = design.title;
            };
        }

        title.textContent = design.title;
        desc.textContent = design.description;

        card.style.animation = 'none';
        card.offsetHeight;
        card.style.animation = 'slideUp 0.5s ease-out forwards';
    }

    async function handleVote(winnerId) {
        if (state.voting) return;
        state.voting = true;

        if (abortController) {
            abortController.abort();
        }
        abortController = new AbortController();

        const loserId = winnerId === state.champion.id
            ? state.challenger.id
            : state.champion.id;

        const winnerCard = winnerId === state.champion.id ? championCard : challengerCard;
        const loserCard = winnerId === state.champion.id ? challengerCard : championCard;

        voteChampion.disabled = true;
        voteChallenger.disabled = true;

        loserCard.classList.add('loser');
        winnerCard.classList.add('winner');

        createSparkles(winnerCard);

        try {
            const result = await API.submitVote(winnerId, loserId);

            updateStats(result.votesCast, result.progressPercentage, result.completed);

            if (result.completed) {
                setTimeout(() => showCompletion(), 600);
                return;
            }

            const challenger = result.challenger;

            const loserSide = loserCard === challengerCard ? 'challenger' : 'champion';
            const winnerDesign = winnerId === state.champion.id ? state.champion : state.challenger;

            setTimeout(() => {
                loserCard.classList.remove('loser', 'winner');
                if (loserSide === 'challenger') {
                    renderCard(loserCard, challenger, 'challenger');
                    state.challenger = challenger;
                    state.champion = winnerDesign;
                } else {
                    renderCard(loserCard, challenger, 'champion');
                    state.champion = challenger;
                    state.challenger = winnerDesign;
                }
                winnerCard.classList.remove('winner');
                voteChampion.disabled = false;
                voteChallenger.disabled = false;
                state.voting = false;
            }, 600);
        } catch (err) {
            if (err.name === 'AbortError') return;
            showNotification('Vote failed: ' + err.message + '. Try again.', 'error');
            winnerCard.classList.remove('winner');
            loserCard.classList.remove('loser');
            voteChampion.disabled = false;
            voteChallenger.disabled = false;
            state.voting = false;
        }
    }

    function createSparkles(card) {
        const container = card.querySelector('.sparkle-container');
        if (!container) return;

        for (let i = 0; i < 12; i++) {
            const sparkle = document.createElement('div');
            sparkle.className = 'sparkle';
            sparkle.style.left = Math.random() * 100 + '%';
            sparkle.style.top = Math.random() * 100 + '%';
            sparkle.style.animationDelay = Math.random() * 0.5 + 's';
            sparkle.style.width = (Math.random() * 4 + 2) + 'px';
            sparkle.style.height = sparkle.style.width;
            container.appendChild(sparkle);

            setTimeout(() => sparkle.remove(), 1500);
        }
    }

    function updateStats(votes, progress, completed) {
        votesCast.textContent = votes;
        progressPercent.textContent = Math.round(progress) + '%';
    }

    function showCompletion() {
        duelContainer.style.display = 'none';
        completionScreen.classList.add('active');
    }

    const lightbox = document.getElementById('lightbox');
    const lightboxImg = lightbox.querySelector('.lightbox-image');
    const lightboxClose = lightbox.querySelector('.lightbox-close');

    function openLightbox(src, alt) {
        lightboxImg.src = src;
        lightboxImg.alt = alt || '';
        lightbox.classList.add('active');
        lightbox.setAttribute('aria-hidden', 'false');
    }

    function closeLightbox() {
        lightbox.classList.remove('active');
        lightbox.setAttribute('aria-hidden', 'true');
    }

    document.querySelectorAll('.design-card-image-wrapper').forEach(wrapper => {
        wrapper.addEventListener('click', function(e) {
            const img = this.querySelector('.design-card-image');
            if (img && img.src) openLightbox(img.src, img.alt);
        });
    });

    lightboxClose.addEventListener('click', closeLightbox);
    lightbox.addEventListener('click', closeLightbox);
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && lightbox.classList.contains('active')) closeLightbox();
    });

    voteChampion.addEventListener('click', () => handleVote(state.champion.id));
    voteChallenger.addEventListener('click', () => handleVote(state.challenger.id));

    document.addEventListener('keydown', (e) => {
        if (e.key === '1' || e.key === 'ArrowLeft') {
            voteChampion.click();
        } else if (e.key === '2' || e.key === 'ArrowRight') {
            voteChallenger.click();
        }
    });

    init();
})();
