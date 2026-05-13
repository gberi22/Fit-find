import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '@env/environment';
import { Observable, tap } from 'rxjs';
import { AuthRequest } from './dto/auth-request';
import { AuthResponse } from './dto/auth-response';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokens = inject(TokenStorageService);

  private readonly authenticated = signal<boolean>(this.tokens.hasToken());
  readonly isAuthenticated = this.authenticated.asReadonly();

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiBaseUrl}/api/public/auth`, request).pipe(
      tap((response) => {
        this.tokens.setToken(response.token);
        this.authenticated.set(true);
      }),
    );
  }

  logout(): void {
    this.tokens.clear();
    this.authenticated.set(false);
  }
}
