/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'aegis-dark': '#0f172a',
        'aegis-panel': '#1e293b',
        'aegis-primary': '#3b82f6',
        'aegis-alert': '#ef4444',
        'aegis-warn': '#f59e0b',
        'aegis-success': '#10b981'
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
