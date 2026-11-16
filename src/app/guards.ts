import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from './core';

export const authGuard: CanActivateFn = () => inject(AuthService).user ? true : inject(Router).createUrlTree(['/entrar']);

export const staffGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.user && auth.user.perfil !== 'CLIENTE' ? true : router.createUrlTree(['/entrar']);
};

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const section = route.paramMap.get('secao') ?? 'dashboard';
  const user = auth.user;
  if (!user) return router.createUrlTree(['/entrar']);
  if (user.perfil === 'COZINHEIRO' && section !== 'pedidos') {
    return router.createUrlTree(['/admin', 'pedidos']);
  }
  if (user.perfil === 'GERENTE' && section === 'usuarios') {
    return router.createUrlTree(['/admin']);
  }
  return true;
};
