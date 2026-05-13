import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '@env/environment';
import { TokenStorageService } from './token-storage.service';

const PUBLIC_PATH_PREFIX = '/api/public/';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokens = inject(TokenStorageService);
  const token = tokens.getToken();

  const targetsBackend = req.url.startsWith(environment.apiBaseUrl);
  const isPublicEndpoint = req.url.includes(PUBLIC_PATH_PREFIX);

  if (!token || !targetsBackend || isPublicEndpoint) {
    return next(req);
  }

  const authorized = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });

  return next(authorized);
};
