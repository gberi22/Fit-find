import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '@auth/auth.service';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

interface PlaceholderLook {
  style: string;
  title: string;
  rating: string;
  budget: string;
  icon: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink, NavbarComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent {
  private readonly auth = inject(AuthService);

  readonly isAuthenticated = this.auth.isAuthenticated;

  get startStylingLink(): string {
    return this.isAuthenticated() ? '/generate' : '/register';
  }

  readonly placeholderLooks: readonly PlaceholderLook[] = [
    { style: 'Minimalist', title: 'Sunday Brunch', rating: '4.8', budget: '$120', icon: '🤍' },
    { style: 'Streetwear', title: 'City Walk', rating: '4.5', budget: '$180', icon: '🖤' },
    { style: 'Vintage', title: 'Café Date', rating: '4.9', budget: '$95', icon: '🤎' },
    { style: 'Formal', title: 'Evening Gala', rating: '4.7', budget: '$340', icon: '💛' },
  ];
}
