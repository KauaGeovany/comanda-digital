import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService, CartService } from './core';
import { FeedbackService } from './feedback';

@Component({ selector:'app-root', standalone:true, imports:[RouterOutlet,RouterLink,RouterLinkActive], templateUrl:'./app.html', styleUrl:'./app.css' })
export class App {
  auth = inject(AuthService); cart = inject(CartService); router = inject(Router); feedback = inject(FeedbackService);
  sair(): void { this.auth.signOut(); void this.router.navigate(['/']); }
}
