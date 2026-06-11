import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '@env/environment';
import { LookDetailResponse } from '@shared/models/look-card.model';
import { Observable, map } from 'rxjs';
import { FeedFilters, FeedResponse } from './feed.model';

const ENDPOINTS = {
  FEED: '/api/public/feed',
  PUBLIC_LOOKS: '/api/public/looks',
  FEED_LOOKS: '/api/feed/looks',
} as const;

@Injectable({ providedIn: 'root' })
export class FeedService {
  private readonly http = inject(HttpClient);

  getFeed(filters: FeedFilters, page: number, size: number): Observable<FeedResponse> {
    let params = new HttpParams().set('page', page).set('size', size);

    if (filters.gender) {
      params = params.set('gender', filters.gender);
    }
    for (const style of filters.styles) {
      params = params.append('style', style);
    }
    if (filters.minBudget !== null) {
      params = params.set('minBudget', filters.minBudget);
    }
    if (filters.maxBudget !== null) {
      params = params.set('maxBudget', filters.maxBudget);
    }

    return this.http
      .get<FeedResponse>(`${environment.apiBaseUrl}${ENDPOINTS.FEED}`, { params })
      .pipe(
        map((response) => ({
          ...response,
          looks: response.looks.map((look) => ({
            ...look,
            imageUrl: `${environment.apiBaseUrl}${look.imageUrl}`,
          })),
        })),
      );
  }

  getLookDetail(id: number): Observable<LookDetailResponse> {
    return this.http
      .get<LookDetailResponse>(`${environment.apiBaseUrl}${ENDPOINTS.PUBLIC_LOOKS}/${id}`)
      .pipe(
        map((detail) => ({ ...detail, imageUrl: `${environment.apiBaseUrl}${detail.imageUrl}` })),
      );
  }

  saveLook(id: number): Observable<void> {
    return this.http.post<void>(`${environment.apiBaseUrl}${ENDPOINTS.FEED_LOOKS}/${id}`, {});
  }

  unsaveLook(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}${ENDPOINTS.FEED_LOOKS}/${id}`);
  }
}
