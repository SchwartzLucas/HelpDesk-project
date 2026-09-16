import { Component, computed, inject, signal } from '@angular/core';
import { TimerService } from '../../core/services/timer.service';
import { formatDuration } from '../../core/utils';

@Component({
  standalone: true,
  imports: [],
  selector: 'app-timer-widget',
  templateUrl: './timer-widget.html',
  styleUrl: './timer-widget.scss',
})
export class TimerWidget {
  private timer = inject(TimerService);

  readonly active = this.timer.active;
  readonly visible = computed(() => this.active() !== null);
  readonly running = computed(() => this.active()?.status === 'RUNNING');
  readonly elapsed = computed(() => {
    const e = this.active();
    if (!e) return '00:00:00';
    if (e.status === 'RUNNING') return formatDuration(e.durationSeconds + this.timer.tick());
    return formatDuration(e.durationSeconds);
  });

  async pause() {
    const e = this.active();
    if (e) await this.timer.pause(e.ticketPublicId);
  }

  async stop() {
    const e = this.active();
    if (e) await this.timer.stop(e.ticketPublicId);
  }

  async resume() {
    const e = this.active();
    if (e) await this.timer.start(e.ticketPublicId);
  }
}