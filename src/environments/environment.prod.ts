export const environment = {
  production: true,
  apiUrl: (globalThis as any).__BOMEFIT_API_URL__ ?? 'http://localhost:8080/api'
};
