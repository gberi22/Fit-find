import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environmentDev } from '@env/environment.dev';
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
} as const;

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);

  getFullName(): Observable<string> {
    return this.http
      .get<ClientNameResponse>(`${environmentDev.apiBaseUrl}${ENDPOINTS.FULL_NAME}`)
      .pipe(map((response) => response.fullName));
  }

  saveLook(request: SaveLookRequest): Observable<void> {
    return this.http.post<void>(`${environmentDev.apiBaseUrl}${ENDPOINTS.MY_LOOKS}`, request);
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

  publishLook(id: number): Observable<void> {
    return this.http.put<void>(`${environmentDev.apiBaseUrl}${ENDPOINTS.MY_LOOKS}/${id}/publish`, {});
  }

  unpublishLook(id: number): Observable<void> {
    return this.http.put<void>(
      `${environmentDev.apiBaseUrl}${ENDPOINTS.MY_LOOKS}/${id}/unpublish`,
      {},
    );
  }

  deleteLook(id: number): Observable<void> {
    return this.http.delete<void>(`${environmentDev.apiBaseUrl}${ENDPOINTS.MY_LOOKS}/${id}`);
  }

  private fetchLooks(endpoint: string): Observable<LookSummary[]> {
    return this.http.get<LooksResponse>(`${environmentDev.apiBaseUrl}${endpoint}`).pipe(
      map((response) =>
        response.looks.map((look) => ({
          ...look,
          imageUrl: `${environmentDev.apiBaseUrl}${look.imageUrl}`,
        })),
      ),
    );
  }

  private fetchLookDetail(path: string): Observable<LookDetailResponse> {
    return this.http
      .get<LookDetailResponse>(`${environmentDev.apiBaseUrl}${path}`)
      .pipe(
        map((detail) => ({ ...detail, imageUrl: `${environmentDev.apiBaseUrl}${detail.imageUrl}` })),
      );
  }
}
