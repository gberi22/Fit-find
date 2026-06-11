import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '@auth/auth.service';
import { EMPTY_FILTERS, FeedFilters } from '@core/feed/feed.model';
import { FeedService } from '@core/feed/feed.service';
import { LookSummary } from '@shared/models/look-card.model';
import { GENDER_OPTIONS, Gender, STYLE_OPTIONS, Style } from '@shared/models/outfit.model';
import { BudgetRangeComponent } from '@shared/ui/budget-range/budget-range.component';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { LookDetailComponent } from '@shared/ui/look-detail/look-detail.component';
import { LookGridComponent } from '@shared/ui/look-grid/look-grid.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';
import { finalize } from 'rxjs';

const PAGE_SIZE = 12;
const BUDGET_MIN = 0;
const BUDGET_MAX = 500;

@Component({
  selector: 'app-feed',
  imports: [
    NavbarComponent,
    FooterComponent,
    BudgetRangeComponent,
    LookGridComponent,
    LookDetailComponent,
    LoadingSpinnerComponent,
    MatButtonModule,
  ],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeedComponent implements OnInit {
  private readonly feedService = inject(FeedService);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly genderOptions = GENDER_OPTIONS;
  protected readonly styleOptions = STYLE_OPTIONS;
  protected readonly budgetMin = BUDGET_MIN;
  protected readonly budgetMax = BUDGET_MAX;

  protected readonly filtersOpen = signal(true);
  protected readonly gender = signal<Gender | null>(null);
  protected readonly styles = signal<Style[]>([]);
  protected readonly minBudget = signal<number>(BUDGET_MIN);
  protected readonly maxBudget = signal<number>(BUDGET_MAX);

  protected readonly looks = signal<LookSummary[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);

  protected readonly selectedLookId = signal<number | null>(null);
  protected readonly canSave = computed(() => this.authService.isAuthenticated());

  private appliedFilters: FeedFilters = EMPTY_FILTERS;

  protected readonly hasActiveFilters = computed(
    () =>
      this.gender() !== null ||
      this.styles().length > 0 ||
      this.minBudget() !== BUDGET_MIN ||
      this.maxBudget() !== BUDGET_MAX,
  );

  protected readonly canPrev = computed(() => this.page() > 0);
  protected readonly canNext = computed(() => this.page() + 1 < this.totalPages());

  ngOnInit(): void {
    this.load();
  }

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
    this.appliedFilters = {
      gender: this.gender(),
      styles: this.styles(),
      minBudget: this.minBudget(),
      maxBudget: this.maxBudget(),
    };
    this.page.set(0);
    this.load();
  }

  protected clearFilters(): void {
    this.gender.set(null);
    this.styles.set([]);
    this.minBudget.set(BUDGET_MIN);
    this.maxBudget.set(BUDGET_MAX);
    this.appliedFilters = EMPTY_FILTERS;
    this.page.set(0);
    this.load();
  }

  protected prevPage(): void {
    if (this.canPrev()) {
      this.page.update((current) => current - 1);
      this.load();
    }
  }

  protected nextPage(): void {
    if (this.canNext()) {
      this.page.update((current) => current + 1);
      this.load();
    }
  }

  protected openLook(look: LookSummary): void {
    this.selectedLookId.set(look.id);
  }

  protected closeLook(): void {
    this.selectedLookId.set(null);
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(false);
    this.feedService
      .getFeed(this.appliedFilters, this.page(), PAGE_SIZE)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (response) => {
          this.looks.set(response.looks);
          this.totalPages.set(response.totalPages);
        },
        error: () => {
          this.error.set(true);
          this.looks.set([]);
          this.totalPages.set(0);
        },
      });
  }
}
