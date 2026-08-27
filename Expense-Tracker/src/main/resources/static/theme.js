/**
 * theme.js — Dark / Light mode toggle
 * Add <script src="theme.js"></script> in <head> of every page
 * Add <div id="themeToggleWrap"></div> wherever you want the button
 */

(function() {
    // ── 1. CSS variables for both themes
    const THEMES = {
        dark: {
            '--bg': '#0d1117',
            '--card': '#161b22',
            '--border': '#30363d',
            '--accent': '#00e5a0',
            '--accent-dim': 'rgba(0,229,160,0.12)',
            '--text': '#e6edf3',
            '--muted': '#8b949e',
            '--danger': '#f85149',
            '--warning': '#e3b341',
            '--info': '#388bfd',
        },
        light: {
            '--bg': '#f6f8fa',
            '--card': '#ffffff',
            '--border': '#d0d7de',
            '--accent': '#00a870',
            '--accent-dim': 'rgba(0,168,112,0.10)',
            '--text': '#1f2328',
            '--muted': '#656d76',
            '--danger': '#cf222e',
            '--warning': '#9a6700',
            '--info': '#0969da',
        }
    };

    // ── 2. Apply theme variables to :root
    function applyTheme(theme) {
        const root = document.documentElement;
        const vars = THEMES[theme];
        Object.entries(vars).forEach(([k, v]) => root.style.setProperty(k, v));
        root.setAttribute('data-theme', theme);
        localStorage.setItem('et_theme', theme);
    }

    // ── 3. Get saved or default theme
    function getSavedTheme() {
        return localStorage.getItem('et_theme') || 'dark';
    }

    // ── 4. Toggle
    function toggleTheme() {
        const current = document.documentElement.getAttribute('data-theme') || 'dark';
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next);
        updateToggleUI(next);
    }

    // ── 5. Update button icon + label
    function updateToggleUI(theme) {
        const btn = document.getElementById('themeToggleBtn');
        if (!btn) return;
        if (theme === 'dark') {
            btn.innerHTML = `<i class="bi bi-sun"></i> <span>Light Mode</span>`;
            btn.title = 'Switch to light mode';
        } else {
            btn.innerHTML = `<i class="bi bi-moon-stars"></i> <span>Dark Mode</span>`;
            btn.title = 'Switch to dark mode';
        }
    }

    // ── 6. Render toggle button HTML
    function renderThemeToggle() {
        const theme = getSavedTheme();
        const icon = theme === 'dark' ? 'bi-sun' : 'bi-moon-stars';
        const label = theme === 'dark' ? 'Light Mode' : 'Dark Mode';
        return `
      <button
        id="themeToggleBtn"
        onclick="window.__toggleTheme()"
        title="Toggle theme"
        style="
          display:flex;align-items:center;gap:6px;
          background:transparent;
          border:1px solid var(--border);
          color:var(--muted);
          border-radius:8px;
          padding:5px 12px;
          font-size:0.78rem;
          font-weight:600;
          font-family:'Nunito',sans-serif;
          cursor:pointer;
          transition:all 0.2s;
          width:100%;
          justify-content:center;
          margin-bottom:8px;
        "
        onmouseover="this.style.borderColor='var(--accent)';this.style.color='var(--accent)';"
        onmouseout="this.style.borderColor='var(--border)';this.style.color='var(--muted)';"
      >
        <i class="bi ${icon}"></i> <span>${label}</span>
      </button>
    `;
    }

    // ── 7. Expose globally
    window.__toggleTheme = toggleTheme;
    window.renderThemeToggle = renderThemeToggle;

    // ── 8. Apply immediately on script load (before DOM paint)
    applyTheme(getSavedTheme());

    // ── 9. After DOM ready, inject button if wrap exists
    document.addEventListener('DOMContentLoaded', function() {
        const wrap = document.getElementById('themeToggleWrap');
        if (wrap) {
            wrap.innerHTML = renderThemeToggle();
        }
        updateToggleUI(getSavedTheme());
    });

})();