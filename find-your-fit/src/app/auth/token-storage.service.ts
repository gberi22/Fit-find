import { Injectable } from '@angular/core';

const TOKEN_KEY = 'fitfind.token';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly storage: Storage | null =
    typeof window === 'undefined' ? null : window.sessionStorage;

  getToken(): string | null {
    return this.storage?.getItem(TOKEN_KEY) ?? null;
  }
  setToken(token: string): void {
    this.storage?.setItem(TOKEN_KEY, token);
  }

  clear(): void {
    this.storage?.removeItem(TOKEN_KEY);
  }

  hasToken(): boolean {
    const token = this.getToken();
    return token !== null && token.length > 0;
  }
}
