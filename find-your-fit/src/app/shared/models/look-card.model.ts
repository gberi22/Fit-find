import { ClothingItem, Gender, Size, Style } from '@shared/models/outfit.model';

export interface LookSummary {
  id: number;
  imageUrl: string;
  published: boolean;
}

export interface LooksResponse {
  looks: LookSummary[];
}

export interface ClientNameResponse {
  fullName: string;
}

export interface ProductResponse {
  name: string | null;
  url: string | null;
  price: string | null;
  category: ClothingItem;
  imageUrl: string | null;
}

export interface LookDetailResponse {
  id: number;
  imageUrl: string;
  username: string;
  styles: Style[];
  gender: Gender;
  size: Size;
  budgetMin: number;
  budgetMax: number;
  published: boolean;
  createdAt: string;
  publishedAt: string | null;
  products: ProductResponse[];
}
