export function errorMessage(err: unknown, task: string): string {
  const status = (err as { status?: number })?.status;
  if (status === 429) {
    return "You've hit the generation limit. Please wait a while and try again.";
  }

  return `Something went wrong while ${task}. Please try again.`;
}
