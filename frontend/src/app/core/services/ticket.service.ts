import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Ticket, TicketCreatePayload, TicketUpdatePayload } from '../models';
import { TicketFilter } from '../utils';

@Injectable({ providedIn: 'root' })
export class TicketService {
  constructor(private http: HttpClient) {}

  list(filter?: TicketFilter) {
    let params = new HttpParams();
    if (filter) {
      const put = (key: string, val: unknown) => {
        if (val !== null && val !== undefined && String(val) !== '') params = params.set(key, String(val));
      };
      put('status', filter.status);
      put('category', filter.category);
      put('priority', filter.priority);
      put('client_id', filter.client_id);
      put('team_id', filter.team_id);
      put('responsable_id', filter.responsable_id);
      put('deadline_from', filter.deadline_from);
      put('deadline_to', filter.deadline_to);
      put('title', filter.title);
      put('period', filter.period);
    }
    return this.http.get<Ticket[]>('/api/ticket/list', { params });
  }

  create(payload: TicketCreatePayload) {
    return this.http.post<Ticket[]>('/api/ticket/create', payload);
  }

  update(payload: TicketUpdatePayload) {
    return this.http.post<Ticket[]>('/api/ticket/update', payload);
  }
}