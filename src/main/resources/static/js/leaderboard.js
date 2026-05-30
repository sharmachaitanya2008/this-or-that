(function() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '/';
        return;
    }

    const container = document.getElementById('leaderboardContent');

    async function load() {
        try {
            const data = await API.getResults();
            if (!data.rankings || data.rankings.length === 0) {
                showEmpty();
                return;
            }
            renderLeaderboard(data);
        } catch (err) {
            container.innerHTML = `
                <div class="leaderboard-empty">
                    <span class="leaderboard-empty-icon" aria-hidden="true">🔮</span>
                    <p>The crystal ball is cloudy... ${err.message}</p>
                </div>`;
        }
    }

    function showEmpty() {
        container.innerHTML = `
            <div class="leaderboard-empty">
                <span class="leaderboard-empty-icon" aria-hidden="true">📜</span>
                <p>No votes have been cast yet. The scroll of champions awaits its first entries...</p>
            </div>`;
    }

    function renderLeaderboard(data) {
        const rankings = data.rankings;
        const winner = data.winner;
        const myPickId = data.myFinalPick ? data.myFinalPick.id : null;

        let html = '';

        if (rankings.length >= 3) {
            html += renderPodium(rankings, myPickId);
        }

        html += '<h2 class="leaderboard-section-title">Full Rankings</h2>';
        html += '<div class="rankings-table-wrapper">';
        html += '<table class="rankings-table" role="table" aria-label="Design rankings">';
        html += '<thead><tr>';
        html += '<th scope="col">Rank</th>';
        html += '<th scope="col">Design</th>';
        html += '<th scope="col">Final Picks</th>';
        html += '<th scope="col">Wins</th>';
        html += '<th scope="col">Losses</th>';
        html += '<th scope="col">Comparisons</th>';
        html += '</tr></thead><tbody>';

        rankings.forEach((ranking, i) => {
            const design = ranking.design;
            const rank = i + 1;
            const isMyPick = myPickId && design.id === myPickId;
            const rankClass = rank === 1 ? 'rank-1' : rank === 2 ? 'rank-2' : rank === 3 ? 'rank-3' : 'rank-default';
            const highlightClass = isMyPick ? 'rank-highlight' : '';
            const badge = rank === 1 ? '👑' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : rank;
            const pickLabel = isMyPick ? '<span class="rank-pick-label">Your Pick</span>' : '';

            html += `<tr class="${rankClass} ${highlightClass}">`;
            html += `<td><span class="rank-badge" aria-label="Rank ${rank}">${badge}</span></td>`;
            html += `<td class="rank-title">
                <img class="rank-thumbnail" src="${escapeHtml(design.imageUrl || '')}" alt="" loading="lazy">
                ${escapeHtml(design.title)} ${pickLabel}
            </td>`;
            html += `<td><strong>${ranking.finalPickCount}</strong></td>`;
            html += `<td>${design.wins}</td>`;
            html += `<td>${design.losses}</td>`;
            html += `<td>${design.comparisons}</td>`;
            html += '</tr>';
        });

        html += '</tbody></table></div>';
        container.innerHTML = html;
    }

    function renderPodium(rankings, myPickId) {
        const top3 = rankings.slice(0, 3);
        const labels = ['', '🥇 Champion', '🥈 2nd Place', '🥉 3rd Place'];

        const order = [1, 0, 2];

        let html = '<h2 class="leaderboard-section-title">Hall of Champions</h2>';
        html += '<div class="podium" role="list" aria-label="Top 3 designs">';

        order.forEach(i => {
            const ranking = top3[i];
            if (!ranking) return;
            const design = ranking.design;
            const place = i + 1;
            const isMyPick = myPickId && design.id === myPickId;

            html += `<div class="podium-place podium-${place}" role="listitem" aria-label="${place === 1 ? 'First' : place === 2 ? 'Second' : 'Third'} place">`;
            html += `<div class="podium-rank">${labels[place]}</div>`;
            html += `<div class="podium-card" style="${isMyPick ? 'border-color:var(--gold); box-shadow:0 0 20px var(--shadow-gold);' : ''}">`;
            html += `<img class="podium-card-image" src="${escapeHtml(design.imageUrl || '')}" alt="${escapeHtml(design.title)}" loading="lazy">`;
            html += `<div class="podium-card-body">`;
            html += `<div class="podium-card-title">${escapeHtml(design.title)} ${isMyPick ? '⭐' : ''}</div>`;
            html += `<div class="podium-card-stats">${ranking.finalPickCount} final pick${ranking.finalPickCount !== 1 ? 's' : ''}</div>`;
            html += `<div class="podium-card-stats" style="margin-top:4px;">${design.wins}W / ${design.losses}L / ${design.comparisons}C</div>`;
            html += `</div></div></div>`;
        });

        html += '</div>';
        return html;
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    load();
})();
