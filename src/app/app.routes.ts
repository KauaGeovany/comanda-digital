import { Routes } from '@angular/router';
import { HomePage, LoginPage, CartPage, OrdersPage, AdminPage } from './pages';
import { authGuard, staffGuard, roleGuard } from './guards';
export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'entrar', component: LoginPage },
  { path: 'carrinho', component: CartPage },
  { path: 'pedidos', component: OrdersPage, canActivate: [authGuard] },
  { path: 'admin', component: AdminPage, canActivate: [staffGuard, roleGuard] },
  { path: 'admin/:secao', component: AdminPage, canActivate: [staffGuard, roleGuard] },
  { path: '**', redirectTo: '' }
];
