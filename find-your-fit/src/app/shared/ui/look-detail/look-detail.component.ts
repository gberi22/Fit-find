import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  input,
  output,
  signal,
  untracked,
} from '@angular/core';
import { ProfileService } from '@core/profile/profile.service';
import { LookDetailResponse } from '@shared/models/look-card.model';
import { clothingItemLabel } from '@shared/models/outfit.model';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-look-detail',
  imports: [LoadingSpinnerComponent],
  templateUrl: './look-detail.component.html',
  styleUrl: './look-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LookDetailComponent {
  private readonly profileService = inject(ProfileService);

  readonly lookId = input.required<number>();
  readonly owned = input<boolean>(false);

  readonly closed = output<void>();
  readonly published = output<number>();

  readonly detail = signal<LookDetailResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal(false);
  readonly publishing = signal(false);
  readonly publishError = signal(false);

  readonly clothingItemLabel = clothingItemLabel;

  constructor() {
    effect(() => {
      const id = this.lookId();
      this.load(id, untracked(this.owned));
    });
  }

  close(): void {
    this.closed.emit();
  }

  publish(): void {
    const current = this.detail();
    if (!current || this.publishing()) {
      return;
    }

    this.publishing.set(true);
    this.publishError.set(false);

    this.profileService
      .publishLook(current.id)
      .pipe(finalize(() => this.publishing.set(false)))
      .subscribe({
        next: () => {
          this.detail.set({ ...current, published: true });
          this.published.emit(current.id);
        },
        error: () => this.publishError.set(true),
      });
  }

  private load(id: number, owned: boolean): void {
    this.detail.set(null);
    this.error.set(false);
    this.publishError.set(false);
    this.loading.set(true);

    const detail$ = owned ? this.profileService.getLook(id) : this.profileService.getPublicLook(id);

    detail$.pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (detail) => this.detail.set(detail),
      error: () => this.error.set(true),
    });
  }
}
