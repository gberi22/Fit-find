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
import { LookDetailComponent, LookDetailMode } from '@shared/ui/look-detail/look-detail.component';
import { LookGridComponent } from '@shared/ui/look-grid/look-grid.component';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

type ProfileTab = 'your-looks' | 'saved';
type LookFilter = 'all' | 'published' | 'drafts';

@Component({
  selector: 'app-profile',
  imports: [
    FooterComponent,
    NavbarComponent,
    LoadingSpinnerComponent,
    LookGridComponent,
    LookDetailComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent implements OnInit {
  private readonly profileService = inject(ProfileService);

  readonly displayName = signal('');

  readonly activeTab = signal<ProfileTab>('your-looks');
  readonly filter = signal<LookFilter>('all');

  readonly yourLooks = signal<LookSummary[]>([]);
  readonly yourLooksLoading = signal(true);
  readonly yourLooksError = signal(false);

  readonly savedLooks = signal<LookSummary[]>([]);
  readonly savedLoading = signal(true);
  readonly savedError = signal(false);

  readonly filteredYourLooks = computed(() => {
    const looks = this.yourLooks();
    switch (this.filter()) {
      case 'published':
        return looks.filter((look) => look.published);
      case 'drafts':
        return looks.filter((look) => !look.published);
      default:
        return looks;
    }
  });

  readonly selectedLookId = signal<number | null>(null);
  readonly selectedMode = signal<LookDetailMode>('owner');

  ngOnInit(): void {
    this.profileService.getFullName().subscribe({
      next: (fullName) => this.displayName.set(fullName),
    });

    this.profileService.getMyLooks().subscribe({
      next: (looks) => {
        this.yourLooks.set(looks);
        this.yourLooksLoading.set(false);
      },
      error: () => {
        this.yourLooksError.set(true);
        this.yourLooksLoading.set(false);
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

  openLook(look: LookSummary, mode: LookDetailMode): void {
    this.selectedMode.set(mode);
    this.selectedLookId.set(look.id);
  }

  closeLook(): void {
    this.selectedLookId.set(null);
  }

  onPublished(lookId: number): void {
    this.setPublished(lookId, true);
  }

  onUnpublished(lookId: number): void {
    this.setPublished(lookId, false);
  }

  onDeleted(lookId: number): void {
    this.yourLooks.update((looks) => looks.filter((look) => look.id !== lookId));
    this.closeLook();
  }

  onUnsaved(lookId: number): void {
    this.savedLooks.update((looks) => looks.filter((look) => look.id !== lookId));
    this.closeLook();
  }

  private setPublished(lookId: number, published: boolean): void {
    this.yourLooks.update((looks) =>
      looks.map((look) => (look.id === lookId ? { ...look, published } : look)),
    );
  }
}
