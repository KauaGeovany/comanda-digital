import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export const API_URL = environment.apiUrl;
export interface Usuario { id: number; nome: string; email: string; perfil: 'CLIENTE'|'ADMIN'|'GERENTE'|'COZINHEIRO'; }
export interface AuthResponse { token: string; tipo: string; expiraEmSegundos: number; usuario: Usuario; }
export interface Prato { id: number; nome: string; descricao: string; fotoUrl?: string; precoVenda: number; tempoPreparoMin: number; categoria: string; status?: string; }
export interface Pagina<T> { content: T[]; totalElements: number; totalPages: number; number: number; }
export interface Pedido { id: number; status: string; valorTotal: number; criadoEm: string; clienteNome: string; itens: { prato: string; quantidade: number; subtotal: number }[]; timeline: { status: string; observacao: string; data: string }[]; }
export interface Dashboard {
    pedidosHoje: number; faturamentoHoje: number; ticketMedioHoje: number;
    inicio: string; fim: string; pedidosEmPreparo: number; foodCostMedio: number;
  vendasDiarias: { data: string; pedidos: number; faturamento: number }[];
  pratosMaisVendidos: { pratoId: number; nome: string; unidadesVendidas: number; faturamento: number }[];
  alertasEstoque: { id: number; nome: string; unidadePadrao: string; saldo: number; estoqueMinimo: number }[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  get token(): string | null { return localStorage.getItem('bomefit_token'); }
  get user(): Usuario | null { const raw = localStorage.getItem('bomefit_user'); if (!raw) return null; try { return JSON.parse(raw) as Usuario; } catch { this.signOut(); return null; } }
  signIn(email: string, senha: string): Observable<AuthResponse> { return this.http.post<AuthResponse>(`${API_URL}/auth/login`, {email, senha}); }
  register(data: {nome:string;email:string;senha:string;telefone?:string;endereco:string}): Observable<AuthResponse> { return this.http.post<AuthResponse>(`${API_URL}/auth/register`, data); }
  save(value: AuthResponse): void { localStorage.setItem('bomefit_token', value.token); localStorage.setItem('bomefit_user', JSON.stringify(value.usuario)); }
  signOut(): void { localStorage.removeItem('bomefit_token'); localStorage.removeItem('bomefit_user'); }
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private key = 'bomefit_cart';
  get items(): { prato: Prato; quantidade: number; observacoes: string }[] { try { return JSON.parse(localStorage.getItem(this.key) || '[]'); } catch { return []; } }
  get count(): number { return this.items.reduce((n, i) => n + i.quantidade, 0); }
  get total(): number { return this.items.reduce((n, i) => n + i.prato.precoVenda * i.quantidade, 0); }
  add(prato: Prato): void { const list = this.items; const found = list.find(i => i.prato.id === prato.id); if (found) found.quantidade++; else list.push({prato, quantidade:1, observacoes:''}); this.write(list); }
  change(id: number, delta: number): void { this.write(this.items.map(i => i.prato.id === id ? {...i, quantidade: i.quantidade + delta} : i).filter(i => i.quantidade > 0)); }
  setObservacoes(id:number, observacoes:string):void { this.write(this.items.map(i=>i.prato.id===id?{...i,observacoes}:i)); }
  clear(): void { localStorage.removeItem(this.key); }
  private write(items: {prato:Prato;quantidade:number;observacoes:string}[]): void { localStorage.setItem(this.key, JSON.stringify(items)); }
}
