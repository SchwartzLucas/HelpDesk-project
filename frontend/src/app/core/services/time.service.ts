import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TimeEntry } from '../models';

@Injectable({ providedIn: 'root' })
export class TimeService {
  constructor(private http: HttpClient) {}

  start(ticketPublicId: string) {
    return this.http.post<TimeEntry>('/api/times/start', { ticketId: ticketPublicId });
  }

  pause(ticketPublicId: string) {
    return this.http.post<TimeEntry>('/api/times/pause', { ticketId: ticketPublicId });
  }

  resume(ticketPublicId: string) {
    return this.http.post<TimeEntry>('/api/times/resume', { ticketId: ticketPublicId });
  }

  stop(ticketPublicId: string) {
    return this.http.post<TimeEntry>('/api/times/stop', { ticketId: ticketPublicId });
  }

  active() {
    return this.http.get<TimeEntry>('/api/times/active');
  }

  byTicket(ticketPublicId: string) {
    return this.http.get<TimeEntry[]>(`/api/times/ticket/${ticketPublicId}`);
  }
}