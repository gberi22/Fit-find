import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import { environment } from '@env/environment';
import { Observable, tap } from 'rxjs';
import { AuthRequest } from './dto/auth-request';
import { AuthResponse } from './dto/auth-response';
import { RegisterRequest } from './dto/register-request';
import { TokenStorageService } from './token-storage.service';

const ENDPOINTS = {
  LOGIN: '/api/public/auth',
  REGISTER: '/api/public/register',
} as const;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokens = inject(TokenStorageService);
  private readonly outfitState = inject(OutfitStateService);

  private readonly authenticated = signal<boolean>(this.tokens.hasToken());
  readonly isAuthenticated = this.authenticated.asReadonly();

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiBaseUrl}${ENDPOINTS.LOGIN}`, request)
      .pipe(
        tap((response) => {
          this.outfitState.clear();
          this.tokens.setToken(response.token);
          this.authenticated.set(true);
        }),
      );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiBaseUrl}${ENDPOINTS.REGISTER}`, request)
      .pipe(
        tap((response) => {
          this.outfitState.clear();
          this.tokens.setToken(response.token);
          this.authenticated.set(true);
        }),
      );
  }

  logout(): void {
    this.outfitState.clear();
    this.tokens.clear();
    this.authenticated.set(false);
  }
}
