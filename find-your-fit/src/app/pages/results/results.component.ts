import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import { OutfitService } from '@core/ai/outfit.service';
import {
  CategorySuggestions,
  ClothingItem,
  OutfitSuggestionResponse,
  Suggestion,
  clothingItemLabel,
} from '@shared/models/outfit.model';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';
import { finalize } from 'rxjs';
import {errorMessage} from '@shared/utils/errorMessageHandler';

@Component({
  selector: 'app-results',
  imports: [LoadingSpinnerComponent, NavbarComponent, RouterLink],
  templateUrl: './results.component.html',
  styleUrl: './results.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResultsComponent {
  private readonly outfitService = inject(OutfitService);
  private readonly outfitState = inject(OutfitStateService);
  private readonly router = inject(Router);

  private readonly request = this.outfitState.request();

  readonly loading = signal(false);
  readonly response = signal<OutfitSuggestionResponse | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly selections = signal<Map<ClothingItem, Suggestion>>(new Map());
  readonly retrying = signal<Set<ClothingItem>>(new Set());

  readonly clothingItemLabel = clothingItemLabel;

  private readonly errorMessageProp = 'styling your look';

  private readonly categoriesWithOptions = computed(
    () => this.response()?.categories.filter((cat) => cat.options.length > 0) ?? [],
  );

  readonly canAssemble = computed(() => {
    const selectable = this.categoriesWithOptions();
    if (selectable.length === 0) {
      return false;
    }
    const selected = this.selections();
    return selectable.every((cat) => selected.has(cat.category));
  });

  constructor() {
    if (!this.request) {
      this.router.navigateByUrl('/generate');
      return;
    }

    this.loading.set(true);
    this.outfitService
      .generate(this.request)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => this.response.set(response),
        error: (err: unknown) => this.errorMessage.set(errorMessage(err, this.errorMessageProp)),
      });
  }

  isSelected(category: ClothingItem, suggestion: Suggestion): boolean {
    return this.selections().get(category) === suggestion;
  }

  isRetrying(category: ClothingItem): boolean {
    return this.retrying().has(category);
  }

  retry(category: ClothingItem): void {
    if (!this.request || this.isRetrying(category)) {
      return;
    }

    this.retrying.update((set) => new Set(set).add(category));

    this.outfitService
      .retryCategory(this.request, category)
      .pipe(
        finalize(() =>
          this.retrying.update((set) => {
            const next = new Set(set);
            next.delete(category);
            return next;
          }),
        ),
      )
      .subscribe({
        next: (updated) => this.replaceCategory(updated),
        error: (err: unknown) => this.errorMessage.set(errorMessage(err, this.errorMessageProp)),
      });
  }

  private replaceCategory(updated: CategorySuggestions): void {
    const current = this.response();
    if (!current) {
      return;
    }
    this.response.set({
      categories: current.categories.map((cat) =>
        cat.category === updated.category ? updated : cat,
      ),
    });
  }

  select(category: ClothingItem, suggestion: Suggestion): void {
    const next = new Map(this.selections());
    next.set(category, suggestion);
    this.selections.set(next);
  }

  assemble(): void {
    if (!this.request || !this.canAssemble()) {
      return;
    }

    this.outfitState.setSelected([...this.selections().values()]);
    this.router.navigateByUrl('/outfit');
  }
}
