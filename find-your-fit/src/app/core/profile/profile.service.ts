import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '@env/environment';
import {
  ClientNameResponse,
  LookDetailResponse,
  LookSummary,
  LooksResponse,
} from '@shared/models/look-card.model';
import { SaveLookRequest } from '@shared/models/outfit.model';
import { Observable, map } from 'rxjs';

const ENDPOINTS = {
  FULL_NAME: '/api/user/full-name',
  MY_LOOKS: '/api/profile/looks',
  SAVED_LOOKS: '/api/profile/looks/saved',
  PUBLIC_LOOKS: '/api/public/looks',
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

  getLook(id: number): Observable<LookDetailResponse> {
    return this.fetchLookDetail(`${ENDPOINTS.MY_LOOKS}/${id}`);
  }

  getPublicLook(id: number): Observable<LookDetailResponse> {
    return this.fetchLookDetail(`${ENDPOINTS.PUBLIC_LOOKS}/${id}`);
  }

  publishLook(id: number): Observable<void> {
    return this.http.put<void>(`${environment.apiBaseUrl}${ENDPOINTS.MY_LOOKS}/${id}/publish`, {});
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

  private fetchLookDetail(path: string): Observable<LookDetailResponse> {
    return this.http
      .get<LookDetailResponse>(`${environment.apiBaseUrl}${path}`)
      .pipe(
        map((detail) => ({ ...detail, imageUrl: `${environment.apiBaseUrl}${detail.imageUrl}` })),
      );
  }
}
