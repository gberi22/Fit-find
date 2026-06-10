import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@auth/auth.service';
import { OutfitStateService } from '@core/ai/outfit-state.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly outfitState = inject(OutfitStateService);

  readonly isAuthenticated = this.auth.isAuthenticated;
  readonly outfitRequest = this.outfitState.request;
  readonly outfitSelected = this.outfitState.selected;

  readonly menuOpen = signal(false);

  toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }

  logout(): void {
    this.closeMenu();
    this.auth.logout();
    this.router.navigateByUrl('/login', { replaceUrl: true });
  }
}
