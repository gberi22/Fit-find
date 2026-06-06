import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
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
  private readonly outfitState = inject(OutfitStateService);

  readonly isAuthenticated = this.auth.isAuthenticated;
  readonly outfitRequest = this.outfitState.request;
  readonly outfitSelected = this.outfitState.selected;
}
