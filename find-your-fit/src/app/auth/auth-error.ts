import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from './dto/api-error';

const FALLBACK_MESSAGE = 'Something went wrong. Please try again.';

export function toAuthErrorMessage(err: unknown): string {
  if (!(err instanceof HttpErrorResponse)) {
    return FALLBACK_MESSAGE;
  }

  switch (err.status) {
    case 0:
      return 'Please check your connection.';
    case 401:
    case 404:
      return 'Invalid email or password.';
    case 429:
      return 'Too many attempts. Please wait a few minutes and try again.';
    default: {
      const body = err.error as ApiError | null;
      return body?.message ?? FALLBACK_MESSAGE;
    }
  }
}
