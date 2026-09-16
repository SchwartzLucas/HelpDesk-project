export type Role = 'ADMIN_USER' | 'SUPPORT_USER' | 'COMMON_USER';

export interface LoginResponse {
  token: string;
  id: number;
  publicId: string;
  name: string;
  login: string;
  role: Role;
  teamId: number | null;
  clientId: number | null;
}

export interface User {
  id: string;
  internalId: number;
  publicCode: string;
  name: string;
  login: string;
  role: Role;
  teamId: number | null;
  teamName: string | null;
  clientId: number | null;
}

export interface Client {
  id: string;
  internalId: number;
  publicCode: string;
  name: string;
  email: string;
}

export interface Team {
  id: string;
  internalId: number;
  publicCode: string;
  name: string;
  description: string;
  managerId: number;
  managerName: string;
  memberCount: number;
}

export interface Ticket {
  id: string;
  publicCode: string;
  title: string;
  description: string;
  priority: number;
  status: number;
  category: number;
  clientId: number;
  clientName: string;
  teamId: number | null;
  teamName: string;
  responsableId: number | null;
  responsableName: string;
  slaExpiration: string;
  deadline: string | null;
  totalSeconds: number;
  createdDate: string;
  updatedDate: string;
}

export interface TimeEntry {
  id: string;
  ticketId: number;
  ticketPublicId: string;
  ticketTitle: string;
  ticketPublicCode: string;
  userId: number;
  userName: string;
  startedAt: string;
  endedAt: string | null;
  durationSeconds: number;
  status: 'RUNNING' | 'STOPPED' | 'PAUSED';
}

export interface DashboardOverview {
  today: number;
  week: number;
  month: number;
  overdue: number;
  inProgress: number;
  done: number;
  total: number;
}

export interface DashboardGroup {
  name: string;
  id: number;
  count: number;
  done: number;
  overdue: number;
  tickets: Ticket[];
}

export interface TeamDashboardRow {
  teamName: string;
  teamId: number | null;
  members: DashboardGroup[];
}

export interface TicketCreatePayload {
  title: string;
  description: string;
  category?: number;
  priority?: number;
  clientId?: number;
  teamId?: number | null;
  responsableId?: number | null;
  deadline?: string | null;
}

export interface TicketUpdatePayload {
  id: string;
  title?: string;
  description?: string;
  category?: number;
  priority?: number;
  status?: number;
  teamId?: number | null;
  responsableId?: number | null;
  deadline?: string | null;
}