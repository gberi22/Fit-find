import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { EMPTY_FILTERS, FeedFilters } from '@core/look/feed.model';
import { GENDER_OPTIONS, Gender, STYLE_OPTIONS, Style } from '@shared/models/outfit.model';
import { BudgetRangeComponent } from '@shared/ui/budget-range/budget-range.component';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

const BUDGET_MIN = 0;
const BUDGET_MAX = 500;

@Component({
  selector: 'app-feed',
  imports: [NavbarComponent, FooterComponent, BudgetRangeComponent, MatButtonModule],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeedComponent {
  protected readonly genderOptions = GENDER_OPTIONS;
  protected readonly styleOptions = STYLE_OPTIONS;
  protected readonly placeholders = Array.from({ length: 12 });

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
}
