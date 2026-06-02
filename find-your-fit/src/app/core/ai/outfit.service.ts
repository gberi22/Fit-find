import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '@env/environment';
import {
  OutfitImageRequest,
  OutfitImageResponse,
  OutfitSuggestionRequest,
  OutfitSuggestionResponse,
} from '@shared/models/outfit.model';
import { Observable } from 'rxjs';

const ENDPOINTS = {
  OUTFIT_SUGGESTIONS: '/api/ai/outfit-suggestions',
  OUTFIT_IMAGE: '/api/ai/outfit-image',
} as const;

@Injectable({ providedIn: 'root' })
export class OutfitService {
  private readonly http = inject(HttpClient);

  generate(value: OutfitSuggestionRequest): Observable<OutfitSuggestionResponse> {
    const form = new FormData();
    form.append('gender', value.gender);
    form.append('size', value.size);
    value.clothes.forEach((item) => form.append('clothes', item));
    value.styles.forEach((style) => form.append('styles', style));
    form.append('minPrice', String(value.minPrice));
    form.append('maxPrice', String(value.maxPrice));

    if (value.additionalComments.trim()) {
      form.append('additionalComments', value.additionalComments.trim());
    }
    value.additionalImages.forEach((file) => form.append('additionalImages', file, file.name));

    return this.http.post<OutfitSuggestionResponse>(
      `${environment.apiBaseUrl}${ENDPOINTS.OUTFIT_SUGGESTIONS}`,
      form,
    );
  }

  generateImage(value: OutfitImageRequest): Observable<OutfitImageResponse> {
    return this.http.post<OutfitImageResponse>(
      `${environment.apiBaseUrl}${ENDPOINTS.OUTFIT_IMAGE}`,
      value,
    );
  }
}
