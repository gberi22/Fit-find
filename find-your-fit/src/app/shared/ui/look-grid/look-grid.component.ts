import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { LookSummary } from '@shared/models/look-card.model';

@Component({
  selector: 'app-look-grid',
  templateUrl: './look-grid.component.html',
  styleUrl: './look-grid.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LookGridComponent {
  readonly looks = input.required<LookSummary[]>();
  readonly lookSelected = output<LookSummary>();
}
