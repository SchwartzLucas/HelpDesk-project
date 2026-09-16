import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DashboardGroup, DashboardOverview, TeamDashboardRow, Ticket } from '../models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  overview() {
    return this.http.get<DashboardOverview>('/api/dashboard/overview');
  }

  ticketsByPeriod(period: string) {
    return this.http.get<Ticket[]>('/api/dashboard/tickets', { params: { period } });
  }

  byClient() {
    return this.http.get<DashboardGroup[]>('/api/dashboard/by-client');
  }

  byTeam() {
    return this.http.get<TeamDashboardRow[]>('/api/dashboard/by-team');
  }
}