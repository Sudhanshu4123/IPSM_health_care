document.addEventListener('DOMContentLoaded', () => {
    const timeDisplay = document.getElementById('live-time');
    const loginDisplay = document.getElementById('last-login');
    const centreSelect = document.getElementById('centre-select');
    const welcomeHeading = document.getElementById('welcome-heading');

    // Update Live Clock
    function updateClock() {
        const now = new Date();
        timeDisplay.textContent = now.toLocaleString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            day: 'numeric',
            month: 'short'
        });
    }

    // Set Initial Static Data
    const now = new Date();
    loginDisplay.textContent = now.getHours() + ":" + now.getMinutes().toString().padStart(2, '0');

    setInterval(updateClock, 1000);
    updateClock();

    // Centre Switch Logic
    centreSelect.addEventListener('change', (e) => {
        const selectedText = e.target.options[e.target.selectedIndex].text;
        welcomeHeading.textContent = `Centre: ${selectedText}`;

        // Simple visual feedback
        welcomeHeading.style.color = 'var(--primary-color)';
        setTimeout(() => {
            welcomeHeading.style.color = '#1e293b';
        }, 500);
    });

    // Navigation Active State
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            if (link.getAttribute('href') === '#') e.preventDefault();
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
        });
    });
});
