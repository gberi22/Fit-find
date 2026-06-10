import { ChangeDetectionStrategy, Component, input, model } from '@angular/core';

@Component({
  selector: 'app-budget-range',
  templateUrl: './budget-range.component.html',
  styleUrl: './budget-range.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BudgetRangeComponent {
  readonly floor = input(0);
  readonly ceil = input(500);
  readonly gap = input(5);

  readonly min = model.required<number>();
  readonly max = model.required<number>();

  protected percent(value: number): number {
    return ((value - this.floor()) / (this.ceil() - this.floor())) * 100;
  }

  protected onMinSlide(event: Event): void {
    const target = event.target as HTMLInputElement;
    const clamped = Math.min(target.valueAsNumber, this.max() - this.gap());
    this.min.set(clamped);
    target.value = String(clamped);
  }

  protected onMaxSlide(event: Event): void {
    const target = event.target as HTMLInputElement;
    const clamped = Math.max(target.valueAsNumber, this.min() + this.gap());
    this.max.set(clamped);
    target.value = String(clamped);
  }

  protected onMinInput(value: number): void {
    this.min.set(Math.min(this.clamp(value), this.max()));
  }

  protected onMaxInput(value: number): void {
    this.max.set(Math.max(this.clamp(value), this.min()));
  }

  private clamp(value: number): number {
    if (Number.isNaN(value)) {
      return this.floor();
    }
    return Math.max(this.floor(), Math.min(this.ceil(), value));
  }
}
