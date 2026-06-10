import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/webjars': 'http://localhost:8080',
      '/resources': 'http://localhost:8080',
      '/owners': 'http://localhost:8080',
      '/vets.html': 'http://localhost:8080',
      '/feedback': 'http://localhost:8080',
      '/oups': 'http://localhost:8080',
    },
  },
})