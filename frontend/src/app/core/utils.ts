export const STATUS_LABELS: Record<number, string> = {
  1: 'Não iniciado',
  2: 'Em andamento',
  3: 'Pausado',
  4: 'Concluído',
  5: 'Fechado',
};

export const STATUS_CLASS: Record<number, string> = {
  1: 'status-todo',
  2: 'status-progress',
  3: 'status-paused',
  4: 'status-done',
  5: 'status-closed',
};

export const PRIORITY_LABELS: Record<number, string> = {
  0: 'Sem prioridade',
  1: 'Normal',
  2: 'Média',
  3: 'Urgente',
  4: 'Crítica',
};

export const PRIORITY_CLASS: Record<number, string> = {
  0: 'pri-0',
  1: 'pri-1',
  2: 'pri-2',
  3: 'pri-3',
  4: 'pri-4',
};

export const ROLE_LABELS: Record<string, string> = {
  ADMIN_USER: 'Admin',
  SUPPORT_USER: 'Suporte',
  COMMON_USER: 'Usuário',
};

export function formatDuration(totalSeconds: number): string {
  if (!totalSeconds || totalSeconds < 0) return '00:00:00';
  const h = Math.floor(totalSeconds / 3600);
  const m = Math.floor((totalSeconds % 3600) / 60);
  const s = Math.floor(totalSeconds % 60);
  return [h, m, s].map((n) => String(n).padStart(2, '0')).join(':');
}

export function formatDurationShort(totalSeconds: number): string {
  if (!totalSeconds || totalSeconds < 60) return '0m';
  const h = Math.floor(totalSeconds / 3600);
  const m = Math.floor((totalSeconds % 3600) / 60);
  if (h > 0) return `${h}h ${m > 0 ? m + 'm' : ''}`;
  return `${m}m`;
}

export function formatDate(value: string | null | undefined): string {
  if (!value) return '—';
  const d = new Date(value);
  return d.toLocaleDateString('pt-BR');
}

export function formatDateShort(value: string | null | undefined): string {
  if (!value) return '—';
  const d = new Date(value);
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
}

export function daysUntil(deadline: string | null | undefined): number | null {
  if (!deadline) return null;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const due = new Date(deadline);
  due.setHours(0, 0, 0, 0);
  return Math.round((due.getTime() - today.getTime()) / 86400000);
}

export function isDone(status: number | undefined): boolean {
  return !!status && status >= 4;
}

export function isOverdue(deadline: string | null | undefined, status: number | undefined): boolean {
  if (!deadline || isDone(status)) return false;
  const due = new Date(deadline);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return due.getTime() < today.getTime();
}

export interface TicketFilter {
  status?: number | null;
  category?: number | null;
  priority?: number | null;
  client_id?: number | null;
  team_id?: number | null;
  responsable_id?: number | null;
  deadline_from?: string | null;
  deadline_to?: string | null;
  title?: string | null;
  period?: string | null;
}