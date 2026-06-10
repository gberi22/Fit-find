import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { EMPTY_FILTERS, FeedFilters } from '@core/look/feed.model';
import { GENDER_OPTIONS, Gender, STYLE_OPTIONS, Style } from '@shared/models/outfit.model';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

const BUDGET_MIN = 0;
const BUDGET_MAX = 500;
const MIN_MAX_BUDGET_GAP = 5;

@Component({
  selector: 'app-feed',
  imports: [NavbarComponent, FooterComponent, MatButtonModule],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeedComponent {
  protected readonly genderOptions = GENDER_OPTIONS;
  protected readonly styleOptions = STYLE_OPTIONS;
  protected readonly placeholders = Array.from({ length: 12 });
  protected readonly budgetMin = BUDGET_MIN;
  protected readonly budgetMax = BUDGET_MAX;
  protected readonly minMaxBudgetGap = MIN_MAX_BUDGET_GAP;

  protected readonly filtersOpen = signal(true);
  protected readonly gender = signal<Gender | null>(null);
  protected readonly styles = signal<Style[]>([]);
  protected readonly minBudget = signal<number>(BUDGET_MIN);
  protected readonly maxBudget = signal<number>(BUDGET_MAX);

  protected readonly appliedFilters = signal<FeedFilters>(EMPTY_FILTERS);

  protected readonly hasActiveFilters = computed(
    () =>
      this.gender() !== null ||
      this.styles().length > 0 ||
      this.minBudget() !== BUDGET_MIN ||
      this.maxBudget() !== BUDGET_MAX,
  );

  protected toggleFilters(): void {
    this.filtersOpen.update((open) => !open);
  }

  protected selectGender(value: Gender): void {
    this.gender.update((current) => (current === value ? null : value));
  }

  protected toggleStyle(value: Style): void {
    this.styles.update((current) =>
      current.includes(value) ? current.filter((style) => style !== value) : [...current, value],
    );
  }

  protected isStyleSelected(value: Style): boolean {
    return this.styles().includes(value);
  }

  protected priceCalculatorForSlider(value: number): number {
    return ((value - BUDGET_MIN) / (BUDGET_MAX - BUDGET_MIN)) * 100;
  }

  protected onMinSlide(event: Event): void {
    const input = event.target as HTMLInputElement;
    const clamped = Math.min(input.valueAsNumber, this.maxBudget() - MIN_MAX_BUDGET_GAP);
    this.minBudget.set(clamped);
    input.value = String(clamped);
  }

  protected onMaxSlide(event: Event): void {
    const input = event.target as HTMLInputElement;
    const clamped = Math.max(input.valueAsNumber, this.minBudget() + MIN_MAX_BUDGET_GAP);
    this.maxBudget.set(clamped);
    input.value = String(clamped);
  }

  protected onMinInput(value: number): void {
    this.minBudget.set(Math.min(this.clampBudget(value), this.maxBudget()));
  }

  protected onMaxInput(value: number): void {
    this.maxBudget.set(Math.max(this.clampBudget(value), this.minBudget()));
  }

  protected applyFilters(): void {
    this.appliedFilters.set({
      gender: this.gender(),
      styles: this.styles(),
      minBudget: this.minBudget(),
      maxBudget: this.maxBudget(),
    });
    // TODO: pass appliedFilters() to <app-look-grid> / FeedService once the grid lands.
  }

  protected clearFilters(): void {
    this.gender.set(null);
    this.styles.set([]);
    this.minBudget.set(BUDGET_MIN);
    this.maxBudget.set(BUDGET_MAX);
    this.appliedFilters.set(EMPTY_FILTERS);
  }

  private clampBudget(value: number): number {
    if (Number.isNaN(value)) {
      return BUDGET_MIN;
    }
    return Math.max(BUDGET_MIN, Math.min(BUDGET_MAX, value));
  }
}
