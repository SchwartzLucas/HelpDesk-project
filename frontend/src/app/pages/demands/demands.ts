import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Client, Team, Ticket, User } from '../../core/models';
import { TicketService } from '../../core/services/ticket.service';
import { ClientService } from '../../core/services/client.service';
import { TeamService } from '../../core/services/team.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { TimerService } from '../../core/services/timer.service';
import { DemandModal } from '../../components/demand-modal/demand-modal';
import {
  PRIORITY_CLASS,
  PRIORITY_LABELS,
  STATUS_CLASS,
  STATUS_LABELS,
  daysUntil,
  formatDateShort,
  formatDuration,
  formatDurationShort,
  isDone,
  isOverdue,
} from '../../core/utils';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, DemandModal],
  selector: 'app-demands',
  templateUrl: './demands.html',
  styleUrl: './demands.scss',
})
export class DemandsPage implements OnInit {
  private route = inject(ActivatedRoute);
  private ticketService = inject(TicketService);
  private clientService = inject(ClientService);
  private teamService = inject(TeamService);
  private userService = inject(UserService);
  private auth = inject(AuthService);
  protected timer = inject(TimerService);

  readonly mine = this.route.snapshot.data['mine'] !== false;

  tickets = signal<Ticket[]>([]);
  loading = signal(false);

  clients = signal<Client[]>([]);
  teams = signal<Team[]>([]);
  users = signal<User[]>([]);

  search = signal('');
  statusFilter = signal<number | null>(null);
  priorityFilter = signal<number | null>(null);
  overdueOnly = signal(false);

  private modal!: DemandModal;

  readonly pageTitle = computed(() => (this.mine ? 'Minhas demandas' : 'Todas as demandas'));
  readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    let list = this.tickets();
    if (q) list = list.filter((t) => t.title.toLowerCase().includes(q) || t.publicCode.toLowerCase().includes(q));
    if (this.priorityFilter() != null) list = list.filter((t) => t.priority === this.priorityFilter());
    if (this.overdueOnly()) list = list.filter((t) => isOverdue(t.deadline, t.status));
    return list;
  });

  readonly statusLabels = STATUS_LABELS;
  readonly statusClass = STATUS_CLASS;
  readonly priorityLabels = PRIORITY_LABELS;
  readonly priorityClass = PRIORITY_CLASS;
  readonly statusOptions = [1, 2, 3, 4, 5];
  readonly priorityOptions = [0, 1, 2, 3, 4];

  async ngOnInit() {
    await Promise.all([this.loadLists(), this.reload()]);
    await this.timer.load();
  }

  onModalReady(modal: DemandModal) {
    this.modal = modal;
    modal.closed.subscribe(() => this.reload());
    modal.saved.subscribe(() => this.reload());
  }

  openCreate() {
    this.modal?.openCreate();
  }

  openEdit(ticket: Ticket) {
    this.modal?.openEdit(ticket);
  }

  async reload() {
    this.loading.set(true);
    try {
      const myId = this.auth.user()?.id ?? null;
      const tickets = await firstValueFrom(
        this.ticketService.list({
          status: this.statusFilter(),
          priority: this.priorityFilter(),
          responsable_id: this.mine ? myId : null,
        }),
      );
      this.tickets.set(tickets);
    } finally {
      this.loading.set(false);
    }
  }

  clearFilters() {
    this.search.set('');
    this.statusFilter.set(null);
    this.priorityFilter.set(null);
    this.overdueOnly.set(false);
    this.reload();
  }

  async changeStatus(ticket: Ticket, status: number) {
    if (ticket.status === status) return;
    await firstValueFrom(
      this.ticketService.update({ id: ticket.id, status }),
    );
    this.reload();
  }

  // ---- timer ----
  async startTimer(ticket: Ticket) {
    await this.timer.start(ticket.id);
    this.reload();
  }

  async pauseTimer(ticket: Ticket) {
    await this.timer.pause(ticket.id);
    this.reload();
  }

  async stopTimer(ticket: Ticket) {
    await this.timer.stop(ticket.id);
    this.reload();
  }

  isRunningOn(ticket: Ticket): boolean {
    const a = this.timer.active();
    return !!a && a.status === 'RUNNING' && a.ticketPublicId === ticket.id;
  }

  isPausedOn(ticket: Ticket): boolean {
    const a = this.timer.active();
    return !!a && a.status !== 'RUNNING' && a.ticketPublicId === ticket.id;
  }

  remaining(ticket: Ticket): string {
    const d = daysUntil(ticket.deadline);
    if (d === null) return '';
    if (d < 0) return `${-d}d atrasado`;
    if (d === 0) return 'Vence hoje';
    return `${d}d`;
  }

  statusOf(ticket: Ticket): string {
    return this.statusLabels[ticket.status] ?? `Status ${ticket.status}`;
  }

  classOf(ticket: Ticket): string {
    return this.statusClass[ticket.status] ?? 'status-todo';
  }

  priLabel(ticket: Ticket): string {
    return this.priorityLabels[ticket.priority] ?? '';
  }

  priClass(ticket: Ticket): string {
    return this.priorityClass[ticket.priority] ?? 'pri-0';
  }

  fmtDuration(s: number): string {
    return formatDuration(s);
  }

  fmtDurationShort(s: number): string {
    return formatDurationShort(s);
  }

  fmtDate(v: string | null): string {
    return formatDateShort(v);
  }

  overdue(ticket: Ticket): boolean {
    return isOverdue(ticket.deadline, ticket.status);
  }

  done(ticket: Ticket): boolean {
    return isDone(ticket.status);
  }

  private async loadLists() {
    try {
      this.clients.set(await firstValueFrom(this.clientService.list()));
    } catch {
      // opcional
    }
    try {
      this.teams.set(await firstValueFrom(this.teamService.list()));
    } catch {
      // opcional
    }
    try {
      this.users.set(await firstValueFrom(this.userService.list()));
    } catch {
      // usuário comum: apenas ele mesmo como responsável possível
      const me = this.auth.user();
      if (me) {
        this.users.set([
          {
            id: me.publicId,
            internalId: me.id,
            publicCode: '',
            name: me.name,
            login: me.login,
            role: me.role,
            teamId: me.teamId,
            teamName: null,
            clientId: me.clientId,
          },
        ]);
      }
    }
  }
}