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
