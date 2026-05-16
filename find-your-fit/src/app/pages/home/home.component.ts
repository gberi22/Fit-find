import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@auth/auth.service';

interface PlaceholderLook {
  style: string;
  title: string;
  rating: string;
  budget: string;
  icon: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly isAuthenticated = this.auth.isAuthenticated;

  readonly placeholderLooks: readonly PlaceholderLook[] = [
    { style: 'Minimalist', title: 'Sunday Brunch', rating: '4.8', budget: '$120', icon: '🤍' },
    { style: 'Streetwear', title: 'City Walk', rating: '4.5', budget: '$180', icon: '🖤' },
    { style: 'Vintage', title: 'Café Date', rating: '4.9', budget: '$95', icon: '🤎' },
    { style: 'Formal', title: 'Evening Gala', rating: '4.7', budget: '$340', icon: '💛' },
  ];

  signOut(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
