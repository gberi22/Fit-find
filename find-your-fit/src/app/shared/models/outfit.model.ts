export type Gender = 'MEN' | 'WOMEN';

export type Size = 'ANY' | 'XS' | 'S' | 'M' | 'L' | 'XL' | 'XXL';

export type Style =
  | 'CASUAL'
  | 'CORPORATE'
  | 'SPORT'
  | 'STREETWEAR'
  | 'CLASSIC'
  | 'GOTHIC'
  | 'ALTERNATIVE'
  | 'TECHWEAR'
  | 'RETRO'
  | 'VINTAGE'
  | 'HIPPIE';

export type ClothingItem =
  | 'SHIRT'
  | 'T_SHIRT'
  | 'DRESS'
  | 'CROP_TOP'
  | 'TROUSERS'
  | 'SKIRT'
  | 'SHORTS'
  | 'JACKET'
  | 'TRENCHCOAT'
  | 'COAT'
  | 'BLOUSE'
  | 'HAT'
  | 'SHOES'
  | 'BOOTS'
  | 'HIGH_HEELS'
  | 'BAG'
  | 'HANDBAG'
  | 'PURSE';

export interface Option<T> {
  value: T;
  label: string;
}

export const GENDER_OPTIONS: readonly Option<Gender>[] = [
  { value: 'MEN', label: 'Men' },
  { value: 'WOMEN', label: 'Women' },
];

export const SIZE_OPTIONS: readonly Option<Size>[] = [
  { value: 'ANY', label: 'Any' },
  { value: 'XS', label: 'XS' },
  { value: 'S', label: 'S' },
  { value: 'M', label: 'M' },
  { value: 'L', label: 'L' },
  { value: 'XL', label: 'XL' },
  { value: 'XXL', label: 'XXL' },
];

export const STYLE_OPTIONS: readonly Option<Style>[] = [
  { value: 'CASUAL', label: 'Casual' },
  { value: 'CORPORATE', label: 'Corporate' },
  { value: 'SPORT', label: 'Sport' },
  { value: 'STREETWEAR', label: 'Streetwear' },
  { value: 'CLASSIC', label: 'Classic' },
  { value: 'GOTHIC', label: 'Gothic' },
  { value: 'ALTERNATIVE', label: 'Alternative' },
  { value: 'TECHWEAR', label: 'Techwear' },
  { value: 'RETRO', label: 'Retro' },
  { value: 'VINTAGE', label: 'Vintage' },
  { value: 'HIPPIE', label: 'Hippie' },
];

export const CLOTHING_ITEM_OPTIONS: readonly Option<ClothingItem>[] = [
  { value: 'SHIRT', label: 'Shirt' },
  { value: 'T_SHIRT', label: 'T-Shirt' },
  { value: 'DRESS', label: 'Dress' },
  { value: 'CROP_TOP', label: 'Crop Top' },
  { value: 'TROUSERS', label: 'Trousers' },
  { value: 'SKIRT', label: 'Skirt' },
  { value: 'SHORTS', label: 'Shorts' },
  { value: 'JACKET', label: 'Jacket' },
  { value: 'TRENCHCOAT', label: 'Trenchcoat' },
  { value: 'COAT', label: 'Coat' },
  { value: 'BLOUSE', label: 'Blouse' },
  { value: 'HAT', label: 'Hat' },
  { value: 'SHOES', label: 'Shoes' },
  { value: 'BOOTS', label: 'Boots' },
  { value: 'HIGH_HEELS', label: 'High Heels' },
  { value: 'BAG', label: 'Bag' },
  { value: 'HANDBAG', label: 'Handbag' },
  { value: 'PURSE', label: 'Purse' },
];

export function clothingItemLabel(item: ClothingItem): string {
  return CLOTHING_ITEM_OPTIONS.find((o) => o.value === item)?.label ?? item;
}

export interface OutfitSuggestionRequest {
  gender: Gender;
  size: Size;
  clothes: ClothingItem[];
  styles: Style[];
  minPrice: number;
  maxPrice: number;
  additionalComments: string;
  additionalImages: File[];
}

export interface Suggestion {
  category: ClothingItem;
  name: string | null;
  link: string | null;
  price: string | null;
  picture: string | null;
  message: string | null;
}

export interface CategorySuggestions {
  category: ClothingItem;
  options: Suggestion[];
  message: string | null;
}

export interface OutfitSuggestionResponse {
  categories: CategorySuggestions[];
}

export interface OutfitImageRequest {
  gender: Gender;
  suggestions: Suggestion[];
}

export interface OutfitImageResponse {
  imageBase64: string;
  mimeType: string;
  message: string | null;
}
