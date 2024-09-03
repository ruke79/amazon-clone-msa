/** @type {import('tailwindcss').Config} */
import daisyui from 'daisyui';

module.exports = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        amazon: {
          blue_light: '#232f3e',
          blue_dark: '#131921',
          orange: '#febd69'
        }
      }
    },
  },  
}

