export interface LookSummary {
  id: number;
  imageUrl: string;
  published: boolean;
}

export interface LooksResponse {
  looks: LookSummary[];
}

// Mirrors backend ClientNameResponse (GET /api/user/full-name).
export interface ClientNameResponse {
  fullName: string;
}
