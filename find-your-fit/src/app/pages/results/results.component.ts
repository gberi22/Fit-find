import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import { OutfitService } from '@core/ai/outfit.service';
import {
  ClothingItem,
  OutfitSuggestionResponse,
  Suggestion,
  clothingItemLabel,
} from '@shared/models/outfit.model';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-results',
  imports: [NavbarComponent, RouterLink],
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

  readonly clothingItemLabel = clothingItemLabel;

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
        error: (err: unknown) => this.errorMessage.set(this.toErrorMessage(err)),
      });
  }

  isSelected(category: ClothingItem, suggestion: Suggestion): boolean {
    return this.selections().get(category) === suggestion;
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

  private toErrorMessage(err: unknown): string {
    const status = (err as { status?: number })?.status;
    if (status === 429) {
      return "You've hit the generation limit. Please wait a while and try again.";
    }
    return 'Something went wrong while styling your look. Please try again.';
  }
}
