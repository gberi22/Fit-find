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
import { BudgetRangeComponent } from '@shared/ui/budget-range/budget-range.component';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

const MAX_IMAGES = 5;

const BUDGET_MIN = 0;
const BUDGET_MAX = 500;
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
    BudgetRangeComponent,
    FooterComponent,
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
    this.toggle(control, value);
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

  setMinPrice(value: number): void {
    this.form.controls.minPrice.setValue(value);
    this.form.controls.minPrice.markAsTouched();
  }

  setMaxPrice(value: number): void {
    this.form.controls.maxPrice.setValue(value);
    this.form.controls.maxPrice.markAsTouched();
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
