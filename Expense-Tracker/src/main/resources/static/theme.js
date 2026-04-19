/**
 * theme.js — Dark / Light mode toggle
 * Shared across all pages. Include with <script src="theme.js"></script>
 *
 * Dark mode  = default (CSS :root variables)
 * Light mode = [data-theme="light"] overrides on <html>
 *
 * Preference is saved to localStorage so it persists across pages.
 */

(function() {
    // ─── Light mode CSS variable overrides ───────────────────────────────────────
    const LIGHT_STYLES = `
    [data-theme="light"] {
      --bg:        #f0f2f5;
      --card:      #ffffff;
      --border:    #d0d7de;
      --accent:    #00a878;
      --accent-dim: rgba(0, 168, 120, 0.10);
      --text:      #1f2328;
      --muted:     #57606a;
      --danger:    #cf222e;
      --warning:   #9a6700;
      --info:      #0550ae;
      --success:   #1a7f37;
    }
    [data-theme="light"] body {
      background: var(--bg);
      color: var(--text);
    }
    [data-theme="light"] .expenses-table tr:hover td {
      background: rgba(0,0,0,0.02);
    }
    [data-theme="light"] input,
    [data-theme="light"] select,
    [data-theme="light"] textarea {
      background: #ffffff !important;
      color: var(--text) !important;
      border-color: var(--border) !important;
    }
    [data-theme="light"] input::placeholder,
    [data-theme="light"] select::placeholder,
    [data-theme="light"] textarea::placeholder {
      color: var(--muted) !important;
    }
  `;

    // ─── Inject light-mode stylesheet once ───────────────────────────────────────
    function injectStyles() {
        if (document.getElementById('theme-overrides')) return;
        const style = document.createElement('style');
        style.id = 'theme-overrides';
        style.textContent = LIGHT_STYLES;
        document.head.appendChild(style);
    }

    // ─── Apply saved theme immediately (before paint — avoids flash) ─────────────
    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
    }

    const saved = localStorage.getItem('et-theme') || 'dark';
    applyTheme(saved);

    // ─── Toggle function called by the button ─────────────────────────────────────
    window.toggleTheme = function() {
        const current = document.documentElement.getAttribute('data-theme') || 'dark';
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next);
        localStorage.setItem('et-theme', next);
        updateToggleBtn();
    };

    // ─── Update button icon + label ───────────────────────────────────────────────
    window.updateToggleBtn = function() {
        const btn = document.getElementById('themeToggleBtn');
        if (!btn) return;
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        btn.innerHTML = isDark
            ? '<i class="bi bi-sun-fill"></i>'
            : '<i class="bi bi-moon-fill"></i>';
        btn.title = isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode';
    };

    // ─── Render the toggle button HTML ────────────────────────────────────────────
    window.renderThemeToggle = function() {
        injectStyles();
        const isDark = (localStorage.getItem('et-theme') || 'dark') === 'dark';
        return `
      <button
        id="themeToggleBtn"
        onclick="toggleTheme()"
        title="${isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}"
        style="
          background: var(--accent-dim);
          border: 1px solid var(--border);
          color: var(--text);
          border-radius: 8px;
          width: 36px;
          height: 36px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          font-size: 1rem;
          transition: all 0.2s;
          flex-shrink: 0;
        "
        onmouseover="this.style.borderColor='var(--accent)'"
        onmouseout="this.style.borderColor='var(--border)'"
      >
        ${isDark ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-fill"></i>'}
      </button>
    `;
    };

    // Run injectStyles on DOMContentLoaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', injectStyles);
    } else {
        injectStyles();
    }
})();