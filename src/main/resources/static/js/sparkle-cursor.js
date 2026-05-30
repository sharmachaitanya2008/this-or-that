(function() {
    var CLICKABLE = 'a, button, [role="button"], [role="link"], [onclick]';
    var throttle = 0;

    function randomBetween(a, b) {
        return a + Math.random() * (b - a);
    }

    function spawnSparkle(x, y) {
        var el = document.createElement('div');
        var size = randomBetween(3, 7);
        var angle = Math.random() * Math.PI * 2;
        var dist = randomBetween(12, 30);
        var tx = Math.cos(angle) * dist;
        var ty = Math.sin(angle) * dist;
        var duration = randomBetween(250, 500);

        el.style.cssText = [
            'position:fixed',
            'left:' + x + 'px',
            'top:' + y + 'px',
            'width:' + size + 'px',
            'height:' + size + 'px',
            'background:radial-gradient(circle,#ffd700,#d4af37)',
            'border-radius:50%',
            'pointer-events:none',
            'z-index:99999',
            'box-shadow:0 0 6px rgba(212,175,55,0.9),0 0 12px rgba(212,175,55,0.4)',
            'mix-blend-mode:screen'
        ].join(';');

        document.body.appendChild(el);

        var anim = el.animate([
            { transform: 'translate(0,0) scale(1)', opacity: 1 },
            { transform: 'translate(' + tx + 'px,' + ty + 'px) scale(0)', opacity: 0 }
        ], {
            duration: duration,
            easing: 'ease-out',
            fill: 'forwards'
        });

        anim.onfinish = function() { el.remove(); };
    }

    document.addEventListener('mousemove', function(e) {
        var now = Date.now();
        if (now - throttle < 60) return;
        var target = e.target;
        if (target.matches(CLICKABLE) || target.closest(CLICKABLE)) {
            throttle = now;
            spawnSparkle(e.clientX, e.clientY);
        }
    });
})();
