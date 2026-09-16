import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardGroup, DashboardOverview, TeamDashboardRow, Ticket } from '../../core/models';
import { DashboardService } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';
import { TimerService } from '../../core/services/timer.service';
import {
  PRIORITY_CLASS,
  PRIORITY_LABELS,
  STATUS_CLASS,
  STATUS_LABELS,
  daysUntil,
  formatDate,
  formatDateShort,
  formatDuration,
  isDone,
  isOverdue,
} from '../../core/utils';

type Tab = 'hoje' | 'semana' | 'mes' | 'cliente' | 'time';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private dashService = inject(DashboardService);
  private auth = inject(AuthService);
  protected timer = inject(TimerService);

  readonly overview = signal<DashboardOverview | null>(null);
  readonly activeTab = signal<Tab>('hoje');

  readonly periodTickets = signal<Ticket[]>([]);
  readonly byClient = signal<DashboardGroup[]>([]);
  readonly byTeam = signal<TeamDashboardRow[]>([]);
  readonly loaded = new Set<Tab>();

  readonly userName = computed(() => this.auth.user()?.name ?? 'Pessoa');
  readonly greeting = computed(() => {
    const h = new Date().getHours();
    if (h < 12) return 'Bom dia';
    if (h < 18) return 'Boa tarde';
    return 'Boa noite';
  });

  readonly statusLabels = STATUS_LABELS;
  readonly statusClass = STATUS_CLASS;
  readonly priorityLabels = PRIORITY_LABELS;
  readonly priorityClass = PRIORITY_CLASS;

  async ngOnInit() {
    this.dashService.overview().subscribe((r) => this.overview.set(r));
    this.loadTab('hoje');
    await this.timer.load();
  }

  async selectTab(tab: Tab) {
    this.activeTab.set(tab);
    this.loadTab(tab);
  }

  async loadTab(tab: Tab) {
    if (this.loaded.has(tab)) return;
    this.loaded.add(tab);
    if (tab === 'cliente') {
      this.dashService.byClient().subscribe((r) => this.byClient.set(r));
    } else if (tab === 'time') {
      this.dashService.byTeam().subscribe((r) => this.byTeam.set(r));
    } else {
      const periodMap: Record<Tab, string> = { hoje: 'today', semana: 'week', mes: 'month', cliente: '', time: '' };
      this.dashService.ticketsByPeriod(periodMap[tab]).subscribe((r) => this.periodTickets.set(r));
    }
  }

  refreshTab() {
    this.loaded.clear();
    this.loadTab(this.activeTab());
    this.dashService.overview().subscribe((r) => this.overview.set(r));
  }

  // helpers
  fmtDate(v: string | null) { return formatDateShort(v); }
  fmtDuration(s: number) { return formatDuration(s); }
  statusOf(s: number) { return this.statusLabels[s] ?? ''; }
  classOf(s: number) { return this.statusClass[s] ?? 'status-todo'; }
  priLabel(p: number) { return this.priorityLabels[p] ?? ''; }
  priClass(p: number) { return this.priorityClass[p] ?? 'pri-0'; }
  isDone(t: Ticket) { return isDone(t.status); }
  isOverdue(t: Ticket) { return isOverdue(t.deadline, t.status); }

  async startTimer(ticket: Ticket) { await this.timer.start(ticket.id); this.refreshTab(); }
  async pauseTimer(ticket: Ticket) { await this.timer.pause(ticket.id); this.refreshTab(); }
  async stopTimer(ticket: Ticket) { await this.timer.stop(ticket.id); this.refreshTab(); }

  isRunningOn(ticket: Ticket): boolean {
    const a = this.timer.active();
    return !!a && a.status === 'RUNNING' && a.ticketPublicId === ticket.id;
  }
  isPausedOn(ticket: Ticket): boolean {
    const a = this.timer.active();
    return !!a && a.status !== 'RUNNING' && a.ticketPublicId === ticket.id;
  }
  remaining(t: Ticket) {
    const d = daysUntil(t.deadline);
    if (d === null) return '';
    if (d < 0) return `${-d}d atrasado`;
    if (d === 0) return 'Hoje';
    return `${d}d`;
  }
}