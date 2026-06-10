import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '@env/environment';
import { ClientNameResponse, LookSummary, LooksResponse } from '@shared/models/look-card.model';
import { SaveLookRequest } from '@shared/models/outfit.model';
import { Observable, map } from 'rxjs';

const ENDPOINTS = {
  FULL_NAME: '/api/user/full-name',
  MY_LOOKS: '/api/profile/looks',
  SAVED_LOOKS: '/api/profile/looks/saved',
} as const;

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);

  getFullName(): Observable<string> {
    return this.http
      .get<ClientNameResponse>(`${environment.apiBaseUrl}${ENDPOINTS.FULL_NAME}`)
      .pipe(map((response) => response.fullName));
  }

  saveLook(request: SaveLookRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiBaseUrl}${ENDPOINTS.MY_LOOKS}`, request);
  }

  getMyLooks(): Observable<LookSummary[]> {
    return this.fetchLooks(ENDPOINTS.MY_LOOKS);
  }

  getSavedLooks(): Observable<LookSummary[]> {
    return this.fetchLooks(ENDPOINTS.SAVED_LOOKS);
  }

  private fetchLooks(endpoint: string): Observable<LookSummary[]> {
    return this.http.get<LooksResponse>(`${environment.apiBaseUrl}${endpoint}`).pipe(
      map((response) =>
        response.looks.map((look) => ({
          ...look,
          imageUrl: `${environment.apiBaseUrl}${look.imageUrl}`,
        })),
      ),
    );
  }
}
