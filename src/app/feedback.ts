import { Injectable, signal } from '@angular/core';

export type FeedbackKind = 'success' | 'error' | 'info';
export interface FeedbackMessage { text: string; kind: FeedbackKind; }

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  readonly current = signal<FeedbackMessage | null>(null);
  private timeout?: ReturnType<typeof setTimeout>;

  success(text: string): void { this.show(text, 'success'); }
  error(text: string): void { this.show(text, 'error'); }
  info(text: string): void { this.show(text, 'info'); }
  dismiss(): void { if (this.timeout) clearTimeout(this.timeout); this.current.set(null); }

  private show(text: string, kind: FeedbackKind): void {
    if (this.timeout) clearTimeout(this.timeout);
    this.current.set({ text, kind });
    this.timeout = setTimeout(() => this.current.set(null), 4500);
  }
}
