export const environment = {
  production: false,
  apiUrl: (globalThis as any).__BOMEFIT_API_URL__ ?? 'http://localhost:8080/api'
};
