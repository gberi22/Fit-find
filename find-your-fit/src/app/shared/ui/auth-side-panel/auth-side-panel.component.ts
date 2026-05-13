import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-auth-side-panel',
  templateUrl: './auth-side-panel.component.html',
  styleUrl: './auth-side-panel.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthSidePanelComponent {
  readonly icon = input<string>('◈');
  readonly quote = input.required<string>();
  readonly attribution = input.required<string>();
}
