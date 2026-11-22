import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const isAuthRequest = /\/api\/auth\/(login|register)(?:$|\?)/.test(req.url);
  const token = auth.token;
  // Login and registration are public endpoints: they must not carry an old
  // token or trigger the expired-session redirect when credentials are rejected.
  const outgoing = token && !isAuthRequest
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(outgoing).pipe(catchError(error => {
    if (error.status === 401 && token && !isAuthRequest) {
      auth.signOut();
      void router.navigate(['/entrar']);
    }
    return throwError(() => error);
  }));
};
