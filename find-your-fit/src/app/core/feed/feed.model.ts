import { Gender, Style } from '@shared/models/outfit.model';
import { LookSummary } from '@shared/models/look-card.model';

export interface FeedResponse {
  looks: LookSummary[];
  totalElements: number;
  totalPages: number;
}

export interface FeedFilters {
  gender: Gender | null;
  styles: Style[];
  minBudget: number | null;
  maxBudget: number | null;
}

export const EMPTY_FILTERS: FeedFilters = {
  gender: null,
  styles: [],
  minBudget: null,
  maxBudget: null,
};
