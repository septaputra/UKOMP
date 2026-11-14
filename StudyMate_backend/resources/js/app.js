import './bootstrap';

const THEME_KEY = 'studymate-theme';

function applyTheme(theme) {
	const nextTheme = theme === 'dark' ? 'dark' : 'light';
	const labels = document.body ? document.body.dataset : {};

	document.documentElement.setAttribute('data-theme', nextTheme);
	if (document.body) {
		document.body.setAttribute('data-theme', nextTheme);
	}

	document.querySelectorAll('[data-theme-toggle]').forEach((switchInput) => {
		if (switchInput instanceof HTMLInputElement) {
			switchInput.checked = nextTheme === 'dark';
		}
	});

	document.querySelectorAll('[data-switch-status]').forEach((stateLabel) => {
		stateLabel.textContent = nextTheme === 'dark'
			? (labels.switchStatusEnabled || 'Enabled')
			: (labels.switchStatusDisabled || 'Disabled');
	});

	document.querySelectorAll('[data-theme-status]').forEach((stateLabel) => {
		stateLabel.textContent = nextTheme === 'dark'
			? (labels.themeStatusDark || 'Dark Mode Enabled')
			: (labels.themeStatusLight || 'Light Mode Enabled');
	});

	document.dispatchEvent(new CustomEvent('studymate:theme-change', {
		detail: { theme: nextTheme },
	}));
}

function initThemeManager() {
	const savedTheme = localStorage.getItem(THEME_KEY) || 'light';
	applyTheme(savedTheme);

	document.addEventListener('change', (event) => {
		const target = event.target;

		if (!(target instanceof HTMLInputElement) || !target.matches('[data-theme-toggle]')) {
			return;
		}

		const theme = target.checked ? 'dark' : 'light';
		localStorage.setItem(THEME_KEY, theme);
		applyTheme(theme);
	});
}

window.StudyMateTheme = { applyTheme };

document.addEventListener('DOMContentLoaded', initThemeManager);
