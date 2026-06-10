import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { ProfileService } from '@core/profile/profile.service';
import { LookSummary } from '@shared/models/look-card.model';
import { LoadingSpinnerComponent } from '@shared/ui/loading-spinner/loading-spinner.component';
import { LookDetailComponent } from '@shared/ui/look-detail/look-detail.component';
import { LookGridComponent } from '@shared/ui/look-grid/look-grid.component';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

type ProfileTab = 'generated' | 'saved';
type LookFilter = 'all' | 'published' | 'drafts';

@Component({
  selector: 'app-profile',
  imports: [FooterComponent, NavbarComponent, LoadingSpinnerComponent, LookGridComponent, LookDetailComponent],
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

  // The look open in the detail modal, plus whether it is the user's own look.
  readonly selectedLookId = signal<number | null>(null);
  readonly selectedOwned = signal(false);

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

  openLook(look: LookSummary, owned: boolean): void {
    this.selectedOwned.set(owned);
    this.selectedLookId.set(look.id);
  }

  closeLook(): void {
    this.selectedLookId.set(null);
  }

  onPublished(lookId: number): void {
    this.generatedLooks.update((looks) =>
      looks.map((look) => (look.id === lookId ? { ...look, published: true } : look)),
    );
  }
}
