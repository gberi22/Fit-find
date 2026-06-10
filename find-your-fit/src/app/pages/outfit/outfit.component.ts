import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import { OutfitService } from '@core/ai/outfit.service';
import { ProfileService } from '@core/profile/profile.service';
import { Suggestion, clothingItemLabel } from '@shared/models/outfit.model';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';
import { errorMessage } from '@shared/utils/errorMessageHandler';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-outfit',
  imports: [LoadingSpinnerComponent, NavbarComponent, RouterLink],
  templateUrl: './outfit.component.html',
  styleUrl: './outfit.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutfitComponent {
  private readonly outfitService = inject(OutfitService);
  private readonly outfitState = inject(OutfitStateService);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);

  readonly items: Suggestion[] = this.outfitState.selected();

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly saving = signal(false);
  readonly saved = signal(false);
  readonly saveError = signal<string | null>(null);

  readonly clothingItemLabel = clothingItemLabel;

  private readonly errorMessageProp = 'assembling your outfit';

  private readonly image = signal<{ imageBase64: string; mimeType: string } | null>(null);
  readonly imageSrc = computed(() => {
    const img = this.image();
    return img ? `data:${img.mimeType};base64,${img.imageBase64}` : null;
  });

  constructor() {
    const request = this.outfitState.request();
    if (!request || this.items.length === 0) {
      this.router.navigateByUrl('/results', { replaceUrl: true });
      return;
    }

    const cached = this.outfitState.image();
    if (cached?.imageBase64) {
      this.image.set({ imageBase64: cached.imageBase64, mimeType: cached.mimeType });
      return;
    }

    this.loading.set(true);
    this.outfitService
      .generateImage({ gender: request.gender, suggestions: this.items })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (res) => {
          if (res.imageBase64) {
            this.image.set({ imageBase64: res.imageBase64, mimeType: res.mimeType });
            this.outfitState.setImage(res);
          } else {
            this.errorMessage.set(
              res.message ?? 'We could not assemble your outfit. Please try again.',
            );
          }
        },
        error: (err: unknown) => this.errorMessage.set(errorMessage(err, this.errorMessageProp)),
      });
  }

  save(published: boolean): void {
    if (this.saving() || this.saved()) {
      return;
    }

    const request = this.outfitState.request();
    const image = this.image();
    if (!request || !image) {
      return;
    }

    this.saving.set(true);
    this.saveError.set(null);

    this.profileService
      .saveLook({
        gender: request.gender,
        size: request.size,
        styles: request.styles,
        budgetMin: request.minPrice,
        budgetMax: request.maxPrice,
        suggestions: this.items,
        imageBase64: image.imageBase64,
        imageMimeType: image.mimeType,
        published,
      })
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.saved.set(true);
          // The look now lives in the profile; drop the ephemeral outfit so the
          // View Outfit page/tab can no longer be reached.
          this.outfitState.clearOutfit();
        },
        error: (err: unknown) => this.saveError.set(errorMessage(err, 'saving your look')),
      });
  }
}
