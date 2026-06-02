import { ChangeDetectionStrategy, Component, OnDestroy, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { OutfitStateService } from '@core/ai/outfit-state.service';
import {
  CLOTHING_ITEM_OPTIONS,
  ClothingItem,
  GENDER_OPTIONS,
  Gender,
  OutfitSuggestionRequest,
  SIZE_OPTIONS,
  STYLE_OPTIONS,
  Size,
  Style,
} from '@shared/models/outfit.model';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

const MAX_IMAGES = 5;

// "Full Outfit" is exclusive — it can't be combined with individual pieces.
const FULL_OUTFIT: ClothingItem = 'FULL_OUTFIT';

const BUDGET_MIN = 0;
const BUDGET_MAX = 500;
const MIN_MAX_BUDGET_GAP = 5;
const DEFAULT_MIN_PRICE = 50;
const DEFAULT_MAX_PRICE = 200;

interface GenerateForm {
  gender: FormControl<Gender | null>;
  size: FormControl<Size | null>;
  clothes: FormControl<ClothingItem[]>;
  styles: FormControl<Style[]>;
  minPrice: FormControl<number | null>;
  maxPrice: FormControl<number | null>;
  additionalComments: FormControl<string>;
}

interface ImagePreview {
  file: File;
  url: string;
}

function budgetRange(group: AbstractControl): ValidationErrors | null {
  const min = group.get('minPrice')?.value;
  const max = group.get('maxPrice')?.value;
  return min != null && max != null && min >= max ? { budgetRange: true } : null;
}

function nonEmptyArray(control: AbstractControl): ValidationErrors | null {
  return Array.isArray(control.value) && control.value.length > 0 ? null : { required: true };
}

@Component({
  selector: 'app-generate',
  imports: [
    ReactiveFormsModule,
    NavbarComponent,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './generate.component.html',
  styleUrl: './generate.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GenerateComponent implements OnDestroy {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly outfitState = inject(OutfitStateService);
  private readonly router = inject(Router);

  readonly genderOptions = GENDER_OPTIONS;
  readonly sizeOptions = SIZE_OPTIONS;
  readonly styleOptions = STYLE_OPTIONS;
  readonly clothingOptions = CLOTHING_ITEM_OPTIONS;
  readonly maxImages = MAX_IMAGES;
  readonly budgetMin = BUDGET_MIN;
  readonly budgetMax = BUDGET_MAX;
  readonly minMaxBudgetGap = MIN_MAX_BUDGET_GAP;

  readonly form = this.formBuilder.group<GenerateForm>(
    {
      gender: this.formBuilder.control<Gender | null>(null, Validators.required),
      size: this.formBuilder.control<Size | null>(null, Validators.required),
      clothes: this.formBuilder.control<ClothingItem[]>([], nonEmptyArray),
      styles: this.formBuilder.control<Style[]>([], nonEmptyArray),
      minPrice: this.formBuilder.control<number | null>(DEFAULT_MIN_PRICE, [
        Validators.required,
        Validators.min(0),
      ]),
      maxPrice: this.formBuilder.control<number | null>(DEFAULT_MAX_PRICE, [
        Validators.required,
        Validators.min(0),
      ]),
      additionalComments: this.formBuilder.control(''),
    },
    { validators: budgetRange },
  );

  readonly images = signal<ImagePreview[]>([]);

  isClothingSelected(value: ClothingItem): boolean {
    return this.form.controls.clothes.value.includes(value);
  }

  isStyleSelected(value: Style): boolean {
    return this.form.controls.styles.value.includes(value);
  }

  toggleClothing(value: ClothingItem): void {
    const control = this.form.controls.clothes;
    if (value === FULL_OUTFIT) {
      // Selecting Full Outfit clears everything else; toggling it off empties the list.
      control.setValue(control.value.includes(FULL_OUTFIT) ? [] : [FULL_OUTFIT]);
      control.markAsTouched();
      return;
    }
    this.toggle(control, value);
  }

  // While Full Outfit is selected, the individual pieces are locked out.
  isClothingDisabled(value: ClothingItem): boolean {
    return value !== FULL_OUTFIT && this.form.controls.clothes.value.includes(FULL_OUTFIT);
  }

  toggleStyle(value: Style): void {
    this.toggle(this.form.controls.styles, value);
  }

  private toggle<T>(control: FormControl<T[]>, value: T): void {
    const current = control.value;
    const next = current.includes(value) ? current.filter((v) => v !== value) : [...current, value];
    control.setValue(next);
    control.markAsTouched();
  }

  priceCalculatorForSlider(value: number | null): number {
    const v = value ?? BUDGET_MIN;
    return ((v - BUDGET_MIN) / (BUDGET_MAX - BUDGET_MIN)) * 100;
  }

  onMinSlide(event: Event): void {
    const input = event.target as HTMLInputElement;
    const max = this.form.controls.maxPrice.value ?? BUDGET_MAX;
    const clamped = Math.min(input.valueAsNumber, max - MIN_MAX_BUDGET_GAP);
    this.form.controls.minPrice.setValue(clamped);
    input.value = String(clamped);
  }

  onMaxSlide(event: Event): void {
    const input = event.target as HTMLInputElement;
    const min = this.form.controls.minPrice.value ?? BUDGET_MIN;
    const clamped = Math.max(input.valueAsNumber, min + MIN_MAX_BUDGET_GAP);
    this.form.controls.maxPrice.setValue(clamped);
    input.value = String(clamped);
  }

  onMinInput(value: number): void {
    const max = this.form.controls.maxPrice.value ?? BUDGET_MAX;
    this.form.controls.minPrice.setValue(Math.min(this.clampBudget(value), max));
    this.form.controls.minPrice.markAsTouched();
  }

  onMaxInput(value: number): void {
    const min = this.form.controls.minPrice.value ?? BUDGET_MIN;
    this.form.controls.maxPrice.setValue(Math.max(this.clampBudget(value), min));
    this.form.controls.maxPrice.markAsTouched();
  }

  private clampBudget(value: number): number {
    if (Number.isNaN(value)) {
      return BUDGET_MIN;
    }
    return Math.max(BUDGET_MIN, Math.min(BUDGET_MAX, value));
  }

  blockNonNumeric(event: KeyboardEvent): void {
    const control = [
      'Backspace',
      'Delete',
      'Tab',
      'Escape',
      'Enter',
      'ArrowLeft',
      'ArrowRight',
      'ArrowUp',
      'ArrowDown',
      'Home',
      'End',
    ];
    if (control.includes(event.key) || event.ctrlKey || event.metaKey) {
      return;
    }
    if (!/^[0-9]$/.test(event.key)) {
      event.preventDefault();
    }
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const picked = Array.from(input.files ?? []).filter((f) => f.type.startsWith('image/'));
    const room = MAX_IMAGES - this.images().length;
    const added = picked.slice(0, room).map((file) => ({
      file,
      url: URL.createObjectURL(file),
    }));
    this.images.update((current) => [...current, ...added]);
    input.value = '';
  }

  removeImage(index: number): void {
    this.images.update((current) => {
      const removed = current[index];
      if (removed) {
        URL.revokeObjectURL(removed.url);
      }
      return current.filter((_, i) => i !== index);
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const request: OutfitSuggestionRequest = {
      gender: raw.gender!,
      size: raw.size!,
      clothes: raw.clothes,
      styles: raw.styles,
      minPrice: raw.minPrice!,
      maxPrice: raw.maxPrice!,
      additionalComments: raw.additionalComments,
      additionalImages: this.images().map((i) => i.file),
    };

    this.outfitState.setRequest(request);
    this.router.navigateByUrl('/results');
  }

  ngOnDestroy(): void {
    this.images().forEach((i) => URL.revokeObjectURL(i.url));
  }
}
