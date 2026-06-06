import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import { OutfitService } from '@core/ai/outfit.service';
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
  private readonly router = inject(Router);

  readonly items: Suggestion[] = this.outfitState.selected();

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

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
      this.router.navigateByUrl('/generate', { replaceUrl: true });
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
}
