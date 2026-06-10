import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '@auth/auth.service';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

@Component({
  selector: 'app-home',
  imports: [RouterLink, NavbarComponent, FooterComponent],
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
}
