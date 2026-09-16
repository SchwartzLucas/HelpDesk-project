import { Injectable, computed, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { TimeEntry } from '../models';
import { TimeService } from './time.service';

@Injectable({ providedIn: 'root' })
export class TimerService {
  readonly active = signal<TimeEntry | null>(null);
  readonly tick = signal(0);
  readonly running = computed(() => this.active()?.status === 'RUNNING');

  private interval: ReturnType<typeof setInterval> | null = null;

  constructor(private timeService: TimeService) {}

  async load() {
    try {
      const entry = await firstValueFrom(this.timeService.active());
      this.setActive(entry);
    } catch {
      this.setActive(null);
    }
  }

  async start(ticketPublicId: string) {
    const entry = await firstValueFrom(this.timeService.start(ticketPublicId));
    this.setActive(entry);
    return entry;
  }

  async pause(ticketPublicId: string) {
    const entry = await firstValueFrom(this.timeService.pause(ticketPublicId));
    this.setActive(entry);
    return entry;
  }

  async resume(ticketPublicId: string) {
    return this.start(ticketPublicId);
  }

  async stop(ticketPublicId: string) {
    const entry = await firstValueFrom(this.timeService.stop(ticketPublicId));
    this.setActive(entry);
    return entry;
  }

  private setActive(entry: TimeEntry | null) {
    this.active.set(entry ?? null);
    this.stopTicker();
    this.tick.set(0);
    if (this.active()?.status === 'RUNNING') {
      this.interval = setInterval(() => this.tick.update((v) => v + 1), 1000);
    }
  }

  private stopTicker() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null;
    }
  }
}