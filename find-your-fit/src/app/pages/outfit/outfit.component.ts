import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import { OutfitService } from '@core/ai/outfit.service';
import { Suggestion, clothingItemLabel } from '@shared/models/outfit.model';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-outfit',
  imports: [NavbarComponent, RouterLink],
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

  private readonly image = signal<{ imageBase64: string; mimeType: string } | null>(null);
  readonly imageSrc = computed(() => {
    const img = this.image();
    return img ? `data:${img.mimeType};base64,${img.imageBase64}` : null;
  });

  constructor() {
    const request = this.outfitState.request();
    if (!request || this.items.length === 0) {
      this.router.navigateByUrl('/generate');
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
          } else {
            this.errorMessage.set(
              res.message ?? 'We could not assemble your outfit. Please try again.',
            );
          }
        },
        error: (err: unknown) => this.errorMessage.set(this.toErrorMessage(err)),
      });
  }

  private toErrorMessage(err: unknown): string {
    const status = (err as { status?: number })?.status;
    if (status === 429) {
      return "You've hit the generation limit. Please wait a while and try again.";
    }
    return 'Something went wrong while assembling your outfit. Please try again.';
  }
}
