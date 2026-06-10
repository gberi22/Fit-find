import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { ProfileService } from '@core/profile/profile.service';
import { LookDetailResponse, LookSummary } from '@shared/models/look-card.model';
import { clothingItemLabel } from '@shared/models/outfit.model';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';
import { finalize } from 'rxjs';

type ProfileTab = 'generated' | 'saved';
type LookFilter = 'all' | 'published' | 'drafts';

@Component({
  selector: 'app-profile',
  imports: [NavbarComponent, LoadingSpinnerComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent implements OnInit {
  private readonly profileService = inject(ProfileService);

  readonly displayName = signal('');

  readonly activeTab = signal<ProfileTab>('generated');
  readonly filter = signal<LookFilter>('all');

  readonly generatedLooks = signal<LookSummary[]>([]);
  readonly generatedLoading = signal(true);
  readonly generatedError = signal(false);

  readonly savedLooks = signal<LookSummary[]>([]);
  readonly savedLoading = signal(true);
  readonly savedError = signal(false);

  readonly filteredGenerated = computed(() => {
    const looks = this.generatedLooks();
    switch (this.filter()) {
      case 'published':
        return looks.filter((look) => look.published);
      case 'drafts':
        return looks.filter((look) => !look.published);
      default:
        return looks;
    }
  });

  readonly modalOpen = signal(false);
  readonly selectedLook = signal<LookDetailResponse | null>(null);
  readonly detailLoading = signal(false);
  readonly detailError = signal(false);
  readonly ownedLook = signal(false);
  readonly publishing = signal(false);
  readonly publishError = signal(false);

  readonly clothingItemLabel = clothingItemLabel;

  ngOnInit(): void {
    this.profileService.getFullName().subscribe({
      next: (fullName) => this.displayName.set(fullName),
    });

    this.profileService.getMyLooks().subscribe({
      next: (looks) => {
        this.generatedLooks.set(looks);
        this.generatedLoading.set(false);
      },
      error: () => {
        this.generatedError.set(true);
        this.generatedLoading.set(false);
      },
    });

    this.profileService.getSavedLooks().subscribe({
      next: (looks) => {
        this.savedLooks.set(looks);
        this.savedLoading.set(false);
      },
      error: () => {
        this.savedError.set(true);
        this.savedLoading.set(false);
      },
    });
  }

  selectTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
  }

  selectFilter(value: LookFilter): void {
    this.filter.set(value);
  }

  openLook(lookId: number, owned: boolean): void {
    this.ownedLook.set(owned);
    this.modalOpen.set(true);
    this.selectedLook.set(null);
    this.detailError.set(false);
    this.publishError.set(false);
    this.detailLoading.set(true);

    const detail$ = owned
      ? this.profileService.getLook(lookId)
      : this.profileService.getPublicLook(lookId);

    detail$.pipe(finalize(() => this.detailLoading.set(false))).subscribe({
      next: (detail) => this.selectedLook.set(detail),
      error: () => this.detailError.set(true),
    });
  }

  closeLook(): void {
    this.modalOpen.set(false);
    this.selectedLook.set(null);
  }

  publish(): void {
    const detail = this.selectedLook();
    if (!detail || this.publishing()) {
      return;
    }

    this.publishing.set(true);
    this.publishError.set(false);

    this.profileService
      .publishLook(detail.id)
      .pipe(finalize(() => this.publishing.set(false)))
      .subscribe({
        next: () => {
          this.selectedLook.set({ ...detail, published: true });
          this.generatedLooks.update((looks) =>
            looks.map((look) => (look.id === detail.id ? { ...look, published: true } : look)),
          );
        },
        error: () => this.publishError.set(true),
      });
  }
}
