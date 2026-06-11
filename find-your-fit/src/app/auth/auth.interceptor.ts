import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { environmentDev } from '@env/environment.dev';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { TokenStorageService } from './token-storage.service';

const PUBLIC_PATH_PREFIX = '/api/public/';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokens = inject(TokenStorageService);
  const token = tokens.getToken();

  const targetsBackend = req.url.startsWith(environmentDev.apiBaseUrl);
  const isPublicEndpoint = req.url.includes(PUBLIC_PATH_PREFIX);

  if (!token || !targetsBackend || isPublicEndpoint) {
    return next(req);
  }

  const auth = inject(AuthService);
  const router = inject(Router);

  const authorized = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });

  return next(authorized).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        auth.logout();
        router.navigateByUrl('/login');
      }
      return throwError(() => error);
    }),
  );
};
