import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getAccessToken();

  const isAuthUrl = req.url.includes('/auth/');
  const authReq =
    token && !isAuthUrl
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(authReq).pipe(
    catchError((err: unknown) => {
      const status = (err as { status?: number })?.status;
      // 401 = sesión inválida → cerrar y volver al login.
      // 403 = autenticado pero sin permiso → NO desloguea, solo propaga.
      if (status === 401 && !isAuthUrl) {
        auth.logout();
        router.navigate(['/auth/login']);
      }
      return throwError(() => err);
    }),
  );
};