import { Injectable, signal } from '@angular/core';
import {
  OutfitImageResponse,
  OutfitSuggestionRequest,
  OutfitSuggestionResponse,
  Suggestion,
} from '@shared/models/outfit.model';

@Injectable({ providedIn: 'root' })
export class OutfitStateService {
  private readonly _request = signal<OutfitSuggestionRequest | null>(null);
  private readonly _response = signal<OutfitSuggestionResponse | null>(null);
  private readonly _selected = signal<Suggestion[]>([]);
  private readonly _image = signal<OutfitImageResponse | null>(null);

  readonly request = this._request.asReadonly();
  readonly response = this._response.asReadonly();
  readonly selected = this._selected.asReadonly();
  readonly image = this._image.asReadonly();

  setRequest(request: OutfitSuggestionRequest): void {
    this._request.set(request);
    this._response.set(null);
  }

  setResponse(response: OutfitSuggestionResponse): void {
    this._response.set(response);
  }

  setSelected(suggestions: Suggestion[]): void {
    this._selected.set(suggestions);
  }

  setImage(image: OutfitImageResponse): void {
    this._image.set(image);
  }

  clear(): void {
    this._request.set(null);
    this._response.set(null);
    this._selected.set([]);
    this._image.set(null);
  }
}
