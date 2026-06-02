import { Injectable, signal } from '@angular/core';
import { OutfitImageResponse, OutfitSuggestionRequest } from '@shared/models/outfit.model';

@Injectable({ providedIn: 'root' })
export class OutfitStateService {
  private readonly _request = signal<OutfitSuggestionRequest | null>(null);
  private readonly _image = signal<OutfitImageResponse | null>(null);

  readonly request = this._request.asReadonly();
  readonly image = this._image.asReadonly();

  setRequest(request: OutfitSuggestionRequest): void {
    this._request.set(request);
  }

  setImage(image: OutfitImageResponse): void {
    this._image.set(image);
  }

  clear(): void {
    this._request.set(null);
    this._image.set(null);
  }
}
