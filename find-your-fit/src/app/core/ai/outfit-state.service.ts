import { Injectable, effect, signal } from '@angular/core';
import {
  OutfitImageResponse,
  OutfitSuggestionRequest,
  OutfitSuggestionResponse,
  Suggestion,
} from '@shared/models/outfit.model';

const STATE_KEY = 'fitfind.outfit-state';
const IMAGE_KEY = 'fitfind.outfit-image';

interface PersistedState {
  request: OutfitSuggestionRequest | null;
  response: OutfitSuggestionResponse | null;
  selected: Suggestion[];
}

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

  constructor() {
    this.restore();
    effect(() => this.persist());
  }

  setRequest(request: OutfitSuggestionRequest): void {
    this._request.set(request);
    this._response.set(null);
    this._selected.set([]);
    this._image.set(null);
  }

  setResponse(response: OutfitSuggestionResponse): void {
    this._response.set(response);
  }

  setSelected(suggestions: Suggestion[]): void {
    this._selected.set(suggestions);
    this._image.set(null);
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

  private persist(): void {
    const request = this.toStorableRequest(this._request());
    const response = this._response();
    const selected = this._selected();
    const empty = !request && !response && selected.length === 0;
    const state: PersistedState = { request, response, selected };
    this.write(STATE_KEY, empty ? null : state);
    this.write(IMAGE_KEY, this._image());
  }

  private restore(): void {
    const state = this.read<PersistedState>(STATE_KEY);
    if (state) {
      this._request.set(state.request ?? null);
      this._response.set(state.response ?? null);
      this._selected.set(state.selected ?? []);
    }
    const image = this.read<OutfitImageResponse>(IMAGE_KEY);
    if (image) {
      this._image.set(image);
    }
  }

  private toStorableRequest(
    request: OutfitSuggestionRequest | null,
  ): OutfitSuggestionRequest | null {
    return request ? { ...request, additionalImages: [] } : null;
  }

  private write(key: string, value: unknown): void {
    try {
      if (value == null) {
        sessionStorage.removeItem(key);
      } else {
        sessionStorage.setItem(key, JSON.stringify(value));
      }
    } catch (error) {
      console.warn(`Failed to persist outfit state ("${key}") to sessionStorage`, error);
    }
  }

  private read<T>(key: string): T | null {
    try {
      const raw = sessionStorage.getItem(key);
      return raw ? (JSON.parse(raw) as T) : null;
    } catch {
      return null;
    }
  }
}
