import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { ProfileLook } from '@shared/models/look-card.model';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

type ProfileTab = 'generated' | 'saved';
type LookFilter = 'all' | 'published' | 'drafts';

@Component({
  selector: 'app-profile',
  imports: [NavbarComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent {
  // TODO: replace with backend
  readonly displayName = 'Jane Doe';

  readonly activeTab = signal<ProfileTab>('generated');
  readonly filter = signal<LookFilter>('all');

  // TODO: replace with backend
  readonly generatedLooks = signal<ProfileLook[]>([]);
  readonly savedLooks = signal<ProfileLook[]>([]);

  readonly filteredGenerated = computed(() => {
    const looks = this.generatedLooks();
    switch (this.filter()) {
      case 'published':
        return looks.filter((look) => look.isPublished);
      case 'drafts':
        return looks.filter((look) => !look.isPublished);
      default:
        return looks;
    }
  });

  selectTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
  }

  selectFilter(value: LookFilter): void {
    this.filter.set(value);
  }
}
