import { DatePipe } from '@angular/common';
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
import { RouterLink } from '@angular/router';
import { FeedService } from '@core/feed/feed.service';
import { ProfileService } from '@core/profile/profile.service';
import { LookDetailResponse } from '@shared/models/look-card.model';
import {
  GENDER_OPTIONS,
  Gender,
  SIZE_OPTIONS,
  STYLE_OPTIONS,
  Size,
  Style,
  clothingItemLabel,
} from '@shared/models/outfit.model';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { errorMessage } from '@shared/utils/errorMessageHandler';
import { finalize } from 'rxjs';

export type LookDetailMode = 'owner' | 'saved' | 'public';

@Component({
  selector: 'app-look-detail',
  imports: [LoadingSpinnerComponent, DatePipe, RouterLink],
  templateUrl: './look-detail.component.html',
  styleUrl: './look-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LookDetailComponent {
  private readonly profileService = inject(ProfileService);
  private readonly feedService = inject(FeedService);

  readonly lookId = input.required<number>();
  readonly mode = input<LookDetailMode>('owner');
  readonly canSave = input(false);

  readonly closed = output<void>();
  readonly published = output<number>();
  readonly unpublished = output<number>();
  readonly deleted = output<number>();
  readonly unsaved = output<number>();
  readonly saved = output<number>();

  readonly detail = signal<LookDetailResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal(false);

  readonly pending = signal(false);
  readonly actionError = signal<string | null>(null);
  readonly confirmingDelete = signal(false);
  readonly savedDone = signal(false);

  readonly clothingItemLabel = clothingItemLabel;

  constructor() {
    effect(() => {
      const id = this.lookId();
      this.load(id, untracked(this.mode));
    });
  }

  styleLabel(value: Style): string {
    return STYLE_OPTIONS.find((option) => option.value === value)?.label ?? value;
  }

  genderLabel(value: Gender): string {
    return GENDER_OPTIONS.find((option) => option.value === value)?.label ?? value;
  }

  sizeLabel(value: Size): string {
    return SIZE_OPTIONS.find((option) => option.value === value)?.label ?? value;
  }

  close(): void {
    this.closed.emit();
  }

  publish(): void {
    this.togglePublished(true);
  }

  unpublish(): void {
    this.togglePublished(false);
  }

  requestDelete(): void {
    this.confirmingDelete.set(true);
  }

  cancelDelete(): void {
    this.confirmingDelete.set(false);
  }

  confirmDelete(): void {
    const current = this.detail();
    if (!current || this.pending()) {
      return;
    }
    this.pending.set(true);
    this.actionError.set(null);
    this.profileService
      .deleteLook(current.id)
      .pipe(finalize(() => this.pending.set(false)))
      .subscribe({
        next: () => this.deleted.emit(current.id),
        error: (err: unknown) => {
          this.confirmingDelete.set(false);
          this.actionError.set(errorMessage(err, 'deleting this look'));
        },
      });
  }

  unsave(): void {
    const current = this.detail();
    if (!current || this.pending()) {
      return;
    }
    this.pending.set(true);
    this.actionError.set(null);
    this.feedService
      .unsaveLook(current.id)
      .pipe(finalize(() => this.pending.set(false)))
      .subscribe({
        next: () => this.unsaved.emit(current.id),
        error: (err: unknown) => this.actionError.set(errorMessage(err, 'removing this look')),
      });
  }

  save(): void {
    const current = this.detail();
    if (!current || this.pending() || this.savedDone()) {
      return;
    }
    this.pending.set(true);
    this.actionError.set(null);
    this.feedService
      .saveLook(current.id)
      .pipe(finalize(() => this.pending.set(false)))
      .subscribe({
        next: () => {
          this.savedDone.set(true);
          this.saved.emit(current.id);
        },
        error: (err: unknown) => this.actionError.set(errorMessage(err, 'saving this look')),
      });
  }

  private togglePublished(publish: boolean): void {
    const current = this.detail();
    if (!current || this.pending()) {
      return;
    }
    this.pending.set(true);
    this.actionError.set(null);
    const request$ = publish
      ? this.profileService.publishLook(current.id)
      : this.profileService.unpublishLook(current.id);
    request$.pipe(finalize(() => this.pending.set(false))).subscribe({
      next: () => {
        this.detail.set({ ...current, published: publish });
        (publish ? this.published : this.unpublished).emit(current.id);
      },
      error: (err: unknown) =>
        this.actionError.set(
          errorMessage(err, publish ? 'publishing this look' : 'unpublishing this look'),
        ),
    });
  }

  private load(id: number, mode: LookDetailMode): void {
    this.detail.set(null);
    this.error.set(false);
    this.actionError.set(null);
    this.confirmingDelete.set(false);
    this.savedDone.set(false);
    this.loading.set(true);

    const detail$ =
      mode === 'owner' ? this.profileService.getLook(id) : this.feedService.getLookDetail(id);

    detail$.pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (detail) => this.detail.set(detail),
      error: () => this.error.set(true),
    });
  }
}
