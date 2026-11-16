export const environment = {
  production: true,
  apiUrl: (globalThis as any).__BOMEFIT_API_URL__ ?? 'https://comanda-digital-plko.onrender.com/api'
};
