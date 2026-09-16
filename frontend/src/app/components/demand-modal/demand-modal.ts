import { Component, EventEmitter, Input, OnInit, Output, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Client, Team, Ticket, User } from '../../core/models';
import { STATUS_LABELS, PRIORITY_LABELS } from '../../core/utils';
import { TicketService } from '../../core/services/ticket.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  selector: 'app-demand-modal',
  templateUrl: './demand-modal.html',
  styleUrl: './demand-modal.scss',
})
export class DemandModal implements OnInit {
  private ticketService = inject(TicketService);

  @Input() clients: Client[] = [];
  @Input() teams: Team[] = [];
  @Input() users: User[] = [];

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();
  @Output() ready = new EventEmitter<DemandModal>();

  open = signal(false);
  editMode = signal(false);
  saving = signal(false);
  error = signal('');

  ngOnInit() {
    this.ready.emit(this);
  }

  title = '';
  description = '';
  clientId: number | null = null;
  teamId: number | null = null;
  responsableId: number | null = null;
  priority = 1;
  status = 1;
  deadline = '';

  private editingTicket: Ticket | null = null;
  readonly statusLabels = STATUS_LABELS;
  readonly priorityLabels = PRIORITY_LABELS;
  readonly priorityOptions = [0, 1, 2, 3, 4];

  openCreate() {
    this.editingTicket = null;
    this.editMode.set(false);
    this.resetFields();
    this.open.set(true);
  }

  openEdit(ticket: Ticket) {
    this.editingTicket = ticket;
    this.editMode.set(true);
    this.title = ticket.title;
    this.description = ticket.description;
    this.clientId = ticket.clientId ?? null;
    this.teamId = ticket.teamId ?? null;
    this.responsableId = ticket.responsableId ?? null;
    this.priority = ticket.priority ?? 1;
    this.status = ticket.status ?? 1;
    this.deadline = this.toLocalInput(ticket.deadline);
    this.open.set(true);
  }

  close() {
    if (this.saving()) return;
    this.open.set(false);
    this.closed.emit();
  }

  async save() {
    if (!this.title.trim()) {
      this.error.set('Informe o título da demanda.');
      return;
    }
    this.saving.set(true);
    this.error.set('');
    try {
      if (this.editMode() && this.editingTicket) {
        await new Promise<void>((resolve, reject) => {
          this.ticketService
            .update({
              id: this.editingTicket!.id,
              title: this.title.trim(),
              description: this.description.trim(),
              category: 1,
              priority: this.priority,
              status: this.status,
              teamId: this.teamId,
              responsableId: this.responsableId,
              deadline: this.fromLocalInput(this.deadline),
            })
            .subscribe({ next: () => resolve(), error: reject });
        });
      } else {
        await new Promise<void>((resolve, reject) => {
          this.ticketService
            .create({
              title: this.title.trim(),
              description: this.description.trim(),
              category: 1,
              priority: this.priority,
              clientId: this.clientId ?? undefined,
              teamId: this.teamId ?? null,
              responsableId: this.responsableId ?? null,
              deadline: this.fromLocalInput(this.deadline),
            })
            .subscribe({ next: () => resolve(), error: reject });
        });
      }
      this.open.set(false);
      this.saved.emit();
    } catch {
      this.error.set('Não foi possível salvar. Tente novamente.');
      this.saving.set(false);
    }
  }

  private resetFields() {
    this.title = '';
    this.description = '';
    this.clientId = this.clients.length ? this.clients[0].internalId : null;
    this.teamId = null;
    this.responsableId = null;
    this.priority = 1;
    this.status = 1;
    this.deadline = '';
    this.error.set('');
  }

  private toLocalInput(iso: string | null | undefined): string {
    if (!iso) return '';
    const d = new Date(iso);
    if (isNaN(d.getTime())) return '';
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  private fromLocalInput(v: string): string | null {
    if (!v) return null;
    const d = new Date(v);
    return isNaN(d.getTime()) ? null : d.toISOString();
  }
}